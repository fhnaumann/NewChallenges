import clipboard from 'clipboardy'

describe('template spec', () => {
  it('passes', () => {
    cy.visit('http://localhost:5173/#/')
    /* ==== Generated with Cypress Studio ==== */
    cy.get('.ml-2 > .mx-auto > .flex-col > .p-button > .p-button-label').click();
    cy.get(':nth-child(1) > .border-solid > .flex').click();
    cy.get('.p-dialog-header-icon > .p-icon').click();
    cy.get(':nth-child(3) > .p-button > .p-button-label').click();
    cy.get('#code > pre').invoke("text").as("settings")
    cy.get('.bg-gray-900 > .flex > .p-button').click().then(() => {
      //clipboard.read().then((copied) => cy.log(copied))
    });
    /* ==== End Cypress Studio ==== */
    cy.log('1234')
    cy.log("5456")
    cy.get("@settings").then((text) => cy.writeFile("path/to/settings.json", text))
    it("print copied value", () => {
      cy.log("abc")
      //cy.log(this.settings)
    })
  })
})