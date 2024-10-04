import type { MCEvent } from '@criteria-interfaces/live'
import type { Model } from '@criteria-interfaces/model'
import { useChallengeState } from '@/stores/challenge_state'
import { useGoalAccess } from '@/composables/goal_access'
import type { BaseGoalConfig, CollectableEntryConfig, GoalName } from '@criteria-interfaces/goals'

export function useSanitizer() {

  const { challengeFileJSON, events } = useChallengeState()
  const { hasTeams } = useGoalAccess(challengeFileJSON!)

  function addMissingGoalEvents() {
    /*
    We need to check if the events match the structure of the challenge file. If not, then that indicates
    that some events got corrupted/were deleted. In this case we need to sync them together.
    This is done by calculating the events that are missing (the challenge file is recognized as
    the "ground truth"). These events are then artificially added in.
    If the challenge does not use teams:
    Get ground truth information from "challenge.goals"
    If the challenges *USES* teams:
    Get ground truth information from each teams individual goal progress.
    */
    if(!challengeFileJSON!.goals) {
      return
    }
    Object.entries(challengeFileJSON!.goals!).forEach(value => ([key,  value]) => {
      const goalName = key as GoalName
      const goalConfig = value as BaseGoalConfig
    })

  }

  function addMisingEventWithInformationFrom(goal: CollectableEntryConfig) {

  }

  return {
    addMissingGoalEvents
  }

}