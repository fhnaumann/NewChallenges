import type { BaseGoalConfig, CollectableEntryConfig, Orderable } from "./goals";
import type { PunishableRuleConfig } from "./rules";

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
     * @default [{
            "collectableName": "stone",
            "collectableData": {
                "amountNeeded": 1,
                "currentAmount": 0
            }
        }]
     */
    broken?: CollectableEntryConfig[]

    /**
     * If true, all blocks have to be broken (not just in the inventory) exactly once.
     * 
     * @default false
     */
    allBlocks?: boolean
}