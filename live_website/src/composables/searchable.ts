import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import type { CriteriaKey, CriteriaType } from 'criteria-interfaces'
import { useTranslation } from '@/composables/language'
import { fromCode2DataRow } from '@/composables/data_row_loaded'

export interface Searchable {
  criteriaType: CriteriaType
  criteriaKey: CriteriaKey
  alias: string[]
}
export interface CodeSearchable {
  code: string
}

export function useSearchable<T>(options: Searchable[] | CriteriaType | CodeSearchable[] | T[] | undefined, accessor: (value: T) => string) {


  const searchFieldValue = ref<string>('')

  function getPartialMatches(): T[] {
    return (options as T[]).filter(value => accessor(value).toLowerCase().includes(searchFieldValue.value.toLowerCase()))
  }

  return { searchFieldValue, getPartialMatches }
}