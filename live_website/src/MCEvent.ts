import type { GoalName } from 'criteria-interfaces'
import type { RuleName } from 'criteria-interfaces'
import type { SettingName } from 'criteria-interfaces'
import type { PunishmentName } from 'criteria-interfaces'

export type CriteriaKey = RuleName | GoalName | SettingName | PunishmentName

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

  data: T
}

export interface Data {
  playerUUID: string
}

export interface BlockBreakData extends Data {
  blockBroken: string,
  amount: number
}