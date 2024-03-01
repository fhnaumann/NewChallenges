import type { NoBlockBreakRuleConfig } from "./blockbreak"
import type { NoItemCollectRuleConfig } from "./item"
import type { NoMobKillRuleConfig } from "./mob"
import type { PunishmentsConfig } from "./punishments"

export type RuleName = keyof EnabledRules

export interface RulesConfig {
    enabledRules?: EnabledRules,
    enabledGlobalPunishments?: PunishmentsConfig
}
export interface EnabledRules {
    noBlockBreak?: NoBlockBreakRuleConfig,
    noMobKill?: NoMobKillRuleConfig,
    noItem?: NoItemCollectRuleConfig,
    noBlockPlace?: NoBlockPlaceRuleConfig,
    noCrafting?: NoCraftingRuleConfig,
    noDamage?: NoDamageRuleConfig,
    noDeath?: NoDeathRuleConfig
}


export interface BaseRuleConfig {

}
export interface PunishableRuleConfig extends BaseRuleConfig {
    punishments?: PunishmentsConfig
}
export interface NoBlockPlaceRuleConfig extends PunishableRuleConfig {
    /**
     * List of materials that are exempted from the rule.
     * @default []
     */
    exemptions?: string[]
}
export interface NoCraftingRuleConfig extends PunishableRuleConfig {
    /**
     * List of materials that are exempted from the rule.
     * @default []
     */
    exemptions?: string[]
}
export interface NoDamageRuleConfig extends PunishableRuleConfig {

}
export interface NoDeathRuleConfig extends PunishableRuleConfig {
    
}