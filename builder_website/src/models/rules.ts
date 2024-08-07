import type { PunishmentsConfig } from './punishments'
import type { NoBlockBreakRuleConfig } from './blockbreak'
import type { NoMobKillRuleConfig } from './mob'
import type { NoItemCollectRuleConfig } from './item'
import type { NoDeathRuleConfig } from './death'
import type { NoBlockPlaceRuleConfig} from './blockplace'

export type RuleName = keyof EnabledRules

export interface RulesConfig {
  enabledRules?: EnabledRules
  enabledGlobalPunishments?: PunishmentsConfig
}
export interface EnabledRules {
  noBlockPlace?: NoBlockPlaceRuleConfig
  noBlockBreak?: NoBlockBreakRuleConfig
  noMobKill?: NoMobKillRuleConfig
  noItem?: NoItemCollectRuleConfig
  noDeath?: NoDeathRuleConfig
}

export interface BaseRuleConfig {}

export type Result = 'Deny' | 'Allow'

export interface PunishableRuleConfig extends BaseRuleConfig {
  punishments?: PunishmentsConfig

}
