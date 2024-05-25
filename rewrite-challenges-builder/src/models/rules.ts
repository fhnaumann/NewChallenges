import type { PunishmentsConfig } from './punishments'
import type { NoBlockBreakRuleConfig } from './blockbreak'
import type { NoMobKillRuleConfig } from '@/models/mob'

export type RuleName = keyof EnabledRules

export interface RulesConfig {
  enabledRules?: EnabledRules
  enabledGlobalPunishments?: PunishmentsConfig
}
export interface EnabledRules {
  noBlockBreak?: NoBlockBreakRuleConfig
  noMobKill?: NoMobKillRuleConfig
}

export interface BaseRuleConfig {}

export type Result = 'Deny' | 'Allow'

export interface PunishableRuleConfig extends BaseRuleConfig {
  punishments?: PunishmentsConfig
  /**
   * The result action when the rule is violated.
   *
   * @default "Deny"
   */
  result?: Result
}
