export type SettingName = keyof SettingsConfig

export interface SettingsConfig {
  customHealthSetting?: CustomHealthSettingConfig
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