describe('Test Files for CraftingGoal Docs Generation', () => {
  beforeEach(() => {
    cy.emptySelection()
  })

  const EXAMPLE_CONFIG_1 = 'no_death_end_challenge_crafting_goal_all_crafting_recipes_once'

  it(EXAMPLE_CONFIG_1, () => {
    cy.configureCraftingGoal({fixedOrder: true}, false, true, false, false)
      .backToMainPage()
      .configureRule('noDeath')
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_1)
  })

  const EXAMPLE_CONFIG_2 = 'no_death_end_challenge_crafting_goal_all_smithing_recipes_once'

  it(EXAMPLE_CONFIG_2, () => {
    cy.configureCraftingGoal({fixedOrder: true}, false, false, false, true)
      .backToMainPage()
      .configureRule('noDeath')
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_2)
  })

  const EXAMPLE_CONFIG_3 = 'no_death_end_challenge_crafting_goal_all_recipes_once'

  it(EXAMPLE_CONFIG_3, () => {
    cy.configureCraftingGoal({fixedOrder: true}, true, false, false, false)
      .backToMainPage()
      .configureRule('noDeath')
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_3)
  })
})