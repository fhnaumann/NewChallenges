import type { PunishableRuleConfig } from '@/models/rules'
import type { BaseGoalConfig, CollectableEntryConfig, Orderable } from '@/models/goals'

export interface NoMobKillRuleConfig extends PunishableRuleConfig {
  /**
   * List of entity types that are exempted from the rule.
   * @default []
   */
  exemptions?: string[]
}

export interface MobGoalConfig extends BaseGoalConfig, Orderable {
  /**
   * The mobs that need to be killed to beat this goal.
   *
   * @default []
   */
  mobs?: CollectableEntryConfig[]
}