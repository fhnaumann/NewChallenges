import type { GoalName, GoalsConfig } from './goals'
import type { RuleName, RulesConfig } from './rules'
import type { SettingName, SettingsConfig } from './settings'

// running the command:
// typescript-json-schema src/components/model/model.ts Model > "challenges_schema.json" --noExtraProps --strictNullChecks --required --defaultProps
// NOTE: Any class that exports an interface may not import any other file (except for other interfaces)!

export interface Model {
  rules?: RulesConfig
  goals?: GoalsConfig
  settings?: SettingsConfig

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
