import type { BaseGoalConfig, CollectableDataConfig } from './goals'

export interface MLGGoalConfig extends BaseGoalConfig {

  /**
   * The number of MLGs that need to be (successfully) completed to beat this goal.
   *
   * @default {}
   */
  numberOfMLGs: CollectableDataConfig

  /**
   * The height the MLG takes place.
   *
   * @minimum 10
   * @maximum 100
   * @default 50
   * @TSJ-type integer
   */
  height: number

  /**
   * The lower bound for determining when to trigger an MLG in seconds.
   *
   * @minimum 60
   * @maximum 86400
   * @default 180
   * @TSJ-type integer
   */
  minTime: number

  /**
   * The upper bound for determining when to trigger an MLG in seconds.
   *
   * @minimum 60
   * @maximum 86400
   * @default 600
   * @TSJ-type integer
   */
  maxTime: number
}