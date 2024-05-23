import type { GoalName, GoalsConfig } from './goals'
import type { RuleName, RulesConfig } from './rules'
import type { SettingName, SettingsConfig } from './settings'

// running the command:
// typescript-json-schema src/models/model.ts Model > "src/assets/challenges_schema.json" --noExtraProps --strictNullChecks --required --defaultProps
// NOTE: Any class that exports an interface may not import any other file (except for other interfaces)!

export interface Model {
  rules?: RulesConfig
  goals?: GoalsConfig
  settings?: SettingsConfig

  /**
   * The number that represents the current order progress. Starts at 0 and increases as
   * goals with their order number are completed.
   * Start: Complete all goals with order number 0.
   * Next: Increase currentOrder if all goals with order number 0 are completed.
   * Next: Increase currentOrder if all goals with order number 1 are completed.
   * End: There are no challenges with a higher orderNumber left to complete.
   * currentOrder is only relevant if not -1. -1 indicates that the challenge has no time limit
   * and goals can be completed whenever (the challenge is over if all goals are completed).
   *
   * @default -1
   * @TJS-type integer
   */
  currentOrder?: number

  /**
   * The time (in seconds) that has passed since the challenge was started.
   *
   * @minimum 0
   * @default 0
   * @TJS-type integer
   */
  timer?: number

  nextChallenge?: Model
}

export type CriteriaKey = GoalName | RuleName | SettingName
export type CriteriaType = 'rules' | 'goals' | 'settings'
