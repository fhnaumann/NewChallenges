// https://on.cypress.io/api


describe('My First Test', () => {
  it('test fixture intercept', () => {

    cy.visit(`http://localhost:5174/challenge/${'m-mob'}`);


    cy.wait('@challenges')
    cy.wait('@events')
  })
})
