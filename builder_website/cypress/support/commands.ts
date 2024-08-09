/// <reference types="cypress" />
// ***********************************************
// This example commands.ts shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add('login', (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add('drag', { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add('dismiss', { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite('visit', (originalFn, url, options) => { ... })
//
// declare global {
//   namespace Cypress {
//     interface Chainable {
//       login(email: string, password: string): Chainable<void>
//       drag(subject: string, options?: Partial<TypeOptions>): Chainable<Element>
//       dismiss(subject: string, options?: Partial<TypeOptions>): Chainable<Element>
//       visit(originalFn: CommandOriginalFn, url: string, options: Partial<VisitOptions>): Chainable<Element>
//     }
//   }
// }

import type { Affects, PunishmentName } from '../../src/models/punishments'
import type { RuleName } from '../../src/models/rules'
import type { CollectableEntryConfig, GoalName } from '../../src/models/goals'
import type { MobGoalConfig } from '../../src/models/mob'
import type { BlockPlaceGoalConfig } from '../../src/models/blockplace'
import type { BlockBreakGoalConfig } from '../../src/models/blockbreak'
import type { ItemGoalConfig, NoItemCollectRuleConfig } from '../../src/models/item'
import type { DeathGoalConfig } from '../../src/models/death'
import type {
  CustomHealthSettingConfig, FloorIsLavaSettingConfig,
  MLGSettingConfig,
  SettingName,
  UltraHardcoreSettingConfig,
} from '../../src/models/settings'
import type { NoCraftingRuleConfig } from '../../src/models/crafting'

Cypress.Commands.add('emptySelection', () => {
  cy.visit('/')
  return cy.get('.w-\\[10rem\\] > .duration-200').click()
})

Cypress.Commands.add('backToMainPage', () => {
  cy.get('.grow > .duration-200').click()
})

function uncheckAllPunishments() {
  (['endPunishment', 'healthPunishment'] as PunishmentName[])
    .map(value => `#${value}`)
    .forEach(value => cy.get(value).uncheck({ force: true }))
}

function setAffected(punishmentType: PunishmentName, affects: Affects): void {
  cy.get(`#${punishmentType}\\.affects > .leading-\\[normal\\]`).click()
  switch (affects) {
    case 'all':
      cy.get(`#${punishmentType}\\.affects_0`).click()
      break
    case 'causer':
      cy.get(`#${punishmentType}\\.affects_1`).click()
      break
  }
}

Cypress.Commands.add('configureHealthPunishment', (rule: RuleName, deselectOthers?: boolean, heartsLost?: number, randomizeHeartsLost?: boolean, affected?: Affects) => {
  if (deselectOthers) {
    uncheckAllPunishments()
  }
  cy.get('#healthPunishment').check({ force: true })
  if (heartsLost) {
    cy.get(`#rules\\.enabledRules\\.${rule}\\.punishments\\.healthPunishment`).clear()
    cy.get(`#rules\\.enabledRules\\.${rule}\\.punishments\\.healthPunishment`).type(String(heartsLost))
  }
  if (randomizeHeartsLost) {
    cy.get(`#rules\\.enabledRules\\.${rule}\\.punishments\\.healthPunishment\\.randomizeHeartsLost`).check({ force: true })
  }
  if (affected) {
    setAffected('healthPunishment', affected)
  }
})

Cypress.Commands.add('configureRandomEffectPunishment', (rule: RuleName, deselectOthers?: boolean, effects?: number, randomizeEffects?: boolean, affected?: Affects) => {
  if (deselectOthers) {
    uncheckAllPunishments()
  }
  cy.get('#randomEffectPunishment').check({ force: true })
  if (effects) {
    cy.get(`#rules\\.enabledRules\\.${rule}\\.punishments\\.randomEffectPunishment`).clear()
    cy.get(`#rules\\.enabledRules\\.${rule}\\.punishments\\.randomEffectPunishment`).type(String(effects))
  }
  if (randomizeEffects) {
    cy.get(`#rules\\.enabledRules\\.${rule}\\.punishments\\.randomEffectPunishment\\.randomizeEffectsAtOnce`).check({ force: true })
  }
  if (affected) {
    setAffected('randomEffectPunishment', affected)
  }
})

Cypress.Commands.add('configureMLGPunishment', (rule: RuleName, deselectOthers?: boolean, height?: number, affected?: Affects) => {
  if (deselectOthers) {
    uncheckAllPunishments()
  }
  cy.get('#mlgPunishment').check({ force: true })
  if (height !== undefined) {
    cy.get('#mlgPunishment.height').clear()
    cy.get('#mlgPunishment.height').type(String(height))
  }
  if (affected) {
    setAffected('mlgPunishment', affected)
  }
})

Cypress.Commands.add('configureRule', (ruleName: RuleName) => {
  cy.get('[aria-label="Browse all Rules"] > .duration-200').click()
  cy.get('.relative > .w-full').clear()
  cy.get('.relative > .w-full').type(ruleName)
  cy.get('.p-4 > .flex-row').click()
})

Cypress.Commands.add('configureGoal', (goalName: GoalName) => {
  cy.get('[aria-label="Browse all Goals"] > .duration-200').click()
  cy.get('.relative > .w-full').clear()
  cy.get('.relative > .w-full').type(goalName)
  cy.get('.p-4 > .flex-row').click()
})

Cypress.Commands.add('configureNoCraftingRule', (noCraftingRuleConfig?: NoCraftingRuleConfig) => {
  cy.configureRule('noCrafting')
  if(noCraftingRuleConfig !== undefined) {
    handleCheckBox('#internalCrafting', noCraftingRuleConfig.internalCrafting)
    handleCheckBox('#workbenchCrafting', noCraftingRuleConfig.workbenchCrafting)
    handleCheckBox('#furnaceSmelting', noCraftingRuleConfig.furnaceSmelting)
    handleCheckBox('#campfireCooking', noCraftingRuleConfig.campfireCooking)
    handleCheckBox('#smithing', noCraftingRuleConfig.smithing)
    handleCheckBox('#stonecutter', noCraftingRuleConfig.stonecutter)
  }
})

function clearCollectableSelection(): void {
  // TODO: this is fragile and will break as soon as the default collectables are not exactly one entry
  cy.get(':nth-child(1) > .bg-goal-100 > .duration-200').click()
}

function addCollectable(config: CollectableEntryConfig): void {
  cy.get('#collectableSelection > .leading-\\[normal\\]').click()
  cy.get('.pt-2 > .relative > .leading-\\[normal\\]').clear()
  cy.get('.pt-2 > .relative > .leading-\\[normal\\]').type(config.collectableName)
  cy.get('#collectableSelection_0').click()
  cy.get(':nth-child(2) > .flex-row > span.inline-flex > .leading-none').clear()
  cy.get(':nth-child(2) > .flex-row > span.inline-flex > .leading-none').type(String(config.collectableData.amountNeeded))
}

function setFixedOrder(fixedOrder: boolean): void {
  if (fixedOrder) {
    cy.get('#fixedOrder').check({ force: true })
  } else {
    cy.get('#fixedOrder').uncheck({ force: true })
  }
}

function setKillAllMobs(killAllMobs: boolean): void {
  if (killAllMobs) {
    cy.get('#killAllMobsOnce').check({ force: true })
  } else {
    cy.get('#killAllMobsOnce').uncheck({ force: true })
  }
}

Cypress.Commands.add('configureMobGoal', (mobGoalConfig?: MobGoalConfig, allMobs?: boolean) => {
  cy.configureGoal('mobGoal')
  clearCollectableSelection()
  mobGoalConfig?.mobs?.forEach(value => addCollectable(value))
  if (mobGoalConfig?.fixedOrder !== undefined) {
    setFixedOrder(mobGoalConfig.fixedOrder)
  }
  if (allMobs !== undefined) {
    setKillAllMobs(allMobs)
  }
})

Cypress.Commands.add('configureMobGoal1EnderDragonFixedOrder', () => {
  cy.configureMobGoal({
    mobs: [{ collectableName: 'Ender Dragon', collectableData: { amountNeeded: 1 } }],
    fixedOrder: true,
  })
})

Cypress.Commands.add('configureBlockPlaceGoal', (blockPlaceGoalConfig?: BlockPlaceGoalConfig, allBlocks?: boolean) => {
  cy.configureGoal('blockPlaceGoal')
  clearCollectableSelection()
  blockPlaceGoalConfig?.placed?.forEach(value => addCollectable(value))
  if (blockPlaceGoalConfig?.fixedOrder !== undefined) {
    setFixedOrder(blockPlaceGoalConfig.fixedOrder)
  }
  if (allBlocks !== undefined) {
    cy.get('#placeAllBlocksOnce').check({ force: true })
  }
})

Cypress.Commands.add('configureBlockBreakGoal', (blockBreakGoalConfig?: BlockBreakGoalConfig, allBlocks?: boolean) => {
  cy.configureGoal('blockBreakGoal')
  clearCollectableSelection()
  blockBreakGoalConfig?.broken?.forEach(value => addCollectable(value))
  if (blockBreakGoalConfig?.fixedOrder !== undefined) {
    setFixedOrder(blockBreakGoalConfig.fixedOrder)
  }
  if (allBlocks !== undefined) {
    cy.get('#breakAllBlocksOnce').check({ force: true })
  }
})

Cypress.Commands.add('configureItemGoal', (itemGoalConfig?: ItemGoalConfig, everything?: boolean, allItems?: boolean, allBlocks?: boolean) => {
  cy.configureGoal('itemGoal')
  clearCollectableSelection()
  itemGoalConfig?.items?.forEach(value => addCollectable(value))
  if (itemGoalConfig?.fixedOrder !== undefined) {
    setFixedOrder(itemGoalConfig.fixedOrder)
  }
  if (everything !== undefined) {
    cy.get('#collectEveryItemOnce').check({ force: true })
  }
  if (allItems !== undefined) {
    cy.get('#collectAllItemsOnce').check({ force: true })
  }
  if (allBlocks !== undefined) {
    cy.get('#collectAllBlockItemsOnce').check({ force: true })
  }
})

Cypress.Commands.add('configureDeathGoal', (deathGoalConfig?: DeathGoalConfig, allDeathTypes?: boolean) => {
  cy.configureGoal('deathGoal')
  if (deathGoalConfig?.deathMessages !== undefined || allDeathTypes) {
    cy.get('#individualDeathMessages').check({ force: true })
  }
  deathGoalConfig?.deathMessages?.forEach(value => addCollectable(value))
  if (deathGoalConfig!.deathAmount !== undefined) {
    cy.get('#deathAmount').clear()
    cy.get('#deathAmount').type(String(deathGoalConfig!.deathAmount.amountNeeded!))
  }
  if (deathGoalConfig?.fixedOrder !== undefined) {
    setFixedOrder(deathGoalConfig.fixedOrder)
  }
  if (allDeathTypes) {
    cy.get('#allDeathMessagesOnce').check({ force: true })
  }
})

Cypress.Commands.add('configureSetting', (settingName: SettingName) => {
  cy.get('[aria-label="Browse all Settings"] > .duration-200').click()
  cy.get('.relative > .w-full').clear()
  cy.get('.relative > .w-full').type(settingName)
  cy.get('.p-4 > .flex-row').click()
})

Cypress.Commands.add('configureCustomHealthSetting', (customHealthSettingConfig?: CustomHealthSettingConfig) => {
  cy.configureSetting('customHealthSetting')
  if (customHealthSettingConfig) {
    if (customHealthSettingConfig.hearts !== undefined) {
      cy.get('#customHealthSettingHearts').clear()
      cy.get('#customHealthSettingHearts').type(String(customHealthSettingConfig.hearts))
    }
  }
})

Cypress.Commands.add('configureUltraHardcoreSetting', (ultraHardCoreSettingConfig?: UltraHardcoreSettingConfig) => {
  cy.configureSetting('ultraHardcoreSetting')
  if (ultraHardCoreSettingConfig) {
    handleCheckBox('#naturalRegeneration', ultraHardCoreSettingConfig.naturalRegeneration)
    handleCheckBox('#regWithGoldenApples', ultraHardCoreSettingConfig.regWithGoldenApples)
    handleCheckBox('#regWithEnchantedGoldenApples', ultraHardCoreSettingConfig.regWithEnchantedGoldenApples)
    handleCheckBox('#regWithSuspiciousStew', ultraHardCoreSettingConfig.regWithSuspiciousStew)
    handleCheckBox('#regWithPotions', ultraHardCoreSettingConfig.regWithPotions)
    handleCheckBox('#allowAbsorptionHearts', ultraHardCoreSettingConfig.allowAbsorptionHearts)
    handleCheckBox('#allowTotems', ultraHardCoreSettingConfig.allowTotems)
  }
})

Cypress.Commands.add('configureFloorIsLavaSetting', (floorIsLavaSetting?: FloorIsLavaSettingConfig) => {
  cy.configureSetting('floorIsLavaSetting')
  if(floorIsLavaSetting !== undefined) {
    handleTextfield('#timeToNextBlockChangeInTicks', floorIsLavaSetting.timeToNextBlockChangeInTicks)
    handleCheckBox('#lavaRemainsPermanently', floorIsLavaSetting.lavaRemainsPermanently)
  }
})

Cypress.Commands.add('configureMLGSetting', (mlgSettingConfig?: MLGSettingConfig) => {
  cy.configureSetting('mlgSetting')
  if(mlgSettingConfig !== undefined) {
    handleTextfield('#settings\\.mlgSetting\\.min', mlgSettingConfig.minTimeSeconds)
    handleTextfield('#settings\\.mlgSetting\\.max', mlgSettingConfig.maxTimeSeconds)
    handleTextfield('#mlgSetting\\.height', mlgSettingConfig.height)
  }
})

function handleCheckBox(selector: string, configValue?: boolean) {
  if (configValue !== undefined) {
    if (configValue) {
      cy.get(selector).check({ force: true })
    } else {
      cy.get(selector).uncheck({ force: true })
    }
  }
}

function handleTextfield(selector: string, configValue: number | string | undefined) {
  if(configValue !== undefined) {
    cy.get(selector).clear()
    cy.get(selector).type(String(configValue))
  }
}

Cypress.Commands.add('addMetadata', (challengeName: string) => {
  cy.get('[aria-label="Modify metadata"] > .duration-200').click()
  cy.get('#challenge-name').clear()
  cy.get('#challenge-name').type(challengeName)
  cy.get('#challenge-created-by').clear()
  cy.get('#challenge-created-by').type('Wiki')
  cy.get('#challenge-whenCreated').clear()
  cy.get('#challenge-whenCreated').type('2024-06-23T14:17:48.131Z')
  cy.get('#challenge-lastModified').clear()
  cy.get('#challenge-lastModified').type('2024-06-23T14:17:48.131Z')
  cy.get('.inline-block').click()
})

Cypress.Commands.add('downloadFile', () => {
  cy.get('[aria-label="Download Settings File"] > .duration-200').click()
  cy.get('[aria-label="Download"] > .duration-200').click()
})


Cypress.Commands.add('generateJSON', (filename) => {
  //cy.get('#code > pre').invoke("text").as("settings")
  //cy.get("@settings").then((text) => cy.writeFile(`path/to/${filename}.json`, text))
  cy.readFile(`${Cypress.config('downloadsFolder')}/${filename}.json`).then((file) => cy.writeFile(`path/to/${filename}.json`, file))
})

Cypress.Commands.add('afterConfiguration', (challengeAndFileName: string) => {
  cy.addMetadata(challengeAndFileName)
  cy.downloadFile()
  cy.generateJSON(challengeAndFileName)
})

export {}
