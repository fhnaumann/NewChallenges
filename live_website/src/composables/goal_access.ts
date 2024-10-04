import type { Model } from '@criteria-interfaces/model'
import type { BaseGoalConfig, GoalName } from '@criteria-interfaces/goals'

export function useGoalAccess(challengeFile: Model) {
  function getGoal(goalName: GoalName): BaseGoalConfig {
    //let goalConfig: BaseGoalConfig | undefined = hasTeams() ? challengeFile : challengeFile.goals[goalName]
  }

  function hasTeams() {
    return challengeFile.teams !== undefined && challengeFile.teams.length > 0
  }

  return {
    hasTeams
  }

}