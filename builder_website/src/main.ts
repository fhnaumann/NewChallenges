import { createApp, ref, watch, computed, toRaw, nextTick, watchEffect } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import PrimeVue from 'primevue/config'
import DialogService from 'primevue/dialogservice'
import ToastService from 'primevue/toastservice'
import './assets/tailwind.css'
import Tooltip from 'primevue/tooltip';
import { defineStore } from 'pinia'
import type { Model } from './components/model/model'
import type { NoBlockBreakRule } from './components/model/rules'
import type { RuleName } from './components/model/rules'
import type { MobGoal } from './components/model/goals'
import type { RulesView } from './components/view/rules'
import modelSchema from '../test-output-schema.json'
import type { PunishmentsView } from './components/view/punishments'
import type { GoalsView } from './components/view/goals'
import ConfirmationService from 'primevue/confirmationservice';
import { useValidator } from './components/validator'

const pinia = createPinia()
const app = createApp(App)

app.use(pinia)
app.use(PrimeVue)
app.use(DialogService)
app.use(ConfirmationService)
app.use(ToastService)

app.directive('tooltip', Tooltip);

export const toast = app.config.globalProperties.$toast

export const useJSONSchemaConfigStore = defineStore('JSONSchemaConfig', {
    state: () => (modelSchema.definitions)
})


const defaultModel: Model = {
    rules: {
        enabledRules: {
            noBlockBreak: {
                exemptions: []
            },
            noBlockPlace: {
                exemptions: []
            },
            noCrafting: {
                exemptions: []
            },
            noDamage: {

            },
            noDeath: {
            
            }
        },
        enabledGlobalPunishments: {
            endPunishment: {
                affects: 'All'
            },
            healthPunishment: {
                affects: 'All'
            }
        }
    },
    goals: {
        mobGoal: {
            mobs: {}
        },
        itemGoal: {
            items: {}
        }
    }
}

export const useDefaultConfigStore = defineStore('defaultConfig', {
    state: () => (defaultModel)
})


const rulesView: RulesView = {
    allrules: {
        noBlockBreak: {
            id: 'noBlockBreak',
            label: 'NoBlockBreak',
            description: 'Breaking blocks is prohibited!',
        },
        noBlockPlace: {
            id: 'noBlockPlace',
            label: "NoBlockPlace",
            description: 'Placing blocks is prohibited!'
        },
        noCrafting: {
            id: 'noCrafting',
            label: "NoCrafting",
            description: 'Crafting items (2x2 inventory and 3x3 crafting table) is prohibited!'
        },
        noDamage: {
            id: 'noDamage',
            label: "NoDamage",
            description: 'Taking damage from any source is prohibited!'
        },
        noDeath: {
            id: 'noDeath',
            label: "NoDeath",
            description: 'Dying is prohibited!'
        }
    }
}

export const useRulesViewStore = defineStore('rulesView', {
    state: () => (rulesView)
})

const goalsView: GoalsView = {
    allgoals: {
        mobGoal: {
            id: 'mobGoal',
            description: 'TODO MobGoal',
            label: 'MobGoal',
        },
        itemGoal: {
            id: 'itemGoal',
            description: 'TODO ItemGoal',
            label: 'ItemGoal'
        }
    }
}

export const useGoalsViewStore = defineStore('goalsView', {
    state: () => (goalsView)
})

const punishmentsView: PunishmentsView = {
    allpunishments: {
        endPunishment: {
            id: 'endPunishment',
            label: 'End',
            description: 'TODO'
        },
        healthPunishment: {
            id: 'healthPunishment',
            label: 'Health',
            description: 'TODO'
        }
    }
}

export const usePunishmentsViewStore = defineStore('punishmentsView', {
    state: () => (punishmentsView)
})

const normalModel: Model = {
    rules: {
        enabledRules: {
            noBlockBreak: {
                exemptions: [],
                punishments: {
                    endPunishment: {
                        affects: 'All'
                    }
                }
            }
        },
        enabledGlobalPunishments: {}
    },
    goals: {}
}

export const useConfigStore = defineStore('config', () => {
    const model = ref(normalModel)
    return { model }
})

app.mount('#app')

//  this.$primevue.changeTheme('md-dark-indigo', 'md-light-indigo', 'lara-dark-purple-link', () => {});