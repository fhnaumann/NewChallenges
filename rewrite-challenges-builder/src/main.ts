import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import PrimeVue from 'primevue/config'
import i18n from '@/i18n'
import Lara from '@/assets/lara'
import Aura from '@/assets/aura'
import type { Model } from '@/models/model'
import DialogService from 'primevue/dialogservice';

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(i18n)
app.use(DialogService)
app.use(PrimeVue, {
  unstyled: true,
  pt: Aura,
})

app.mount('#app')

export interface ModelAccess<T> {
  get: (model: Model) => T | undefined
  where: string
  testSchematron: boolean
}
