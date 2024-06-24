describe('Test Files for NoMobKillRule Docs Generation', () => {
  beforeEach(() => {
    cy.emptySelection()
  })

  const EXAMPLE_CONFIG_1 = 'no_mob_kill_except_ender_dragon_2_random_effect_all_mob_goal_1_ender_dragon'

  it(EXAMPLE_CONFIG_1, () => {
    cy.configureRule('noItem')
      .configureRandomEffectPunishment('noItem', true, 2, false, 'all')
      .backToMainPage()
      .configureMobGoal1EnderDragonFixedOrder()
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_1)
  })
})