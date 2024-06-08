import type { PunishableRuleConfig } from './rules'
import type { BaseGoalConfig } from './goals'

export interface NoDeathRuleConfig extends PunishableRuleConfig {

}

export interface DeathGoalConfig extends BaseGoalConfig {

  /**
   * The amount of deaths required to complete the goal.
   * @minimum 1
   * @maximum 1000
   * @default 1
   * @TJS-type integer
   */
  deathAmount?: number
}