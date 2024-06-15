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

Cypress.Commands.add('emptySelection', () => {
  cy.visit('/');
  cy.get('.w-\\[10rem\\] > .duration-200').click();
})

Cypress.Commands.add('addMetadata', (challengeName: string) => {
  cy.visit('/');
  cy.get('[aria-label="Modify metadata"] > .duration-200').click();
  cy.get('#challenge-name').clear();
  cy.get('#challenge-name').type(challengeName);
  cy.get('#challenge-created-by').clear();
  cy.get('#challenge-created-by').type('Wiki');
  cy.get('.inline-block').click();
})


Cypress.Commands.add('generateJSON', (filename) => {
  //cy.get('#code > pre').invoke("text").as("settings")
  //cy.get("@settings").then((text) => cy.writeFile(`path/to/${filename}.json`, text))
  cy.readFile(`${Cypress.config('downloadsFolder')}/${filename}.json`).then((file) => cy.writeFile(`path/to/${filename}.json`, file))

})

export {}
