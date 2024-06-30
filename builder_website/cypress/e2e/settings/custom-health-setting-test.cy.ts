describe('Test Files for CustomHealthSetting Docs Generation', () => {
  beforeEach(() => {
    cy.emptySelection()
  })

  const EXAMPLE_CONFIG_1 = 'no_death_end_challenge_mob_goal_1_ender_dragon_custom_health_setting_10'

  it(EXAMPLE_CONFIG_1, () => {
    cy.configureMobGoal1EnderDragonFixedOrder()
      .backToMainPage()
      .configureRule('noDeath')
      .backToMainPage()
      .configureCustomHealthSetting({ hearts: 10 })
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_1)
  })
})