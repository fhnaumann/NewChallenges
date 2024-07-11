import lang_en from '../public/language/en_us.json'
import death_messages_en from '../public/language/death_messages_as_data_source_JSON.json'
import type { DataRow } from '@/models/data_row'

export type Language = 'en' | 'de'

interface Translations {
  [translationKey: string]: {
    lang: string
  }
}

let selectedLanguage: Language = 'en'

export function useTranslation() {

  const language_data = {
    'en': Object.assign({},
      lang_en,
      death_messages_en.data.reduce((acc, { code, deathMessageWithDummyData }) => ({ ...acc, [code]: deathMessageWithDummyData }), {}))
  }

  function translate(translationKey: string): string {
    try {
      return language_data[selectedLanguage][translationKey]
      /*
      if (selectedLanguage === 'en') {
        return lang_en[translationKey as keyof typeof lang_en]
      } else if (selectedLanguage === 'de') {
        return 'TODO German language'
      }

       */
    } catch (error) {
      throw new Error(
        `Failed to get translation key '${translationKey}' for language '${selectedLanguage}'. This is a bug!`,
      )
    }
    return 'LANGUAGE UNKNOWN'
  }

  function translateDataRow(dataRow: DataRow): string {
    if(dataRow.mc_translation_key !== undefined) {
      return translate(dataRow.mc_translation_key)
    }
    else {
      return translate(dataRow.code)
    }
  }

  return { translate, translateDataRow }
}
