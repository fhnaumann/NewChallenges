import json
import uuid
import boto3
import time
from botocore.exceptions import ClientError
from boto3.dynamodb.conditions import Key
from boto3.dynamodb.types import TypeDeserializer
from decimal import Decimal

dynamodb = boto3.resource('dynamodb')
s3_client = boto3.client('s3')

access_coll_entry_config_in_goals = {
    "blockPlaceGoal": "placed",
    "blockBreakGoal": "broken",
    "mobGoal": "mobs",
    "itemGoal": "items",
    "deathGoal": "deathMessages",
    "craftingGoal": "crafted"
}
access_event_data = {
    "blockPlaceGoal": "placed",
    "blockBreakGoal": "broken",
    "mobGoal": "mob",
    "itemGoal": "item",
    "deathGoal": "deathMessageKey",
    "craftingGoal": "recipeCrafted"
}
# directly copied from typescript interface definition
default_coll_data_config = {
    "amountNeeded": 1,
    "currentAmount": 0,
    "completion": {
        "whenCollectedSeconds": -1
    }
    
}

def lambda_handler(event, context):
    print(event)
    print(context)
    stage = event["stageVariables"]["ENV"]
    tablename = ""
    if stage == 'production':
        tablename = 'challenge-events'
    else:
        tablename = f'challenge-events-{stage}'
    
    print(tablename)
    table = dynamodb.Table(tablename)
    
    
    challengeID = event.get('queryStringParameters', {}).get('challenge_ID', None)
    if challengeID is None:
        return {
            'statusCode': 400,
            'body': json.dumps({'error': 'challengeID not provided or empty!'})
        }
    try:

        as_json_items = fetch_events_from_dynamodb(table, challengeID)
        if as_json_items and as_json_items[-1].get('modifierType', '') == 'artificial' and as_json_items[-1].get("lastModified", 0) - time.time_ns() < int(5e9):
            # The last write for the given challenge ID was to sync them back up. Prevent race condition by waiting 5 seconds.
            time.sleep(5)
        
        if stage != 'production': # not yet prod ready, requires testing
            # Perform sync check by verifying that challenge file matches the event. Otherwise handle the desync by adding artificial events.
            # 1 create lookup map from s3 file
            # 2 create lookup map from dynamodb events
            # 3 calculate diff between maps
            # 4 Either delete events or artificially add events based of diff
            # Lookup map structure:
            #{
            #  blockBreakGoal: {
            #    stone: 1,
            #    dirt: 4
            #  }
            #  mobGoal: {
            #    pig: 5  
            #  }
            #}
            challenge_file_response = s3_client.get_object(Bucket=f"existing-challenges-{stage}", Key=f"{challengeID}.json")
            model = json.load(challenge_file_response['Body'].read())
                    
            challenge_file_lookup_map = create_lookup_map_from_challenge_file(model)
            mc_events_lookup_map = create_lookup_map_from_mc_events(as_json_items)
            diff = calculate_diff_between_challenge_file_and_mc_events(challenge_file_lookup_map, mc_events_lookup_map)
            events_to_remove, artificial_events = handle_diff_between_challenge_file_and_mc_events(diff, challengeID, as_json_items)
            for event_idx_to_remove in events_to_remove:
                to_delete = as_json_items[event_idx_to_remove]
                delete_response = table.delete_item(
                    Key={
                        'challenge_ID': challengeID,
                        'timestamp#eventID': f'{to_delete["timestamp"]}#{to_delete["eventID"]}'
                    })
                del to_delete
            as_json_items.extend(artificial_events)
            for artificial_event in artificial_events:
                artificial_event_item = {
                    'challenge_ID': challengeID,
                    'timestamp#eventID': f'{artificial_event["timestamp"]}#{artificial_event["eventID"]}',
                    'eventID': artificial_event["eventID"],
                    'timestamp': artificial_event["timestamp"],
                    'eventType': artificial_event["eventType"],
                    'data': artificial_event["data"],
                    'lastModified': time.time_ns(),
                    'modifierType': 'artificial'
                }
                table.put_item(Item=artificial_event_item)
        
        # TODO: Handle race condition when two people trigger this lambda (by visiting the live frontend) so that it is not sanitized twice.
        # If this ever occurres, it would fix itself when the next person refreshes (just more data now). The only way this could blow up is,
        # if two (or more people) keep on refreshing the page every time.
        # Solution: Use locks (or see time out at the top of the file)
        
        return {
            'statusCode': 200,
            'body': json.dumps(as_json_items)
        }
    except ClientError as e:
        return {
            'statusCode': 500,
            'body': json.dumps({'error': 'Could not retrieve data', 'details': str(e)})
        }
        

def fetch_events_from_dynamodb(table, challengeID):
    # fetch events from dynamodb
    response = table.query(
        KeyConditionExpression=Key('challenge_ID').eq(challengeID))
    print("response from DynamoDB", response)
    items = response.get('Items', [])
    # convert to JSON
    as_json_items = []
    for item in items:
        del item["timestamp#eventID"]
        print("item", item)
        as_json = deserialize(item)
        as_json_items.append(as_json)
        print("deserialized", as_json)
    return as_json_items

def handle_diff_between_challenge_file_and_mc_events(diff, challenge_ID, mc_events):
    artificial_events = []
    events_to_remove = [] # list of indices used to index mc_events
    for goal_name, collectable_amount in diff.items():
        for code, amount in collectable_amount.items():
            if amount > 0:
                # some events are missing -> add artificial events
                artificial_events.append(create_artificial_event_for(goal_name, challenge_ID, code, amount))
            else:
                # too many events -> remove latest matching events
                events_to_remove, tooManyRemoved = remove_latest_matching_event_for(goal_name, code, amount, mc_events)
                if tooManyRemoved < 0:
                    artificial_events.append(create_artificial_event_for(goal_name, challenge_ID, code, abs(tooManyRemoved)))
    return events_to_remove, artificial_events


def create_artificial_event_for(goal_name, challenge_ID, code, amount):
    # It's not reconstructable when the missing event occurred
    timestamp = 0
    return {
        "challengeID": challenge_ID,
        "eventID": str(uuid.uuid4()),
        "timestamp": timestamp,
        "eventType": goal_name,
        "data": {
            "amount": amount,
            "timestamp": timestamp,
            "player": {
                "playerUUID": "",
                "playerName": "",
                "skinTextureURL": "http://textures.minecraft.net/texture/d34e063cafb467a5c8de43ec78619399f369f4a52434da8017a983cdd92516a0" # questionmark head
            }
        },
        "lastModified": time.time_ns(),
        "modifierType": 'artificial'
    }

def remove_latest_matching_event_for(goal_name, code, amount, mc_events):
    indicies_to_delete = []
    for index, mc_event in enumerate(reversed(mc_events)):
        event_type = mc_event.get('eventType', '')
        if event_type != goal_name:
            continue
        event_data = mc_event.get('data', {})
        code_in_existing_event = event_data.get(access_event_data[event_type], '')
        if code_in_existing_event != code:
            continue
        removing_amount = event_data["amount"]
        amount = amount - removing_amount
        indicies_to_delete.append(index)
        if amount <= 0:
            return indicies_to_delete, amount
    
    return indicies_to_delete, 0

def calculate_diff_between_challenge_file_and_mc_events(challenge_file_lookup_map, mc_events_lookup_map):
    diff = {}
    
    for goal_name, challenge_file_collectable_amount in challenge_file_lookup_map.items():
        diff[goal_name] = {}
        if not goal_name in mc_events_lookup_map.keys():
            diff[goal_name] = challenge_file_collectable_amount
        else:
            for challenge_file_code, challenge_file_amount in challenge_file_collectable_amount.items():
                mc_event_amount = mc_events_lookup_map[goal_name].get(challenge_file_code, 0)
                amount_diff = challenge_file_amount - mc_event_amount
                if amount_diff != 0:
                    diff[goal_name][challenge_file_code] = amount_diff
        
    return diff
        

def create_lookup_map_from_mc_events(mc_events):
    lookup_map = {}
    
    interested_event_types = access_event_data.keys()
    
    for mc_event in mc_events:
        event_type = mc_event.get('eventType', '')
        # only interested in event types that are goal keys
        if event_type not in interested_event_types:
            continue
        if event_type not in lookup_map:
            lookup_map[event_type] = {}
        
        event_data = mc_event.get('data', {})
        code = event_data.get(access_event_data[event_type], '')
        if code == '':
            print(f"ERROR; Missing code at {event_type}")
            return None
        lookup_map[event_type][code] = event_data.get('amount', 1)
        
    return lookup_map
        

def create_lookup_map_from_challenge_file(challenge_file):
    lookup_map = {}
    
    goals_config = challenge_file.get('goals', {})
    for goal_name, goal_config in goals_config.items():
        lookup_map[goal_name] = {}
        key_for_coll_entry_access = access_coll_entry_config_in_goals.get(goal_name, "")
        collectable_entry_configs = goals_config.get(key_for_coll_entry_access, [])
        for coll_entry_config in collectable_entry_configs:
            if 'collectableName' not in coll_entry_config:
                print(f'ERROR; Missing code at {key_for_coll_entry_access}')
                return None
            code = coll_entry_config['collectableName']
            coll_data_config = coll_entry_config.get('collectableData', default_coll_data_config)
            # amount_needed = coll_data_config.get('amountNeeded', default_coll_data_config["amountNeeded"])
            current_amount = coll_data_config.get('currentAmount', default_coll_data_config["currentAmount"])
            # when_collected_seconds = coll_data_config.get('completion', default_coll_data_config["completion"]).get('whenCollectedSeconds')
            
            lookup_map[goal_name][code] = current_amount
    
    return lookup_map
            
    
        
serializer = TypeDeserializer()

def deserialize(data):
    if isinstance(data, Decimal):
        return int(data)
    
    if isinstance(data, list):
        return [deserialize(v) for v in data]

    if isinstance(data, dict):
        try:
            return serializer.deserialize(data)
        except TypeError:
            return {k: deserialize(v) for k, v in data.items()}
    else:
        return data