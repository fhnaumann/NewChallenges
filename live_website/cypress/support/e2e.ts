// ***********************************************************
// This example support/index.js is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.js using ES2015 syntax:
import './commands'
import type { GoalName } from '@fhnaumann/criteria-interfaces'

// Alternatively you can use CommonJS syntax:
// require('./commands')

declare global {
  namespace Cypress {
    interface Chainable<Subject> {

      /**
       * Visit a live challenge webpage.
       * @param challengeID Uniquely identifies a challenges
       */
      visitLiveChallenge(challengeID: string): Chainable<Subject>

      /**
       * Open a dialog that shows detailed progress information about the given goal.
       * @param goalName
       */
      openGoalDetails(goalName: GoalName): Chainable<Subject>
    }
  }
}