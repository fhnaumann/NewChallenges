describe('Test Files for BlockBreakGoal Docs Generation', () => {
  beforeEach(() => {
    cy.emptySelection()
  })

  const EXAMPLE_CONFIG_1 = 'no_death_end_challenge_block_break_goal_1_beacon'

  it(EXAMPLE_CONFIG_1, () => {
    cy.configureBlockBreakGoal({
      broken: [{ collectableName: 'beacon', collectableData: { amountNeeded: 1 } }],
      fixedOrder: true,
    })
      .backToMainPage()
      .configureRule('noDeath')
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_1)
  })

  const EXAMPLE_CONFIG_2 = 'no_death_end_challenge_block_break_goal_every_block_once_fixed_order'

  it(EXAMPLE_CONFIG_2, () => {
    cy.configureBlockBreakGoal({ fixedOrder: true }, true)
      .backToMainPage()
      .configureRule('noDeath')
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_2)
  })
})