import type { DataConfig, MCEvent } from '../../../criteria-interfaces/src'
import { WebSocket } from 'mock-socket'
import { getServer } from '../support/server.js'
import type { BlockBreakDataConfig } from '../../../criteria-interfaces'


describe('Testing BlockBreakGoal Challenge with Events', () => {
  it('BlockBreakGoal', () => {
    const socketPromise = intercept('blockbreakgoal', 'blockbreakgoal')

    cy.get('[data-cy="blockBreakGoal"]').should('exist')
    cy.get('[data-cy="mock1"]').should('exist')
    cy.get('[data-cy="5-time"]').contains("5")

    cy.get('[data-cy="blockBreakGoal"]').click()
    cy.get('[data-cy="show_completed-test"]').click({force: true})
    cy.get('[data-cy="dragon_egg"]').should('exist')
    cy.get('[data-cy="stone"]').should('exist')

    cy.get('[data-cy="dragon_egg-completionStatus"]').contains("0/2")
    cy.get('[data-cy="stone-completionStatus"]').contains("1/1")

    cy.get('body').type('{esc}')
    cy.wrap(socketPromise).then((mockSocket) => {
      // Use the `mockSocket` variable to send a message to client
      mockSocket.send(JSON.stringify({
        eventType: 'blockBreakGoal',
        eventID: "mock-live-1",
        challengeID: "mocked",
        timestamp: 15,
        data: {
          timestamp: 15,
          player: {},
          amount: 1,
          broken: 'dragon_egg'
        }
      } as MCEvent<BlockBreakDataConfig>))


      // After this, write code that asserts some change that happened due to the socket message
      cy.get('[data-cy="blockBreakGoal"]').click()
      cy.get('[data-cy="show_completed-test"]').click({force: true})
      cy.get('[data-cy="dragon_egg-completionStatus"]').contains("1/2")
    })
  })
})

function intercept(challengeFileFixturesPath: string, eventFileFixturesPath: string) {
  cy.intercept(Cypress.env('EXISTING_CHALLENGES_S3_INTERCEPT_URL'), {fixture: `${challengeFileFixturesPath}.c.json`}).as("challenge-file")
  cy.intercept(Cypress.env('CHALLENGE_EVENTS_INTERCEPT_URL'), {fixture: `${eventFileFixturesPath}.events.json`}).as("mc-events")

  // Create mock server
  const socketPromise =  getServer()

  // visit the page to establish connection
  cy.visit('/challenge/blockbreakgoal', {
    onBeforeLoad: (win) => {
      // Stub out JS WebSocket
      cy.stub(win, 'WebSocket', url => new WebSocket(url))
    }
  })

  cy.wait(["@challenge-file", "@mc-events"])

  return socketPromise
}