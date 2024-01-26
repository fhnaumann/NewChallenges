import type { RuleName } from "../model/rules";
import type { Searchable } from "../searchable";


export interface RuleView extends Searchable {
    /**The internal code that is used for building and accessing paths in the configuration store/file */
    id: RuleName,
    /** The description that is displayed to the user below the label. */
    description: string,
    /** The path to the associated image. */
    image?: string
}
/**
 * Used to enforce type safety onto the store that contains every rulesView once
 */
export type RulesView = {
    allrules: {
        [ruleName in RuleName]: RuleView
    }
}