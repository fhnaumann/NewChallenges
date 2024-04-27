import clipboard from 'clipboardy'

describe('rules tests', () => {
  beforeEach(() => {
    cy.emptySelection()
  })
  /* ==== Test Created with Cypress Studio ==== */
  it('gen_empty_settings', function() {
    /* ==== Generated with Cypress Studio ==== */
    //cy.visit('http://localhost:5173/');
    /* ==== End Cypress Studio ==== */
    /* ==== Generated with Cypress Studio ==== */
    cy.get('.h-screen > :nth-child(2) > .p-button > .p-button-label').click();
    cy.get('.bg-gray-900 > .flex > .p-button').click();
    
    /* ==== End Cypress Studio ==== */
    cy.get('#code > pre').invoke("text").as("settings")
    cy.get("@settings").then((text) => cy.writeFile("path/to/settings.json", text))
  });

  /* ==== Test Created with Cypress Studio ==== */
  it('no_death_end_challenge_mob_goal_1_ender_dragon', function() {
    /* ==== Generated with Cypress Studio ==== */
    cy.get('[aria-label="Configure Global Punishments 0"] > .p-button-label').click();
    cy.get(':nth-child(1) > .flex-col > .justify-between > :nth-child(1) > .p-checkbox > .p-checkbox-box').click();
    cy.get('.p-dialog-header-icon > .p-icon').click();
    cy.get('[aria-label="Add New Rule"] > .p-button-label').click();
    cy.get('.p-paginator-next > .p-icon').click();
    cy.get('.border-solid > .flex').click();
    cy.get('.p-dialog-header-icon > .p-icon > path').click();
    cy.get('.ml-2 > .mx-auto > .flex-col > .p-button > .p-button-label').click();
    cy.get(':nth-child(1) > .border-solid > .flex > .text-wrap').click();
    cy.get('.p-dialog-header-icon').click();
    cy.get('#pv_id_8_0_header_action > .flex > p').click();
    cy.get('.p-dropdown-label').click();
    cy.get('.p-dropdown-filter').clear('e');
    cy.get('.p-dropdown-filter').type('end');
    cy.get('#pv_id_41_1 > .flex > div').click();
    cy.get(':nth-child(2) > .p-checkbox > .p-checkbox-box').click();
    cy.get('.h-screen > :nth-child(2) > .p-button > .p-button-label').click();
    cy.get('.bg-gray-900 > .flex > .p-button').click();
    /* ==== End Cypress Studio ==== */
    cy.generateJSON("test123")
  });
})