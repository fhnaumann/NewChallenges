import type { CriteriaKey, CriteriaType } from '@/models/model'
import { useModelStore } from '@/stores/model'

export function getBgColor(criteriaType: CriteriaType, hover: boolean): string {
  switch (criteriaType) {
    case 'rules':
      return hover ? 'bg-rule-100 hover:bg-rule-200' : 'bg-rule-100'
    case 'goals':
      return hover ? `bg-goal-100 hover:bg-goal-200` : 'bg-goal-100'
    case 'settings':
      return hover ? `bg-setting-100 hover:bg-setting-200` : 'bg-setting-100'
    default:
      return 'bg-red-100'
  }
}

export function getTextColor(criteriaType: CriteriaType): string {
  switch(criteriaType) {
    case 'rules': return `text-red-900`
    case 'goals': return `text-yellow-900`
    case 'settings': return `text-gray-900`
    default: return `text-red-900`
  }
}

export function deleteCriteria(criteriaType: CriteriaType, criteriaCode: CriteriaKey) {
  const { set } = useModelStore()
  console.log("deleting", criteriaType, criteriaCode)
  if(criteriaType === 'rules') {
    console.log("deleting rule", criteriaCode)
    set(`rules.enabledRules.${criteriaCode}`, undefined, false)
  }
  else {
    console.log("deleting else", criteriaCode)
    set(`${criteriaType}.${criteriaCode}`, undefined, false)
  }
}