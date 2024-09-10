import type { MCEvent } from '@criteria-interfaces/live'

export function useUtil() {

  function getCriteriaColorFrom(mcEvent: MCEvent<any>): string {
    if (mcEvent.eventType.endsWith('Goal')) {
      return 'customized-goal'
    }
    if (mcEvent.eventType.endsWith('Rule')) {
      return 'customized-rule'
    }
    if (mcEvent.eventType.endsWith('Setting')) {
      return 'customized-setting'
    }
  }


  return {
    getCriteriaColorFrom
  }
}