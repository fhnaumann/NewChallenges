// https://on.cypress.io/api


import { useChallengeState } from '../../src/stores/challenge_state'
import type { BlockBreakDataConfig, DataConfig, MCEvent } from '../../../criteria-interfaces'

describe('Challenge State is correctly fetched', () => {
  /*
  it('test fixture intercept', () => {

    //cy.visit(`http://localhost:5174/challenge/${''}`);
    cy.visitLiveChallenge('timer-1')

    cy.wait('@challenge-file')
    cy.wait('@mc-events')

    cy.get('[data-cy="not-started-text"]').should('exist')
    cy.get('[data-cy="running-text"]').should('exist')
  })*/
  it('state is not started if events empty', () => {
    intercept([], undefined)

    cy.get('[data-cy="not-started-text"]').should('exist')
  })
  it('state is (assumed to be) running with time from last event', () => {
    intercept([{
      action: 'event',
      eventType: 'blockBreakGoal',
      timestamp: 5,
      challengeID: 'timer-1',
      eventID: "ignored",
      data: {
        amount: 1,
        player: {
          playerName: "",
          playerUUID: "",
          skinTextureURL: ""
        },
        timestamp: 10
      }
    },
      {
      action: 'event',
      eventType: 'blockBreakGoal',
      timestamp: 10,
      challengeID: 'timer-1',
      eventID: "ignored",
      data: {
        amount: 1,
        player: {
          playerName: "",
          playerUUID: "",
          skinTextureURL: ""
        },
        timestamp: 10
      }
    }], undefined)
    cy.get('[data-cy="running-text"]').should('exist')
    cy.get('[data-cy="running-text"]').contains('10')
  })

  it('state is finished when last event is finished event', () => {
    intercept([{
      action: 'event',
      eventType: 'blockBreakGoal',
      timestamp: 5,
      challengeID: 'timer-1',
      eventID: "ignored",
      data: {
        amount: 1,
        player: {
          playerName: "",
          playerUUID: "",
          skinTextureURL: ""
        },
        timestamp: 5,
        broken: "stone"
      } as BlockBreakDataConfig
    },
      {
        action: 'event',
        eventType: 'end',
        timestamp: 10,
        challengeID: 'timer-1',
        eventID: "ignored",
      }], undefined)
    cy.get('[data-cy="finished-text"]').should('exist')
  })
})

function intercept(eventResponse: MCEvent<DataConfig>[] | undefined, fixturePath: string | undefined) {
  cy.intercept(Cypress.env('EXISTING_CHALLENGES_S3_INTERCEPT_URL'), {fixture: 'timer-1.c.json'}).as("challenge-file")
  if(eventResponse !== undefined) {
    cy.intercept(Cypress.env('CHALLENGE_EVENTS_INTERCEPT_URL'), eventResponse).as("mc-events")
  }
  if(fixturePath !== undefined) {
    cy.intercept(Cypress.env('CHALLENGE_EVENTS_INTERCEPT_URL'), {fixture: `${fixturePath}.events.json`}).as("mc-events")
  }

  cy.visit("/challenge/timer-1")
  cy.wait(["@challenge-file", "@mc-events"])
}
