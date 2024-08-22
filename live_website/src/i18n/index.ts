import { createI18n } from 'vue-i18n'
import en from '@/i18n/locales/en/en.json'

const i18n = createI18n({
  legacy: false,
  locale: 'en',
  messages: {
    en: Object.assign({}, en),
  }
})

export default i18n