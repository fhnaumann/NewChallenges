/// <reference types="cypress" />
// ***********************************************
// This example commands.ts shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add('login', (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add('drag', { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add('dismiss', { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite('visit', (originalFn, url, options) => { ... })
//
// declare global {
//   namespace Cypress {
//     interface Chainable {
//       login(email: string, password: string): Chainable<void>
//       drag(subject: string, options?: Partial<TypeOptions>): Chainable<Element>
//       dismiss(subject: string, options?: Partial<TypeOptions>): Chainable<Element>
//       visit(originalFn: CommandOriginalFn, url: string, options: Partial<VisitOptions>): Chainable<Element>
//     }
//   }
// }

Cypress.Commands.add('visitLiveChallenge', (challengeID: string) => {
  cy.intercept(`${Cypress.env('BASE_EXISTING_CHALLENGES_S3_URL')}/*`, { fixture: `${challengeID}.c.json` }).as('challenge-file')
  cy.intercept(`${Cypress.env('BASE_CHALLENGE_EVENTS_URL')}?challenge_ID=*`, { fixture: `${challengeID}.events.json` }).as('mc-events')

  cy.visit(`/challenge/${challengeID}`)
})

declare global {
  namespace Cypress {
    interface Chainable<Subject = any> {
      /**
       * Visit a live challenge webpage.
       * @param challengeID Uniquely identifies a challenge.
       */
      visitLiveChallenge(challengeID: string): Chainable<Subject>;
    }
  }
}


export {}
