import { useModelStore } from '@/stores/model'
import type { CriteriaKey, CriteriaType, Model } from '@/models/model'
import type { BaseRuleConfig, EnabledRules, RuleName, RulesConfig } from '@/models/rules'
import type { BaseGoalConfig, GoalName, GoalsConfig } from '@/models/goals'

export function useFetchable() {

  const model: Model = useModelStore().model

  function fetch<T extends BaseRuleConfig | BaseGoalConfig>(criteriaType: CriteriaType, criteriaCode: CriteriaKey, id: string): T {
    let submodel: any = model[criteriaType as keyof Model]
    if(criteriaType === 'rules') {
      submodel = (submodel as RulesConfig)['enabledRules']
    }
    const criterias: BaseGoalConfig[] | BaseRuleConfig[] = submodel[criteriaCode]
    const criteriaMatch = criterias.find((element) => element.id === id)
    if(criteriaMatch === undefined) {
      throw new Error(`${String(criteriaCode)} is not a valid criteria on ${criteriaType}!`)
    }
    return criteriaMatch as T
  }

  return { fetch }
}