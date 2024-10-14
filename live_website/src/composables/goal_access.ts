import type { Model } from '@fhnaumann/criteria-interfaces'
import type { BaseGoalConfig, GoalName } from '@fhnaumann/criteria-interfaces'

export function useGoalAccess(challengeFile: Model) {

  function hasTeams() {
    return challengeFile.teams !== undefined && challengeFile.teams.length > 0
  }

  return {
    hasTeams
  }

}