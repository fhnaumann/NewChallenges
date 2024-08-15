import type { PunishableRuleConfig } from './rules'
import type { BaseGoalConfig, CollectableEntryConfig, Orderable } from './goals'

export interface NoBlockBreakRuleConfig extends PunishableRuleConfig {
  /**
   * List of materials that are exempted from the rule.
   * @default []
   */
  exemptions?: string[]
}

export interface BlockBreakGoalConfig extends BaseGoalConfig, Orderable {
  /**
   * The blocks (more specifically materials) that need to be broken to beat this goal.
   *
   * @default []
   */
  broken?: CollectableEntryConfig[]
}