import type { BlockBreakGoalConfig } from "./blockbreak"

export type GoalName = keyof GoalsConfig

export type GoalPathSplitKey = keyof (MobGoalConfig & ItemGoalConfig & BlockBreakGoalConfig)

export interface GoalsConfig {
    mobGoal?: MobGoalConfig
    itemGoal?: ItemGoalConfig
    blockbreakGoal?: BlockBreakGoalConfig
}

export interface BaseGoalConfig {

    /**
     * If the goal is completed.
     * 
     * @default false
     */
    complete?: boolean
}

export interface Orderable {
    /**
     * If true, all selected elements have to be collected/killed in a specific order.
     * 
     * @default false
     */
    fixedOrder?: boolean
}

export interface CollectableDataConfig {
    /**
     * The amount that needs to be collected.
     * 
     * @minimum 1
     * @maximum 100
     * @default 1
     * @TJS-type integer
     */
    amountNeeded?: number

    /**
     * The amount that is currently collected.
     *
     * @default 0
     * @TJS-type integer
     */
    currentAmount?: number
}
export interface CollectableEntryConfig {
    /**
     * The name of the collectable. This could, for example, be "PIG" (entity), "STONE" (material).
     */
    collectableName: string
    /**
     * The data that is meant to be collected for this specific collectable
     * 
     * @default {}
     */
    collectableData?: CollectableDataConfig
}
export interface MobGoalConfig extends BaseGoalConfig, Orderable {
    /**
     * The mobs that need to be killed to beat this goal.
     * 
     * @default ["collectableName": "ENDER_DRAGON", "collectableData": {"amountNeeded": 1}]
     */
    mobs?: CollectableEntryConfig[]
}

export interface ItemGoalConfig extends BaseGoalConfig, Orderable {
    /**
     * The items that need to be collected to beat this goal.
     */
    items?: CollectableEntryConfig

    /**
     * If true, all items have to be collected.
     * 
     * @default true
     */
    allItems?: boolean

    /**
     * If true, all items that are placable blocks (determined by 
     * {@link https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html#isBlock()|Spigots isBlock method})
     * have to be collected.
     * 
     * @default false
     */
    allBlocks?: boolean
}