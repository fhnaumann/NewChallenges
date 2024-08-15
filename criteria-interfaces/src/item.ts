import type { PunishableRuleConfig } from './rules'
import type { BaseGoalConfig, CollectableEntryConfig, Orderable } from './goals'

export interface NoItemCollectRuleConfig extends PunishableRuleConfig {
  /**
   * List of materials that are exempted from the rule.
   * @default []
   */
  exemptions?: string[]
}

export interface ItemGoalConfig extends BaseGoalConfig, Orderable {
  /**
   * The items that need to be collected to beat this goal.
   *
   * @default []
   */
  items?: CollectableEntryConfig[]
}