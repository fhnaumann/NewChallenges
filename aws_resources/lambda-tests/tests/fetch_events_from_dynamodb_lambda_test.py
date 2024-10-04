import unittest
import boto3
import time
import json
from moto import mock_aws
from .utils import load_scenario, create_dynamodb_table
from lambdas.fetch_events_from_dynamodb_lambda_handler import lambda_handler

@mock_aws
class FetchEventsFromDynamoDBLambdaTest(unittest.TestCase):

	def setUp(self):
		self.dynamodb = boto3.resource('dynamodb', region_name='us-east-1')
		self.s3 = boto3.client('s3', region_name='us-east-1')
		self.table_name = 'challenge-events-test'
		self.bucket_name = 'existing-challenges-test'

		self.table = create_dynamodb_table(self.dynamodb, self.table_name)
		self.s3.create_bucket(Bucket=self.bucket_name)

		challenge_file, mc_events = load_scenario('fetch_events', 'block_break_goal', True, time.time_ns())
		self.challenge_ID = challenge_file["metadata"]["challengeID"]

		self.s3.put_object(
			Bucket=self.bucket_name,
			Key=f"{self.challenge_ID}.json",
			Body=json.dumps(challenge_file).encode('utf-8')
		)

		for mc_event in mc_events:
			self.table.put_item(Item=mc_event)

		self.fake_lambda_event_object = fake_fetch_lambda_event_object(self.challenge_ID)

	def test_mc_events_are_fetched(self):
		lambda_result = lambda_handler(self.fake_lambda_event_object, {})
		self.assertEqual(lambda_result["statusCode"], 200)

def fake_fetch_lambda_event_object(challenge_ID: str):
	
	return {
		"stageVariables": {
			"ENV": "testing"
		},
		"queryStringParameters": {
			"challenge_ID": challenge_ID
		}
	}
