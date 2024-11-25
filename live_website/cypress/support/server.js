// server.js

import { Server } from 'mock-socket'

export const getServer = () => {
  return new Cypress.Promise(resolve => {
    // Initialize server
    const mockServer = new Server('wss://oaxuru4o1c.execute-api.eu-central-1.amazonaws.com/testing/?client_type=live-website&challenge_ID=blockbreakgoal')
    //const mockServer = new Server('ws://localhost:5173/')

    let mockSocket
    mockServer.on('connection', (socketHandle) => {
      resolve(socketHandle)
    })
  })
}