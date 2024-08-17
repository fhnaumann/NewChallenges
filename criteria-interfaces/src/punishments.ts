export type PunishmentName = keyof PunishmentsConfig
export type Affects = "causer" | "all"

export interface PunishmentsConfig {
  cancelPunishment?: CancelPunishmentConfig
  endPunishment?: EndPunishmentConfig,
  healthPunishment?: HealthPunishmentConfig,
  deathPunishment?: DeathPunishmentConfig,
  randomEffectPunishment?: RandomEffectPunishmentConfig,
  randomItemPunishment?: RandomItemPunishmentConfig,
  mlgPunishment?: MLGPunishmentConfig
}

export interface BasePunishmentConfig {
  /**
   * Who is affected by the punishment.
   *
   * @default "all"
   */
  affects?: Affects
}

export interface CancelPunishmentConfig {

}

export interface EndPunishmentConfig extends BasePunishmentConfig {

}
export interface HealthPunishmentConfig extends BasePunishmentConfig {
  /**
   * The amount of hearts (half hearts) that are subtracted.
   *
   * @minimum 1
   * @maximum 10
   * @default 1
   * @TJS-type integer
   */
  heartsLost?: number
  /**
   * If true, the amount of hearts (half hearts) that are subtracted per punishment are
   * randomized every time.
   *
   * @default false
   */
  randomizeHeartsLost?: boolean
}
export interface DeathPunishmentConfig extends BasePunishmentConfig {

}

export interface RandomEffectPunishmentConfig extends BasePunishmentConfig {
  /**
   * The number of effects that are applied at once.
   *
   * @minimum 1
   * @maximum 10
   * @default 1
   * @TJS-type integer
   */
  effectsAtOnce?: number

  /**
   * If true, the number of effects that are applied per punishment is randomized every time.
   *
   * @default false
   */
  randomizeEffectsAtOnce?: boolean
}

export interface RandomItemPunishmentConfig extends BasePunishmentConfig {
  /**
   * The number of items that are removed from the players inventory at once.
   * Inventory includes armor slots and the off-hand slot.
   *
   * @minimum 1
   * @maximum 10
   * @default 1
   * @TSJ-type integer
   */
  itemsRemovedAtOnce?: number
  /**
   * If true, the number of items that are removed from the players inventory per punishment is randomized every time.
   *
   * @default false
   */
  randomizeItemsRemovedAtOnce?: boolean
  /**
   * Flag that, if true, clears the entire inventory. This is preferred over
   * "itemsRemovedAtOnce" with the maximum value, since inventory size may vary
   * depending on other active rules/settings. Although, fundamentally it should not make
   * a difference.
   *
   * @default false
   */
  entireInventoryRemoved?: boolean
}

export interface MLGPunishmentConfig extends BasePunishmentConfig {

  /**
   * The height the MLG takes place.
   *
   * @minimum 10
   * @maximum 100
   * @default 50
   * @TSJ-type integer
   */
  height?: number
}

export interface PunishmentData {
  /**
   * List of
   */
  affected: string[]
}