import type { CriteriaKey, CriteriaType } from '@/models/model'
import { useModelStore } from '@/stores/model'

export function getBgColor(criteriaType: CriteriaType, strength: 1 | 2 | 3): string {
  switch (criteriaType) {
    case 'rules':
      return `bg-red-${strength}00`
    case 'goals':
      return `bg-yellow-${strength}00`
    case 'settings':
      return `bg-gray-${strength}00`
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