# API DOC

# Communication Tests

Each "test case" is set up on the Challenges Plugin Unit Test, the Live Frontend Unit Test, the AWS Lambda Unit Test, the Challenges Plugin -> AWS Integration Test, and then AWS -> Live Frontend Integration Test.
Every test case requires some JSON data as part of the test. This folder contains all test resource files which are copied to the different places where they are needed.

- Challenges Plugin Unit Test and Challenges Plugin -> AWS Integration Test: plugin/src/test/resources/wand55/github/io/challenges/communication/
- Live Frontend Unit Test: live_website/cypress/fixtures/communication/
- AWS Lambda Unit Test: aws_resources/lambda-tests/tests/resources/communication/
- The test resources for the AWS -> Live Frontend Integration Test will be stored on the testing stage on AWS after running the Challenges Plugin -> AWS Integration Test. This tests the event fetching. Additionally, the same data will be used to again to simulate receiving the events live

Note that each test suite may have additional tests to handle specific edge cases only relevant to them.

## Simple Test Cases:

Used for Unit Tests and Integration Tests.

- Connect Event JSON
- BlockBreakGoal Event JSON: stone broken
- BlockBreakRule Event JSON: stone broken, 1 heart lost
...
- One dummy challenge file JSON

## Complex Test Cases:

Used for Integration Tests.

### Simple Complete Challenge Communication

- Challenge File JSON: NoBlockPlace, 1 Heart Punishment, collect 10 dragon egg (ItemGoal)
- Historic Event JSON: Connect Event
- Occurring Event JSON

### 

## MC-Events (Websockets)

- Connect:
    Param `client_type` can be `mc-server` or `live-website`. If the latter, then its connectionID will be stored in a dynamodb temporarily.
- Challenge Start:
```json
{
    "action": "event",
    "challengeID": "challenge-1",
    "eventID": "event-1",
    "eventType": "start",
    "timestamp": 0,
}
```

- Challenge Pause:
```json
{
    "action": "event",
    "challengeID": "challenge-1",
    "eventID": "event-1",
    "eventType": "`pause`",
    "timestamp": 0,
}
```

- Challenge Resume:
```json
{
    "action": "event",
    "challengeID": "challenge-1",
    "eventID": "event-1",
    "eventType": "resume",
    "timestamp": 0,
}
```
- Challenge Event:
```json
{
    "action": "event",
    "challengeID": "challenge-1",
    "eventID": "event-1",
    "eventType": "blockBreakGoal",
    "timestamp": 0,
    "data": {
        "playerUUID": "...",
        "blockBroken": "stone",
        "amount": 1
    }
}
```

- Challenge End:
```json
{
    "action": "event",
    "challengeID": "challenge-1",
    "eventID": "event-1",
    "eventType": "end",
    "timestamp": 5436,
    "endData": {
        "success": false,
        "winnerTeam": "Team-1"
    }
}
```
- Challenge Resume After End:
```json
{
    "action": "event",
    "challengeID": "challenge-1",
    "eventID": "event-1",
    "eventType": "resume-after-end",
    "timestamp": 0,
}
```