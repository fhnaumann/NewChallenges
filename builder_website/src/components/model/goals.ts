import type { BlockBreakGoalConfig } from "./blockbreak"
import type { ItemGoalConfig } from "./item"
import type { Base } from "./model"

export type GoalName = keyof GoalsConfig

export type GoalPathSplitKey = keyof (MobGoalConfig & ItemGoalConfig & BlockBreakGoalConfig)

export interface GoalsConfig {
    mobGoal?: MobGoalConfig
    itemGoal?: ItemGoalConfig
    blockbreakGoal?: BlockBreakGoalConfig
}

export interface BaseGoalConfig extends Base {

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

    /**
     * Flag to remember whether the collectables have been shuffled or not.
     * Can only ever be true if 'fixedOrder' is set to true.
     * The builder website will always set this value to false (or leave it as default).
     * Coming from the builder website to the minecraft server for the first time, then 'shuffled'
     * will be false, which indicates to the server to shuffle the collectables once, set 'shuffled'
     * to true and therefore never re-shuffle it again (on subsequent server starts).
     * 
     * @default false
     */
    shuffled?: boolean
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
    collectableData: CollectableDataConfig
}
export interface MobGoalConfig extends BaseGoalConfig, Orderable {
    /**
     * The mobs that need to be killed to beat this goal.
     * 
     * @default ["collectableName": "ENDER_DRAGON", "collectableData": {"amountNeeded": 1}]
     */
    mobs?: CollectableEntryConfig[]
}