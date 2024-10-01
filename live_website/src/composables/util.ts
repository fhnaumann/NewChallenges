import type { DataConfig, MCEvent } from '@criteria-interfaces/live'

export function useUtil() {

  function getCriteriaColorFrom(mcEvent: MCEvent<any>): string {
    if (mcEvent.eventType.endsWith('Goal')) {
      return 'customized-goal'
    }
    if (mcEvent.eventType.startsWith('no') || mcEvent.eventType.endsWith('Rule')) {
      return 'customized-rule'
    }
    if (mcEvent.eventType.endsWith('Setting')) {
      return 'customized-setting'
    }
  }
  
  function hasAppliedPunishments(data: any): boolean {
    return data && typeof data.appliedPunishments !== "undefined"
  }

  return {
    getCriteriaColorFrom,
    hasAppliedPunishments
  }
}