describe('Test Files for DeathGoal Docs Generation', () => {
  beforeEach(() => {
    cy.emptySelection()
  })

  const EXAMPLE_CONFIG_1 = 'no_block_place_no_block_break_end_challenge_death_goal_death_amount_100'

  it(EXAMPLE_CONFIG_1, () => {
    cy.configureDeathGoal({ deathAmount: 100 })
      .backToMainPage()
      .configureRule('noBlockBreak')
      .backToMainPage()
      .configureRule('noBlockPlace')
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_1)
  })
})