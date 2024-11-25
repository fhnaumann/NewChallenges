import type { PunishableRuleConfig } from './rules'
import type { BaseGoalConfig, CollectableEntryConfig, Orderable } from './goals'
import {DataConfig} from "./live";

export interface NoItemCollectRuleConfig extends PunishableRuleConfig {
  /**
   * List of materials that are exempted from the rule.
   * @default []
   */
  exemptions?: string[]
}

export interface ItemGoalConfig extends BaseGoalConfig, Orderable {
  /**
   * The items that need to be collected to beat this goal.
   *
   * @default []
   */
  items?: CollectableEntryConfig[]
}

export interface ItemDataConfig extends DataConfig {
  /**
   * The item (as a material code) that was interacted with.
   */
  item: string
}