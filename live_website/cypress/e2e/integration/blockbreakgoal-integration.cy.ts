describe('BlockBreakGoal Integration Test', () => {

  const challengeName = "block_break_goal_it"

  it('Events are fetched and loaded', () => {
    cy.visit(`/challenge/${challengeName}`)

    cy.get('[data-cy="blockBreakGoal"]').click()
    cy.get('[data-cy="show_completed-test"]').click()
    cy.get('[data-cy="dragon_egg-completionStatus"]').contains("0/1")
    cy.get('[data-cy="stone-completionStatus"]').contains("1/1")
  })
})