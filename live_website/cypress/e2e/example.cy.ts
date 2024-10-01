// https://on.cypress.io/api


describe('My First Test', () => {
  it('test fixture intercept', () => {
    cy.intercept(`${Cypress.env('BASE_EXISTING_CHALLENGES_S3_URL')}/*`, { fixture: 'challenges.c.json' }).as('challenges')
    cy.intercept(`${Cypress.env('BASE_CHALLENGE_EVENTS_URL')}?challenge_ID=*`, { fixture: 'events.json' }).as('events')

    cy.visit(`http://localhost:5174/challenges/${'m-mob'}`);


    cy.wait('@challenges')
    cy.wait('@events')
  })
})
