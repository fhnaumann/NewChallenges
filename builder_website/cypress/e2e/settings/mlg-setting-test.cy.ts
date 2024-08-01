describe('Test Files for MLGSetting Docs Generation', () => {
  beforeEach(() => {
    cy.emptySelection()
  })

  const EXAMPLE_CONFIG_1 = 'no_death_end_challenge_mob_goal_1_ender_dragon_mlg_setting_30_height_60s_min_5m_max'

  it(EXAMPLE_CONFIG_1, () => {
    cy.configureMobGoal1EnderDragonFixedOrder()
      .backToMainPage()
      .configureRule('noDeath')
      .backToMainPage()
      .configureMLGSetting({ height: 30, minTimeSeconds: 30, maxTimeSeconds: 5 * 60 })
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_1)
  })
})