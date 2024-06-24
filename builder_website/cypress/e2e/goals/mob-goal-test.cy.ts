describe('Test Files for MobGoal Docs Generation', () => {
  beforeEach(() => {
    cy.emptySelection()
  })

  const EXAMPLE_CONFIG_1 = 'no_death_end_challenge_mob_goal_1_ender_dragon'

  it(EXAMPLE_CONFIG_1, () => {
    cy.configureMobGoal1EnderDragonFixedOrder()
      .backToMainPage()
      .configureRule('noDeath')
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_1)
  })

  const EXAMPLE_CONFIG_2 = 'no_death_end_challenge_mob_goal_every_mob_once_fixed_order'

  it(EXAMPLE_CONFIG_2, () => {
    cy.configureMobGoal({ fixedOrder: true }, true)
      .backToMainPage()
      .configureRule('noDeath')
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_2)
  })
})