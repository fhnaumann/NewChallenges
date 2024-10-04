import json
import os
from typing import Literal

LambdaType = Literal['fetch_events']
ScenarioType = Literal['block_break_goal']

def load_event_json_file(lambda_type: LambdaType, filename: str, existing_events, last_modified=0, modifier_type='server-event'):
	filetype = 'existing_events' if existing_events else 'live_events'
	filepath = os.path.join('resources', lambda_type, f'{filename}.{filetype}.json')
	script_dir = os.path.dirname(__file__)
	abs_path = os.path.join(script_dir, filepath)
	with open(abs_path, 'r') as file:
		mc_events = json.load(file)
		if existing_events:
			for mc_event in mc_events:
				mc_event["lastModifier"] = last_modified
				mc_event["modifierType"] = modifier_type
				mc_event["timestamp#eventID"] = f"{mc_event["timestamp"]}#{mc_event["eventID"]}"
	return mc_events

def load_challenge_file(lambda_type: LambdaType, filename: str):
	filepath = os.path.join('resources', lambda_type, f'{filename}.challenge.json')
	script_dir = os.path.dirname(__file__)
	abs_path = os.path.join(script_dir, filepath)
	with open(abs_path, 'r') as file:
		challenge_file = json.load(file)
	return challenge_file

def load_scenario(lambda_type: LambdaType, scenario_type: ScenarioType, existing_events=True, last_modified=0, modifier_type='server-event'):
	challenge_file = load_challenge_file(lambda_type, scenario_type)
	mc_events = load_event_json_file(lambda_type, scenario_type, existing_events, last_modified, modifier_type)
	return challenge_file, mc_events

def create_dynamodb_table(dynamodb, table_name: str):
	# Define a mocked DynamoDB table
	table = dynamodb.create_table(
		TableName=table_name,
		KeySchema=[
			{'AttributeName': 'challenge_ID', 'KeyType': 'HASH'},  # Partition key
			{'AttributeName': 'timestamp#eventID', 'KeyType': 'RANGE'}  # Sort key
		],
		AttributeDefinitions=[
			{'AttributeName': 'challenge_ID', 'AttributeType': 'S'},
			{'AttributeName': 'timestamp#eventID', 'AttributeType': 'S'}
		],
		ProvisionedThroughput={
			'ReadCapacityUnits': 10,
			'WriteCapacityUnits': 10
		}
	)
	table.meta.client.get_waiter('table_exists').wait(TableName=table_name)
	return table