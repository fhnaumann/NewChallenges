import type { GoalName } from '../model/goals'
import type { Searchable } from '../searchable'

export interface GoalView extends Searchable {
  /**The internal code that is used for building and accessing paths in the configuration store/file */
  id: GoalName
  /** The description that is displayed to the user below the label. */
  description: string
  /** The path to the associated image. */
  image?: string
}

export type GoalsView = {
    allgoals: {
        [goalName in GoalName]: GoalView
    }
}
