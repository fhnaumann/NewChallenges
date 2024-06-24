describe('Test Files for NoItemRule Docs Generation', () => {
  beforeEach(() => {
    cy.emptySelection()
  })

  const EXAMPLE_CONFIG_1 = 'no_item_1_heart_lost_all_mob_goal_1_ender_dragon'

  it(EXAMPLE_CONFIG_1, () => {
    cy.configureRule('noItem')
      .configureHealthPunishment('noItem', true)
      .backToMainPage()
      .configureMobGoal1EnderDragonFixedOrder()
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_1)
  })
})