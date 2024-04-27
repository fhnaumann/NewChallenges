describe('goals tests', () => {
    beforeEach(() => {
        cy.emptySelection()
    })

    it('no_death_end_challenge_block_break_goal_1_beacon', () => {
        /* ==== Generated with Cypress Studio ==== */
        cy.get('[aria-label="Add New Rule"] > .p-button-label').click();
        cy.get('.p-paginator-next > .p-icon').click();
        cy.get('.border-solid > .flex').click();
        cy.get('.p-dialog-header-icon').click();
        cy.get('[aria-label="Configure Global Punishments 0"]').click();
        cy.get(':nth-child(1) > .flex-col > .justify-between > :nth-child(1) > .p-checkbox > .p-checkbox-box').click();
        cy.get('.p-dialog-header-icon > .p-icon').click();
        cy.get('.ml-2 > .mx-auto > .flex-col > .p-button').click();
        cy.get(':nth-child(3) > .border-solid > .flex > .text-3xl').click();
        cy.get('.p-dialog-header-icon').click();
        cy.get('#pv_id_8_0_header_action > .flex > p').click();
        cy.get('.p-dropdown-label').click();
        cy.get('.p-dropdown-filter').clear('be');
        cy.get('.p-dropdown-filter').type('bea');
        cy.get('#pv_id_41_0 > .flex > div').click();
        cy.get(':nth-child(2) > .p-checkbox > .p-checkbox-box').click();
        cy.get('.h-screen > :nth-child(2) > .p-button > .p-button-label').click();
        cy.get('.bg-gray-900 > .flex > .p-button').click();
        /* ==== End Cypress Studio ==== */
        cy.generateJSON("no_death_end_challenge_block_break_goal_1_beacon")
    })
})