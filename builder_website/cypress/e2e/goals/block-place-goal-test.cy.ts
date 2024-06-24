describe('Test Files for BlockPlaceGoal Docs Generation', () => {
  beforeEach(() => {
    cy.emptySelection()
  })

  const EXAMPLE_CONFIG_1 = 'no_death_end_challenge_block_place_goal_dragon_egg_fixed_order'

  it(EXAMPLE_CONFIG_1, () => {
    cy.configureBlockPlaceGoal({
      placed: [{ collectableName: 'Dragon Egg', collectableData: { amountNeeded: 1 } }],
      fixedOrder: true,
    })
      .backToMainPage()
      .configureRule('noDeath')
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_1)
  })

  const EXAMPLE_CONFIG_2 = 'no_death_end_challenge_block_place_goal_every_block_once_fixed_order'

  it(EXAMPLE_CONFIG_2, () => {
    cy.configureBlockPlaceGoal({ fixedOrder: true }, true)
      .backToMainPage()
      .configureRule('noDeath')
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_2)
  })
})