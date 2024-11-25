import json
import os
import unittest
from decimal import Decimal
from typing import Literal

from boto3.dynamodb.types import TypeDeserializer

LambdaType = Literal['fetch_events']
ScenarioType = Literal[
	'regular_block_break_goal',
	'missing_events_block_break_goal',
	'partial_missing_events_block_break_goal',
	'too_many_events_block_break_goal',
	'too_many_events_too_many_removed_block_break_goal',
	'too_many_stone_too_many_removed_and_partial_missing_dirt_block_break_goal',
]

def load_event_json_file(lambda_type: LambdaType, scenario_type: ScenarioType, filename: str, existing_events, last_modified=0, modifier_type='server-event'):
	filetype = 'existing_events' if existing_events else 'live_events'
	filepath = os.path.join('resources', lambda_type, scenario_type, f'{filename}.{filetype}.json')
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

def load_challenge_file(lambda_type: LambdaType, scenario_type: ScenarioType, filename: str):
	filepath = os.path.join('resources', lambda_type, scenario_type, f'{filename}.challenge.json')
	script_dir = os.path.dirname(__file__)
	abs_path = os.path.join(script_dir, filepath)
	with open(abs_path, 'r') as file:
		challenge_file = json.load(file)
	return challenge_file

def load_scenario(lambda_type: LambdaType, scenario_type: ScenarioType, existing_events=True, last_modified=0, modifier_type='server-event'):
	challenge_file = load_challenge_file(lambda_type, scenario_type, scenario_type)
	mc_events = load_event_json_file(lambda_type, scenario_type, scenario_type, existing_events, last_modified, modifier_type)
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


def assert_same_events(expected, actual, code_key):
	test_case = unittest.TestCase()
	test_case.assertEqual(len(expected), len(actual), "Different amount of events")
	expected = sorted(expected, key=lambda x: (x[code_key], x["timestamp"]))
	actual = sorted(actual, key=lambda x: (x[code_key], x["timestamp"]))
	for expected_event, actual_event in zip(expected, actual):
		test_case.assertEqual(expected_event[code_key], actual_event[code_key])
		test_case.assertEqual(expected_event["amount"], actual_event["amount"])
		test_case.assertEqual(expected_event["player"], actual_event["player"])
		test_case.assertEqual(expected_event["timestamp"], actual_event["timestamp"])


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