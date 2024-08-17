import type { GoalName, GoalsConfig } from './goals'
import type { RuleName, RulesConfig } from './rules'
import type { SettingName, SettingsConfig } from './settings'

// running the command:
// typescript-json-schema src/models/model.ts Model > "src/assets/challenges_schema.json" --noExtraProps --strictNullChecks --required --defaultNumberType=integer
// NOTE: Any class that exports an interface may not import any other file (except for other interfaces)!

export type CriteriaKey = GoalName | RuleName | SettingName
export type CriteriaType = 'rules' | 'goals' | 'settings'

export interface Model {
  rules?: RulesConfig
  goals?: GoalsConfig
  settings?: SettingsConfig

  /**
   * Contains the team names for this challenge. If at least one team exists, then the goals from the goals config
   * will be moved to here, where each team has their own goals' config.
   * In a way this list may be used check if this challenge has teams:
   * List is empty     -> No teams, everything goal related is in the goals property
   * List is not empty -> Teams, everything goal related is in the goals property for the individual teams. The "global"
   *                      goals property is empty/undefined.
   *
   * @default []
   */
  teams?: TeamConfig[]

  /**
   * ONLY RELEVANT IF THERE ARE NO TEAMS! Otherwise, look for the same property in an individual team.
   *
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

  /**
   * Metadata about the challenge itself and how it was configured and built.
   */
  metadata: ChallengeMetadata

  nextChallenge?: Model
}

export interface ChallengeMetadata {

  /**
   * Unique identifies the challenge across all challenges.
   */
  challengeID: string

  /**
   * Name of the challenge. Can be set by the user. Used to differentiate between challenges and to load a specific challenge.
   */
  name: string,

  /**
   * When the challenge was first initially created. Is always set by the challenge builder.
   *
   * @TJS-format date-time
   */
  whenCreated?: string,

  /**
   * When the challenge was last modified. Is always set by the challenge builder.
   *
   * @TJS-format date-time
   */
  lastModified?: string

  /**
   * Author of the challenge. The user who configured and built the challenge.
   *
   * @default "-"
   */
  createdBy: string

  /**
   * Version of the challenge builder used at the time of creation.
   */
  builderVersion: string

  /**
   * Version of the minecraft plugin used at the time of loading the first time.
   */
  pluginVersion: string

  /**
   * MC Version the challenge builder targets at the time of creation.
   */
  builderMCVersion: string

  /**
   * MC Version the minecraft plugin targets at the time of loading the first time.
   */
  pluginMCVersion: string,
}

export interface MinMaxRangeConfig {

  /**
   * The lower bound.
   *
   * @minimum 10
   * @maximum 86400
   * @default 180
   * @TSJ-type integer
   */
  minTimeSeconds?: number

  /**
   * The upper bound.
   *
   * @minimum 10
   * @maximum 86400
   * @default 600
   * @TSJ-type integer
   */
  maxTimeSeconds?: number
}

export interface TeamConfig {
  teamName: string
  /**
   * The UUIDs of the players that are in this team.
   *
   * @default []
   */
  playerUUIDs?: string[]
  goals: GoalsConfig

  /**
   * ONLY RELEVANT IF THERE ARE TEAMS! Otherwise, look for the same property in the "global" goals config.
   *
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
}

export interface MCEvent<T> {

  /**
   * The unique ID to identify the challenge this event originated from.
   */
  challengeID: string,

  /**
   * The unique ID to identify the event in this challenge.
   */
  eventID: string,

  /**
   * @TSJ-type integer
   */
  timestamp: number,

  /**
   * The event type. Depending on the type, additional data may be transmitted.
   */
  eventType: CriteriaKey

  /**
   * Potential additional data involved.
   */
  data?: T
}

export interface Data {
  playerUUID: string
}



