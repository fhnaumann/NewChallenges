describe('Test Files for NoBlockBreakRule Docs Generation', () => {
  beforeEach(() => {
    cy.emptySelection()
  })

  const EXAMPLE_CONFIG_1 = 'no_block_break_1_heart_lost_all_mob_goal_1_ender_dragon'

  it(EXAMPLE_CONFIG_1, () => {
    cy.configureRule('noBlockBreak')
      .configureHealthPunishment('noBlockBreak', true)
      .backToMainPage()
      .configureMobGoal1EnderDragonFixedOrder()
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_1)
  })
})