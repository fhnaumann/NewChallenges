import type { PunishableRuleConfig } from './rules'
import type { BaseGoalConfig, CollectableDataConfig, CollectableEntryConfig, Orderable } from './goals'

export interface NoDeathRuleConfig extends PunishableRuleConfig {

  /**
   * Determines if the usage of a totem triggers the punishments or not.
   * true = no punishments are triggered
   * false = punishments are triggered
   *
   * @default true
   */
  ignoreTotem?: boolean
}

export interface DeathGoalConfig extends BaseGoalConfig, Orderable {

  /**
   * The amount of deaths required to complete the goal.
   *
   * @default {}
   */
  deathAmount?: CollectableDataConfig

  /**
   * Determines if the usage of a totem counts towards the deathAmount.
   *
   * @default false
   */
  countTotem?: boolean

  /**
   * The death messages that need to be reached to beat this goal.
   *
   * @default []
   */
  deathMessages?: CollectableEntryConfig[]
}