import {PunishableRuleConfig, RuleDataConfig} from './rules'
import type { BaseGoalConfig, CollectableEntryConfig, Orderable } from './goals'
import {DataConfig} from "./live";

export interface NoBlockBreakRuleConfig extends PunishableRuleConfig {
  /**
   * List of materials that are exempted from the rule.
   * @default []
   */
  exemptions?: string[]
}

export interface BlockBreakGoalConfig extends BaseGoalConfig, Orderable {
  /**
   * The blocks (more specifically materials) that need to be broken to beat this goal.
   *
   * @default []
   */
  broken?: CollectableEntryConfig[]
}

export interface BlockBreakDataConfig extends DataConfig {
  /**
   * The block (as a material code) that was broken.
   */
  broken: string
}
export interface NoBlockBreakRuleDataConfig extends BlockBreakDataConfig, RuleDataConfig {

}