import type { PunishableRuleConfig } from './rules'
import type { BaseGoalConfig, CollectableEntryConfig, Orderable } from './goals'

export interface NoBlockPlaceRuleConfig extends PunishableRuleConfig {
  /**
   * List of materials that are exempted from the rule.
   * @default []
   */
  exemptions?: string[]
}

export interface BlockPlaceGoalConfig extends BaseGoalConfig, Orderable {
  /**
   * The blocks (more specifically materials) that need to be placed to beat this goal.
   *
   * @default []
   */
  placed?: CollectableEntryConfig[]
}