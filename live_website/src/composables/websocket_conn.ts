export function useWebsocketConn(challengeID: string) {

  const ws = new WebSocket(`wss://oaxuru4o1c.execute-api.eu-central-1.amazonaws.com/production/?client_type=live-website&challenge_ID=${challengeID}`)



}