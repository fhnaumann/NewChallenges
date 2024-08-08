import type { PunishableRuleConfig } from './rules'
import type { BaseGoalConfig, CollectableEntryConfig, Orderable } from './goals'

export interface NoCraftingRuleConfig extends PunishableRuleConfig {
  /**
   * List of crafting results (materials) that are exempted from the rule.
   *
   * @default []
   */
  exemptions?: string[]

  /**
   * Determines if crafting in the internal 2x2 crafting grid is allowed or not.
   *
   * @default false
   */
  internalCrafting?: boolean,

  /**
   * Determines if crafting in the 3x3 crafting grid is allowed or not.
   *
   * @default false
   */
  workbenchCrafting?: boolean,

  /**
   * Determines if smelting an item in a furnace (furnace, blast-furnace, smoker) is allowed or not.
   *
   * @default true
   */
  furnaceSmelting?: boolean

  /**
   * Determines if cooking an item in a campfire is allowed or not.
   *
   * @default true
   */
  campfireCooking?: boolean

  /**
   * Determines if using a smithing table is allowed or not.
   *
   * @default true
   */
  smithing?: boolean

  /**
   * Determines if using a stonecutter is allowed or not.
   *
   * @default false
   */
  stonecutter?: boolean
}

export interface CraftingGoalConfig extends BaseGoalConfig, Orderable {

  /**
   * The crafting recipes that need to be crafted to beat this goal. This also includes smelting, cooking over a campfire, etc.
   *
   * @default []
   */
  crafted?: CollectableEntryConfig[]
}