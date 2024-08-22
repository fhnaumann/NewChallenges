import type { MinMaxRangeConfig } from './model'
import {DataConfig, PlayerConfig} from "./live";

export type SettingName = keyof SettingsConfig

export interface SettingsConfig {
  customHealthSetting?: CustomHealthSettingConfig
  ultraHardcoreSetting?: UltraHardcoreSettingConfig
  mlgSetting?: MLGSettingConfig
  floorIsLavaSetting?: FloorIsLavaSettingConfig
}

export interface BaseSettingConfig {

}

export interface CustomHealthSettingConfig extends BaseSettingConfig {

  /**
   * The maximum number of hearts the players play with.
   *
   * @minimum 1
   * @maximum 100
   * @default 20
   * @TJS-type integer
   */
  hearts?: number
}

export interface UltraHardcoreSettingConfig extends BaseSettingConfig {

  /**
   * Determines if natural regeneration is enabled. This flag essentially controls the NATURAL_REGENERATION gamerule.
   *
   * @default false
   */
  naturalRegeneration?: boolean

  /**
   * Determines if eating a golden apple provides regeneration.
   *
   * @default true
   */
  regWithGoldenApples?: boolean

  /**
   * Determines if eating an enchanted golden apple provides regeneration.
   *
   * @default true
   */
  regWithEnchantedGoldenApples?: boolean

  /**
   * Determines if eating a suspicious stew (crafted with an oxeye daisy) provided regeneration.
   *
   * @default true
   */
  regWithSuspiciousStew?: boolean

  /**
   * Determines if using a healing/instant-health (splash-)potion provides regeneration.
   *
   * @default true
   */
  regWithPotions?: boolean

  /**
   * Determines if absorption hearts (yellow) should be allowed or immediately removed. They can be gained most noticeably
   * by eating a golden apple, an enchanted golden apple or by activating a totem.
   *
   * @default true
   */
  allowAbsorptionHearts?: boolean

  /**
   * Determines if totems work.
   *
   * @default true
   */
  allowTotems?: boolean
}

export interface MLGSettingConfig extends BaseSettingConfig, MinMaxRangeConfig {

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

export interface MLGSettingDataConfig {
  /**
   * Indicates if all players beat the MLG.
   */
  success: boolean

  /**
   * List of players that failed the MLG
   */
  failedMLG: PlayerConfig[]
}

export interface FloorIsLavaSettingConfig extends BaseSettingConfig {

  /**
   * The time until regular block -> magma block OR magma block -> lava in ticks.
   *
   * @minimum 10
   * @maximum 200
   * @default 30
   * @TSJ-type integer
   */
  timeToNextBlockChangeInTicks?: number

  /**
   * Determines if lava remains permanently or is transformed back to the original block.
   *
   * @default false
   */
  lavaRemainsPermanently?: boolean
}