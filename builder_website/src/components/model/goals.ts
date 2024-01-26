export type GoalName = keyof GoalsConfig

export interface GoalsConfig {
    mobGoal?: MobGoalConfig
    itemGoal?: ItemGoalConfig
}

interface BaseGoalConfig {

    /**
     * If the goal is completed.
     * 
     * @default false
     */
    complete?: boolean
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
    amountNeeded: number

    /**
     * The amount that is currently collected.
     *
     * @default 0
     * @TJS-type integer
     */
    currentAmount: number
}
export interface CollectableEntryConfig {
    [collectableName: string]: CollectableDataConfig
}
export interface MobGoalConfig extends BaseGoalConfig {
    /**
     * The mobs that need to be killed to beat this goal.
     * 
     * @default {"ENDER_DRAGON": {"amount": 1}}
     */
    mobs: CollectableEntryConfig
}

export interface ItemGoalConfig extends BaseGoalConfig {
    /**
     * The items that need to be collected to beat this goal.
     */
    items: CollectableEntryConfig

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