describe('Test Files for NoDeathRule Docs Generation', () => {
  beforeEach(() => {
    cy.emptySelection()
  })

  const EXAMPLE_CONFIG_1 = 'no_death_end_challenge_mob_goal_1_ender_dragon'

  it(EXAMPLE_CONFIG_1, () => {
    cy.configureRule('noDeath')
      .backToMainPage()
      .configureMobGoal1EnderDragonFixedOrder()
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_1)
  })
})