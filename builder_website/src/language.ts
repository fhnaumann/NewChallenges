import lang_en from '../public/language/en_us.json'
import death_messages_en from '@/assets/data_rows/death_messages_with_dummy_data_data_source_JSON.json'
import craftables_en from '@/assets/data_rows/craftables.json'
import type { DataRow } from 'criteria-interfaces'
import { ALL_MATERIAL_DATA, fromCode2DataRow } from '@/models/data_row_loaded'
import { useI18n } from 'vue-i18n'

export type Language = 'en' | 'de'

interface Translations {
  [translationKey: string]: {
    lang: string
  }
}

let selectedLanguage: Language = 'en'

export function useTranslation() {

  const { t } = useI18n()

  const language_data = {
    'en': Object.assign({},
      lang_en,
      death_messages_en.data.reduce((acc, { code, deathMessageWithDummyData }) => ({ ...acc, [code]: deathMessageWithDummyData }), {}),
    )
  }
  // assign later because it uses variables that are assigned above
  Object.assign(language_data.en, craftables_en.data.reduce((acc, { code, result, recipeType }) => ({ ...acc, [code]: `${translateDataRow(fromCode2DataRow(result))} from ${recipeType}` }), {}))

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
    if(dataRow.translation_key !== undefined) {
      return translate(dataRow.translation_key)
    }
    else {
      return translate(dataRow.code)
    }
  }

  function craftingType2DataRow(craftingType: string): DataRow {
    switch(craftingType) {
      case "crafting": return ALL_MATERIAL_DATA.find(value => value.code === 'crafting_table')!
      case "furnace": return ALL_MATERIAL_DATA.find(value => value.code === 'furnace')!
      case "blasting": return ALL_MATERIAL_DATA.find(value => value.code === 'blast_furnace')!
      case "campfire": return ALL_MATERIAL_DATA.find(value => value.code === 'campfire')!
      case "smithing": return ALL_MATERIAL_DATA.find(value => value.code === 'smithing_table')!
      case "smoking": return ALL_MATERIAL_DATA.find(value => value.code === 'smoker')!
      case "stonecutting": return ALL_MATERIAL_DATA.find(value => value.code === 'stonecutter')!
      default: return ALL_MATERIAL_DATA.find(value => value.code === 'paper')!
    }
  }

  return { translate, translateDataRow, craftingType2DataRow }
}
