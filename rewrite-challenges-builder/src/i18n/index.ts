import { createI18n } from 'vue-i18n'
import en_punishments from '@/i18n/locales/en/punishments.json'
import en_rules from '@/i18n/locales/en/rules.json'
import en_general from '@/i18n/locales/en/general.json'
import en_settings from '@/i18n/locales/en/settings.json'
import en_goals from '@/i18n/locales/en/goals.json'

const i18n = createI18n({
  legacy: false,
  locale: 'en',
  messages: {
    en: Object.assign({}, en_general, en_punishments, en_rules, en_goals, en_settings),
  }
})

export default i18n