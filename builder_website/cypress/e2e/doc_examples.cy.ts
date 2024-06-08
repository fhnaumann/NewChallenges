describe('Test Files for Docs Generation', () => {
  beforeEach(() => {
    cy.emptySelection()
  })


  it('no_death_end_challenge_block_break_goal_1_beacon', () => {
    /* ==== Generated with Cypress Studio ==== */
    cy.get('[aria-label="Browse all Rules"] > .duration-200').click();
    cy.get('.p-4 > :nth-child(1)').click();
    cy.get('.grow').click();
    cy.get('.customized-goal > .relative > .duration-200').click();
    cy.get('.p-4 > :nth-child(1)').click();
    cy.get('#pv_id_12 > .leading-\\[normal\\]').click();
    cy.get('.pt-2 > .relative > .leading-\\[normal\\]').clear('b');
    cy.get('.pt-2 > .relative > .leading-\\[normal\\]').type('bea');
    cy.get('.justify-start > .flex > div').click();
    cy.get('.grow').click();
    cy.get('.fixed > .flex > .relative > .duration-200').click();
    cy.get('[aria-label="Download"] > .duration-200').click();
    /* ==== End Cypress Studio ==== */
    cy.generateJSON("no_death_end_challenge_block_break_goal_1_beacon")
  })

  /* ==== Test Created with Cypress Studio ==== */
  it('no_death_end_challenge_block_break_goal_every_block_once_fixed_order', function() {
    /* ==== Generated with Cypress Studio ==== */
    cy.get('[aria-label="Browse all Rules"] > .duration-200').click();
    cy.get('.relative > .w-full').clear('n');
    cy.get('.relative > .w-full').type('nodeath');
    cy.get('.p-4 > .flex-row').click();
    cy.get('.grow > .duration-200').click();
    cy.get('.customized-goal > .relative > .duration-200').click();
    cy.get('.relative > .w-full').clear('bl');
    cy.get('.relative > .w-full').type('blockbrea');
    cy.get('.p-4 > .flex-row').click();
    cy.get('#breakAllBlocksOnce').check();
    cy.get(':nth-child(4) > .relative > #goals\\.blockBreakGoal').check();
    cy.get('.grow').click();
    cy.get('.fixed > .flex > .relative > .duration-200').click();
    cy.get('[aria-label="Download"] > .duration-200').click();
    /* ==== End Cypress Studio ==== */
    cy.generateJSON("no_death_end_challenge_block_break_goal_every_block_once_fixed_order")
  });

  /* ==== Test Created with Cypress Studio ==== */
  it('no_death_end_challenge_item_goal_1_dragon_egg', function() {
    /* ==== Generated with Cypress Studio ==== */
    cy.get('[aria-label="Browse all Rules"] > .duration-200').click();
    cy.get('.relative > .w-full').clear();
    cy.get('.relative > .w-full').type('nodeath');
    cy.get('.p-4 > .flex-row').click();
    cy.get('.grow').click();
    cy.get('.customized-goal > .relative > .duration-200').click();
    cy.get('.relative > .w-full').clear('it');
    cy.get('.relative > .w-full').type('item');
    cy.get('.flex-row > .flex > :nth-child(2)').click();
    cy.get('.grow').click();
    cy.get('.fixed > .flex > .relative > .duration-200').click();
    cy.get('[aria-label="Download"] > .duration-200').click();
    cy.get('.shrink-0').click();
    /* ==== End Cypress Studio ==== */
    cy.generateJSON("no_death_end_challenge_item_goal_1_dragon_egg")
  });

  /* ==== Test Created with Cypress Studio ==== */
  it('no_death_end_challenge_item_goal_every_item_once_fixed_order', function() {
    /* ==== Generated with Cypress Studio ==== */
    cy.get('[aria-label="Browse all Rules"] > .duration-200').click();
    cy.get('.relative > .w-full').clear();
    cy.get('.relative > .w-full').type('nodea');
    cy.get('.p-4 > .flex-row').click();
    cy.get('.grow').click();
    cy.get('.customized-goal > .relative > .duration-200').click();
    cy.get('.relative > .w-full').clear();
    cy.get('.relative > .w-full').type('item');
    cy.get('.p-4 > .flex-row').click();
    cy.get('#collectEveryItemOnce').check();
    cy.get(':nth-child(5) > .relative > #goals\\.itemGoal').check();
    cy.get('.grow > .duration-200').click();
    cy.get('.fixed > .flex > .relative > .duration-200').click();
    cy.get('[aria-label="Download"]').click();
    /* ==== End Cypress Studio ==== */
    cy.generateJSON("no_death_end_challenge_item_goal_every_item_once_fixed_order")
  });

  /* ==== Test Created with Cypress Studio ==== */
  it('no_death_end_challenge_mob_goal_1_ender_dragon', function() {
    /* ==== Generated with Cypress Studio ==== */
    cy.get('[aria-label="Browse all Rules"] > .duration-200').click();
    cy.get('.relative > .w-full').clear();
    cy.get('.relative > .w-full').type('nodeath');
    cy.get('.p-4 > .flex-row').click();
    cy.get('.grow').click();
    cy.get('.customized-goal > .relative > .duration-200').click();
    cy.get('.relative > .w-full').clear();
    cy.get('.relative > .w-full').type('mob');
    cy.get('.p-4 > .flex-row').click();
    cy.get(':nth-child(4) > .relative > #goals\\.mobGoal').check();
    cy.get('.grow').click();
    cy.get('.fixed > .flex > .relative').click();
    cy.get('[aria-label="Download"] > .duration-200').click();
    /* ==== End Cypress Studio ==== */
    cy.generateJSON("no_death_end_challenge_mob_goal_1_ender_dragon")
  });

  /* ==== Test Created with Cypress Studio ==== */
  it('no_death_end_challenge_mob_goal_every_mob_once_fixed_order', function() {
    /* ==== Generated with Cypress Studio ==== */
    cy.get('[aria-label="Browse all Rules"] > .duration-200').click();
    cy.get('.relative > .w-full').clear();
    cy.get('.relative > .w-full').type('nodeat');
    cy.get('.p-4 > .flex-row').click();
    cy.get('.grow').click();
    cy.get('.customized-goal > .relative > .duration-200').click();
    cy.get('.relative > .w-full').clear();
    cy.get('.relative > .w-full').type('mob');
    cy.get('.p-4 > .flex-row').click();
    cy.get('#killAllMobsOnce').check();
    cy.get(':nth-child(4) > .relative > #goals\\.mobGoal').check();
    cy.get('.grow > .duration-200').click();
    cy.get('.fixed > .flex > .relative > .duration-200').click();
    cy.get('[aria-label="Download"] > .duration-200').click();
    /* ==== End Cypress Studio ==== */
    cy.generateJSON("no_death_end_challenge_mob_goal_every_mob_once_fixed_order")
  });
})