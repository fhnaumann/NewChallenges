import lang_en from '../assets/en_us.json'
import type { DataRow } from './loadableDataRow'


export type Language = 'en' | 'de'

interface Translations {
    [translationKey: string]: {
        lang: string
    }
}

let selectedLanguage: Language = 'en'

export function useTranslation() {

    function translate(translationKey: string): string {
        try {
            if(selectedLanguage === 'en') {
                return lang_en[translationKey as keyof typeof lang_en]
            }
            else if(selectedLanguage === 'de') {
                return 'TODO German language'
            }
        } catch(error) {
            throw new Error(`Failed to get translation key '${translationKey}' for language '${selectedLanguage}'. This is a bug!`)
        }
        return 'LANGAUGE UNKNOWN'
    }

    function translateDataRow(dataRow: DataRow): string {
        return translate(dataRow.translation_key)
    }

    return { translate, translateDataRow }

}