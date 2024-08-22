import './assets/main.css'
import PrimeVue from 'primevue/config'
import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import Aura from '@/assets/aura/'
import router from './router'
import i18n from '@/i18n'
import Tooltip from 'primevue/tooltip'

const app = createApp(App)

app.use(createPinia())
app.use(i18n)
app.use(router)
app.use(PrimeVue, {
  unstyled: true,
  pt: Aura
})
app.directive('tooltip', Tooltip)

app.mount('#app')
