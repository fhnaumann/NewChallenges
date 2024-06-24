// ***********************************************************
// This example support/index.js is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.js using ES2015 syntax:
import './commands'
import type { RuleName } from '../../src/models/rules'
import type { Affects } from '../../src/models/punishments'
import type { GoalName } from '../../src/models/goals'
import type { MobGoalConfig } from '../../src/models/mob'
import type { BlockPlaceGoalConfig } from '../../src/models/blockplace'
import type { BlockBreakGoalConfig } from '../../src/models/blockbreak'
import type { ItemGoalConfig } from '../../src/models/item'
import type { DeathGoalConfig } from '../../src/models/death'

// Alternatively you can use CommonJS syntax:
// require('./commands')

declare global {
  namespace Cypress {

    interface Chainable<Subject> {

      /**
       * Visits the Main Page and clears all default selections.
       *
       * Start: Nothing
       * End: Main Page
       */
      emptySelection(): Chainable<Subject>

      /**
       * Opens the metadata editor and sets some predefined values and
       * the provided challengeName
       *
       * Start: Main Page
       * End: Main Page
       */
      addMetadata(challengeName: string): Chainable<Subject>

      /**
       * Downloads the file.
       *
       * Start: Main Page
       * End: Main Page with Dialog for downloading open
       */
      downloadFile(): Chainable<Subject>

      /**
       * Reads a file that was previously downloaded (in the downloads folder)
       * and writes it to a specific path that the e2e GitHub Actions works with.
       */
      generateJSON(fileName: string): Chainable<Subject>

      /**
       * Aggregates commands that run after doing the customization with rules/goals/punishments.
       * Runs addMetadata, downloadFile and generateJSON.
       *
       * Start: Main Page
       * End: Main Page with Dialog for downloading open
       */
      afterConfiguration(challengeAndFileName: string): Chainable<Subject>

      /**
       * Adds a rule.
       *
       * Start: Main Page
       * End: Page for customizing the rule that is/was added.
       */
      configureRule(ruleName: RuleName): Chainable<Subject>

      /**
       * Adds a goal.
       * Start: Main Page
       * End: Page for customizing the goal that is/was added.
       */
      configureGoal(goalName: GoalName): Chainable<Subject>

      /**
       * Enable health punishment (and disable the default end punishment).
       *
       * Start: Page for customizing a rule
       * End: Page for customizing a rule
       *
       */
      configureHealthPunishment(rule: RuleName, deselectOthers?: boolean, heartsLost?: number, randomizeHeartsLost?: boolean, affected?: Affects): Chainable<Subject>

      /**
       * Enable random effect punishment (and disable the default end punishment).
       *
       * Start: Page for customizing a rule
       * End: Page for customizing a rule
       */
      configureRandomEffectPunishment(rule: RuleName, deselectOthers?: boolean, effects?: number, randomizeEffects?: boolean, affected?: Affects): Chainable<Subject>

      /**
       * Customize MobGoal.
       *
       * Start: Main Page
       * End: Page for customizing a goal
       */
      configureMobGoal(mobGoalConfig?: MobGoalConfig, allMobs?: boolean): Chainable<Subject>

      /**
       * Customize BlockPlaceGoal
       *
       * Start: Main Page
       * End: Page for customizing a goal
       *
       * @param blockPlaceGoalConfig
       * @param allBlocks
       */
      configureBlockPlaceGoal(blockPlaceGoalConfig?: BlockPlaceGoalConfig, allBlocks?: boolean): Chainable<Subject>

      /**
       * Customize BlockBreakGoal
       *
       * Start: Main Page
       * End: Page for customizing a goal
       */
      configureBlockBreakGoal(blockBreakGoalConfig?: BlockBreakGoalConfig, allBlocks?: boolean): Chainable<Subject>

      /**
       * Customize ItemGoal
       *
       * Start: Main Page
       * End: Page for customizing a goal
       */
      configureItemGoal(itemGoalConfig?: ItemGoalConfig, everything?: boolean, allItems?: boolean, allBlocks?: boolean): Chainable<Subject>

      /**
       * Customize DeathGoal
       *
       * Start: Main Page
       * End: Page for customizing a goal
       */
      configureDeathGoal(deathGoalConfig?: DeathGoalConfig): Chainable<Subject>

      /**
       * Start: Main Page
       * End: Page for customizing a goal
       */
      configureMobGoal1EnderDragonFixedOrder(): Chainable<Subject>

      /**
       * Go from a specific criteria page to the main page.
       *
       * Start: Page for customizing a criteria
       * End: Main Page
       */
      backToMainPage(): Chainable<Subject>
    }
  }
}




