import type { BlockBreakGoalConfig } from './blockbreak'
import type { MobGoalConfig } from '@/models/mob'

export type GoalName = keyof GoalsConfig

export interface GoalsConfig {
  blockBreakGoal?: BlockBreakGoalConfig
  mobGoal?: MobGoalConfig
}

export interface BaseGoalConfig extends Timeable {
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

export interface Timeable {
  /**
   * The lower bound for determining the time that is allowed to complete a goal.
   * Cannot be bigger than maxTimeSeconds.
   *
   * @minimum 10
   * @maximum 86400
   * @default 180
   * @TSJ-type integer
   */
  minTimeSeconds?: number

  /**
   * The upper bound for determining the time that is allowed to complete a goal.
   * Cannot be smaller than minTimeSeconds.
   *
   * @minimum 10
   * @maximum 86400
   * @default 600
   * @TSJ-type integer
   */
  maxTimeSeconds?: number

  /**
   * Defines the ordering of other timeable goals within the same challenge.
   * Goals with smaller order values are required to be completed before goals with
   * larger ones. Equal order value means that they have to be completed simultaneously.
   * The order number (= the progress in the challenge) is incremented when
   * all goals for the "current" order number are completed. If multiple goals are currently
   * running simultaneously, then it only increases if all of these are completed.
   * Goals with no order number (= order number is -1) don't have a time limit and can be
   * completed at any time during the active challenge.
   *
   * @default -1
   */
  order?: number
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

  /**
   * Contains information about the completion progress. This includes player names and the amount each player has contributed to the completion of this collectable.
   */
  completion?: CompletionConfig
}
export interface CompletionConfig {
  /**
   * The time in seconds (since the start) that it took to complete this collectable. -1 indicates that is has not been collected (e.g. 'complete' is false).
   *
   * @default -1
   * @TJS-type integer
   */
  whenCollectedSeconds: number

  /**
   * The player (names) that contributed to completing this collectable.
   */
  contributors?: ContributorsConfig
}
export interface ContributorsConfig {
  [key: string]: number
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
