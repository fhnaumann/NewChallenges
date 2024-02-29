import type { BaseForceConfig } from "./forces"
import type { BaseGoalConfig, Orderable, CollectableEntryConfig } from "./goals"
import type { PunishableRuleConfig } from "./rules"

export interface NoItemCollectRuleConfig extends PunishableRuleConfig {
    /**
     * List of materials that are exempted from the rule.
     * @default []
     */
    exemptions?: string[]
}

export interface ItemForceConfig extends BaseForceConfig, Omit<ItemGoalConfig, 'fixedOrder'> {
    
}

export interface ItemGoalConfig extends BaseGoalConfig, Orderable {
    /**
     * The items that need to be collected to beat this goal.
     * 
     * @default []
     */
    items?: CollectableEntryConfig[]
}