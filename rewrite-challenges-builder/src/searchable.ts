import { useModelStore } from '@/stores/model'
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import type { CriteriaKey, CriteriaType } from '@/models/model'

export interface Searchable {
  criteriaType: CriteriaType
  criteriaKey: CriteriaKey
  alias: string[]
}

export function useSearchable(options: Searchable[] | undefined) {

  const i18n = useI18n()
  const selectedLangMessages = i18n.messages.value[i18n.locale.value]

  if (options === undefined) {
    options = getAllSearchOptions()
  }

  console.log("goals", getLabelFor('goals'))
  console.log("rules", getLabelFor('rules'))
  console.log("settings", getLabelFor('settings'))
  console.log("early combined", getAllSearchOptions())
  const searchFieldValue = ref<string>('')

  function getPartialMatches(): Searchable[] {
    if (!searchFieldValue.value.trim()) {
      return options!
    } else {
      return options!.filter((searchable) =>
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