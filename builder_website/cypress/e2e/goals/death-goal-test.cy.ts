describe('Test Files for DeathGoal Docs Generation', () => {
  beforeEach(() => {
    cy.emptySelection()
  })

  const EXAMPLE_CONFIG_1 = 'no_block_place_no_block_break_end_challenge_death_goal_death_amount_100'

  it(EXAMPLE_CONFIG_1, () => {
    cy.configureDeathGoal({ deathAmount: { amountNeeded: 100 } })
      .backToMainPage()
      .configureRule('noBlockBreak')
      .backToMainPage()
      .configureRule('noBlockPlace')
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_1)
  })

  const EXAMPLE_CONFIG_2 = 'no_block_place_no_block_break_end_challenge_death_goal_all_death_types_once_fixed_order'

  it(EXAMPLE_CONFIG_2, () => {
    cy.configureDeathGoal({fixedOrder: true }, true)
      .backToMainPage()
      .configureRule('noBlockBreak')
      .backToMainPage()
      .configureRule('noBlockPlace')
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_2)
  })
})