describe('Test Files for ItemGoal Docs Generation', () => {
  beforeEach(() => {
    cy.emptySelection()
  })

  const EXAMPLE_CONFIG_1 = 'no_death_end_challenge_item_goal_1_dragon_egg'

  it(EXAMPLE_CONFIG_1, () => {
    cy.configureItemGoal({
      items: [{ collectableName: 'Dragon Egg', collectableData: { amountNeeded: 1 } }],
      fixedOrder: true,
    })
      .backToMainPage()
      .configureRule('noDeath')
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_1)
  })

  const EXAMPLE_CONFIG_2 = 'no_death_end_challenge_item_goal_every_item_once_fixed_order'

  it(EXAMPLE_CONFIG_2, () => {
    cy.configureItemGoal({ fixedOrder: true }, true)
      .backToMainPage()
      .configureRule('noDeath')
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_2)
  })
})