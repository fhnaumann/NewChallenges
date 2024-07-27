describe('Test Files for CustomHealthSetting Docs Generation', () => {
  beforeEach(() => {
    cy.emptySelection()
  })

  const EXAMPLE_CONFIG_1 = 'no_death_end_challenge_mob_goal_1_ender_dragon_no_natural_reg'

  it(EXAMPLE_CONFIG_1, () => {
    cy.configureMobGoal1EnderDragonFixedOrder()
      .backToMainPage()
      .configureRule('noDeath')
      .backToMainPage()
      .configureUltraHardcoreSetting({
        naturalRegeneration: false,
        regWithGoldenApples: true,
        regWithEnchantedGoldenApples: true,
        regWithSuspiciousStew: true,
        regWithPotions: true,
        allowAbsorptionHearts: true,
        allowTotems: true,
      })
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_1)
  })

  const EXAMPLE_CONFIG_2 = 'no_death_end_challenge_mob_goal_1_ender_dragon_no_reg_at_all'

  it(EXAMPLE_CONFIG_1, () => {
    cy.configureMobGoal1EnderDragonFixedOrder()
      .backToMainPage()
      .configureRule('noDeath')
      .backToMainPage()
      .configureUltraHardcoreSetting({
        naturalRegeneration: false,
        regWithGoldenApples: false,
        regWithEnchantedGoldenApples: false,
        regWithSuspiciousStew: false,
        regWithPotions: false,
        allowAbsorptionHearts: false,
        allowTotems: false,
      })
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_2)
  })
})