import unittest
import boto3
import time
import json

from boto3.dynamodb.conditions import Key
from moto import mock_aws
from .utils import load_scenario, create_dynamodb_table, deserialize, assert_same_events, ScenarioType
from lambdas.fetch_events_from_dynamodb_lambda_handler import lambda_handler
import os


@mock_aws
class FetchEventsFromDynamoDBLambdaTest(unittest.TestCase):

    def setUp(self):
        self.longMessage = True
        os.environ['AWS_REGION'] = 'eu-central-1'

        self.dynamodb = boto3.resource('dynamodb', region_name='eu-central-1')
        self.s3 = boto3.client('s3', region_name='eu-central-1')
        self.table_name = 'challenge-events-testing'
        self.bucket_name = 'existing-challenges-testing'

        self.table = create_dynamodb_table(self.dynamodb, self.table_name)
        self.s3.create_bucket(Bucket=self.bucket_name, CreateBucketConfiguration={'LocationConstraint': 'eu-central-1'})

    def _individual_setup(self, scenario_type: ScenarioType):
        self.challenge_file, self.mc_events = load_scenario('fetch_events', scenario_type, True, time.time_ns())
        self.challenge_id = self.challenge_file["metadata"]["challengeID"]

        self.s3.put_object(
            Bucket=self.bucket_name,
            Key=f"{self.challenge_id}.json",
            Body=json.dumps(self.challenge_file).encode('utf-8')
        )
        for mc_event in self.mc_events:
            self.table.put_item(Item=mc_event)
        self.fake_lambda_event_object = fake_fetch_lambda_event_object(self.challenge_id)

    def test_mc_events_are_fetched(self):
        self._individual_setup('regular_block_break_goal')
        lambda_result = lambda_handler(self.fake_lambda_event_object, {})
        self.assertEqual(lambda_result["statusCode"], 200, lambda_result["body"])

    def test_missing_events(self):
        self._individual_setup('missing_events_block_break_goal')

        lambda_result = lambda_handler(self.fake_lambda_event_object, {})
        self.assertEqual(lambda_result["statusCode"], 200, lambda_result["body"])
        response = self.table.query(
            KeyConditionExpression=Key('challenge_ID').eq(self.challenge_id))
        items = response.get("Items", [])
        if len(items) == 0:
            self.fail("No artificial events where added.")
        json_items = [deserialize(item) for item in items]
        data_from_actual_events = [item["data"] for item in json_items]
        print(json_items)
        artificial_timestamp = 0
        artificial_player_config = {
            "playerUUID": "",
            "playerName": "?",
            "skinTextureURL": 'http://textures.minecraft.net/texture/d34e063cafb467a5c8de43ec78619399f369f4a52434da8017a983cdd92516a0'
        }
        expected = [
            {
                "broken": "stone",
                "amount": 1,
                "timestamp": artificial_timestamp,
                "player": artificial_player_config
            },
            {
                "broken": "dirt",
                "amount": 2,
                "timestamp": artificial_timestamp,
                "player": artificial_player_config
            }
        ]
        # print(data_from_actual_events)
        assert_same_events(expected, data_from_actual_events, "broken")

    def test_partial_missing_events(self):
        '''
        Scenario: The challenge file contains more "event spaces" than there are actual events. The missing events
        are artificially added.
        '''
        self._individual_setup('partial_missing_events_block_break_goal')

        lambda_result = lambda_handler(self.fake_lambda_event_object, {})
        self.assertEqual(lambda_result["statusCode"], 200, lambda_result["body"])
        response = self.table.query(
            KeyConditionExpression=Key('challenge_ID').eq(self.challenge_id))
        items = response.get("Items", [])
        if len(items) == 0:
            self.fail("No artificial events where added.")
        json_items = [deserialize(item) for item in items]
        print(json_items)
        data_from_actual_events = [item["data"] for item in json_items]
        print(json_items)
        artificial_timestamp = 0
        artificial_player_config = {
            "playerUUID": "",
            "playerName": "?",
            "skinTextureURL": 'http://textures.minecraft.net/texture/d34e063cafb467a5c8de43ec78619399f369f4a52434da8017a983cdd92516a0'
        }
        expected = [
            {
                "broken": "stone",
                "amount": 1,
                "timestamp": 80,
                "player": {
                    "playerUUID": "c41c9dcf-20cb-406d-aacd-bde2320283c6",
                    "playerName": "wand555",
                    "skinTextureURL": "http://textures.minecraft.net/texture/78c0ae51af17c299a4cff889054f04db731f490483614fa14588c45822fb6970"
                },
            },

            {
                "broken": "dirt",
                "amount": 2,
                "timestamp": artificial_timestamp,
                "player": artificial_player_config
            }
        ]
        # print(data_from_actual_events)
        assert_same_events(expected, data_from_actual_events, "broken")

    def test_too_many_events(self):
        self._individual_setup('too_many_events_block_break_goal')

        lambda_result = lambda_handler(self.fake_lambda_event_object, {})
        self.assertEqual(lambda_result["statusCode"], 200, lambda_result["body"])
        response = self.table.query(
            KeyConditionExpression=Key('challenge_ID').eq(self.challenge_id))
        items = response.get("Items", [])
        if len(items) == 0:
            self.fail("No artificial events where added.")
        json_items = [deserialize(item) for item in items]
        data_from_actual_events = [item["data"] for item in json_items]
        print(json_items)
        expected = [
            {
                "broken": "stone",
                "amount": 1,
                "timestamp": 80,
                "player": {
                    "playerUUID": "c41c9dcf-20cb-406d-aacd-bde2320283c6",
                    "playerName": "wand555",
                    "skinTextureURL": "http://textures.minecraft.net/texture/78c0ae51af17c299a4cff889054f04db731f490483614fa14588c45822fb6970"
                }
            }
        ]
        # print(data_from_actual_events)
        assert_same_events(expected, data_from_actual_events, "broken")

    def test_too_many_events_too_many_removed(self):
        '''
        Scenario: The existing event has an amount of 4 (achieved through skipping a goal). The challenge file lists
        that only 2 events should have occurred. In this case remove the event if amount of 4 and create an artificial
        event with amount of 2.
        '''

        self._individual_setup('too_many_events_too_many_removed_block_break_goal')

        lambda_result = lambda_handler(self.fake_lambda_event_object, {})
        self.assertEqual(lambda_result["statusCode"], 200, lambda_result["body"])
        response = self.table.query(
            KeyConditionExpression=Key('challenge_ID').eq(self.challenge_id))
        items = response.get("Items", [])
        if len(items) == 0:
            self.fail("No artificial events where added.")
        json_items = [deserialize(item) for item in items]
        data_from_actual_events = [item["data"] for item in json_items]
        print(json_items)
        artificial_timestamp = 0
        artificial_player_config = {
            "playerUUID": "",
            "playerName": "?",
            "skinTextureURL": 'http://textures.minecraft.net/texture/d34e063cafb467a5c8de43ec78619399f369f4a52434da8017a983cdd92516a0'
        }
        expected = [
            {
                "broken": "stone",
                "amount": 2,
                "timestamp": artificial_timestamp,
                "player": artificial_player_config
            }
        ]
        # print(data_from_actual_events)
        assert_same_events(expected, data_from_actual_events, "broken")

    def test_too_many_stone_too_many_removed_and_partial_missing_dirt(self):
        self._individual_setup('too_many_stone_too_many_removed_and_partial_missing_dirt_block_break_goal')

        lambda_result = lambda_handler(self.fake_lambda_event_object, {})
        self.assertEqual(lambda_result["statusCode"], 200, lambda_result["body"])
        response = self.table.query(
            KeyConditionExpression=Key('challenge_ID').eq(self.challenge_id))
        items = response.get("Items", [])
        if len(items) == 0:
            self.fail("No artificial events where added.")
        json_items = [deserialize(item) for item in items]
        data_from_actual_events = [item["data"] for item in json_items]
        print(json_items)
        artificial_timestamp = 0
        artificial_player_config = {
            "playerUUID": "",
            "playerName": "?",
            "skinTextureURL": 'http://textures.minecraft.net/texture/d34e063cafb467a5c8de43ec78619399f369f4a52434da8017a983cdd92516a0'
        }
        expected = [
            {
                "broken": "stone",
                "amount": 1,
                "timestamp": artificial_timestamp,
                "player": artificial_player_config
            },
            {
                "broken": "dirt",
                "amount": 1,
                "timestamp": 100,
                "player": {
                    "playerUUID": "c41c9dcf-20cb-406d-aacd-bde2320283c6",
                    "playerName": "wand555",
                    "skinTextureURL": "http://textures.minecraft.net/texture/78c0ae51af17c299a4cff889054f04db731f490483614fa14588c45822fb6970"
                }
            },
            {
                "broken": "dirt",
                "amount": 2,
                "timestamp": artificial_timestamp,
                "player": artificial_player_config
            }
        ]
        # print(data_from_actual_events)
        assert_same_events(expected, data_from_actual_events, "broken")


def fake_fetch_lambda_event_object(challenge_ID: str):
    return {
        "stageVariables": {
            "ENV": "testing"
        },
        "queryStringParameters": {
            "challenge_ID": challenge_ID
        }
    }
