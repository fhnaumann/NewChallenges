import type { PunishableRuleConfig } from './rules'
import type { BaseGoalConfig, CollectableEntryConfig, Orderable } from './goals'
import {DataConfig} from "./live";

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

export interface MobDataConfig extends DataConfig {

  /**
   * The entity (as an entity type code) that was interacted with.
   */
  mob: string
}