# API DOC

https://de.fiverr.com/s/427o2pk
https://de.fiverr.com/s/vvyBvkZ
https://de.fiverr.com/s/yvAYeQq


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