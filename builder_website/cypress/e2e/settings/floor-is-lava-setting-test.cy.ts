describe('Test Files for FloorIsLavaSetting Docs Generation', () => {
  beforeEach(() => {
    cy.emptySelection()
  })

  const EXAMPLE_CONFIG_1 = 'no_death_end_challenge_mob_goal_1_ender_dragon_floorislava_setting_1s_no_permanent_lava'

  it(EXAMPLE_CONFIG_1, () => {
    cy.configureMobGoal1EnderDragonFixedOrder()
      .backToMainPage()
      .configureRule('noDeath')
      .backToMainPage()
      .configureFloorIsLavaSetting({timeToNextBlockChangeInTicks: 20, lavaRemainsPermanently: false})
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_1)
  })
})