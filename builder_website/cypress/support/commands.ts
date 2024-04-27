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
    cy.visit('http://localhost:5173/');
    cy.get('#pv_id_5_0_header_action > .flex > .cursor-pointer').click();
    cy.get('#pv_id_8_0_header_action > .flex > .cursor-pointer').click();
    cy.get('[aria-label="Configure Global Punishments 1"] > .p-button-label').click();
    cy.get(':nth-child(1) > .flex-col > .justify-between > :nth-child(1) > .p-checkbox > .p-checkbox-box').click();
    cy.get('.p-dialog-header-icon > .p-icon > path').click();
})

Cypress.Commands.add('generateJSON', (filename) => {
    cy.get('#code > pre').invoke("text").as("settings")
    cy.get("@settings").then((text) => cy.writeFile(`path/to/${filename}.json`, text))
})

export {}
