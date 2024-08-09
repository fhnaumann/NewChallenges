describe('Test Files for NoCraftingRule Docs Generation', () => {
  beforeEach(() => {
    cy.emptySelection()
  })

  const EXAMPLE_CONFIG_1 = 'no_crafting_no_crafting_at_all_1_heart_lost_all_mob_goal_1_ender_dragon'

  it(EXAMPLE_CONFIG_1, () => {
    cy.configureNoCraftingRule({
      internalCrafting: false,
      workbenchCrafting: false,
      campfireCooking: true,
      furnaceSmelting: true,
      smithing: true,
      stonecutter: false,
    })
      .configureHealthPunishment('noCrafting', true)
      .backToMainPage()
      .configureMobGoal1EnderDragonFixedOrder()
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_1)
  })

  const EXAMPLE_CONFIG_2 = 'no_crafting_nothing_1_heart_lost_all_mob_goal_1_ender_dragon'

  it(EXAMPLE_CONFIG_1, () => {
    cy.configureNoCraftingRule({
      internalCrafting: false,
      workbenchCrafting: false,
      campfireCooking: false,
      furnaceSmelting: false,
      smithing: false,
      stonecutter: false,
    })
      .configureHealthPunishment('noCrafting', true)
      .backToMainPage()
      .configureMobGoal1EnderDragonFixedOrder()
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_2)
  })

  const EXAMPLE_CONFIG_3 = 'no_crafting_no_smelting_cooking_1_heart_lost_all_mob_goal_1_ender_dragon'

  it(EXAMPLE_CONFIG_1, () => {
    cy.configureNoCraftingRule({
      internalCrafting: true,
      workbenchCrafting: true,
      campfireCooking: false,
      furnaceSmelting: false,
      smithing: true,
      stonecutter: true,
    })
      .configureHealthPunishment('noCrafting', true)
      .backToMainPage()
      .configureMobGoal1EnderDragonFixedOrder()
      .backToMainPage()
      .afterConfiguration(EXAMPLE_CONFIG_3)
  })
})