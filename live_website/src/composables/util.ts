import type { DataConfig, MCEvent } from '@fhnaumann/criteria-interfaces'

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
    return ''
  }
  
  function hasAppliedPunishments(data: any): boolean {
    console.log("HAS PUNISHMENT:")
    console.log(data)
    return data && typeof data.appliedPunishments !== "undefined"
  }

  return {
    getCriteriaColorFrom,
    hasAppliedPunishments
  }
}