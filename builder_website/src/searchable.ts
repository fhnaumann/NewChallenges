import { useModelStore } from '@/stores/model'
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import type { CriteriaKey, CriteriaType } from '@/models/model'

export interface Searchable {
  criteriaType: CriteriaType
  criteriaKey: CriteriaKey
  alias: string[]
}

export function useSearchable(options: Searchable[] | CriteriaType | undefined) {

  const i18n = useI18n()
  const selectedLangMessages = i18n.messages.value[i18n.locale.value]

  if (options === undefined) {
    options = getAllSearchOptions()
  }
  if(["rules", "settings", "goals"].includes(options as CriteriaType)) {
    options = getLabelFor(options as CriteriaType)
  }
  const searchFieldValue = ref<string>('')

  function getPartialMatches(): Searchable[] {
    if (!searchFieldValue.value.trim()) {
      return options! as Searchable[]
    } else {
      return (options! as Searchable[]).filter((searchable) =>
        searchable.alias.some(value => value.toLowerCase().includes(searchFieldValue.value.toLowerCase())),
      )
    }
  }

  function getAllSearchOptions(): Searchable[] {
    return ([] as Searchable[]).concat(getLabelFor('rules'), getLabelFor('goals'), getLabelFor('settings'))
  }

  function getLabelFor(labelFor: CriteriaType) {
    return Object.entries(selectedLangMessages[labelFor]['types'])
      .map(([key, value]) => {
        return {
          criteriaType: labelFor,
          criteriaKey: key as CriteriaKey,
          alias: (value as string[])["alias"]
        } as Searchable
      })
  }

  return { searchFieldValue, getPartialMatches }
}