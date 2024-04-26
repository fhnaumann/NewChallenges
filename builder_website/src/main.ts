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
import type { RulesView } from './components/view/rules'
import modelSchema from './assets/challenges_schema.json'
import type { PunishmentsView } from './components/view/punishments'
import type { GoalsView } from './components/view/goals'
import ConfirmationService from 'primevue/confirmationservice';
import { createMemoryHistory, createRouter, createWebHashHistory, createWebHistory, type RouteRecordRaw } from 'vue-router';
import ResourcePack from "./components/ResourcePack.vue"
import { createI18n } from 'vue-i18n'
import en_punishments from './locales/en/punishments.json'
import en_rules from './locales/en/rules.json'
import Docs from './components/Docs.vue'
import HomeView from './components/HomeView.vue'
import ChallengeBuilder from './components/ChallengeBuilder.vue'

const pinia = createPinia()
const app = createApp(App)

app.use(pinia)
app.use(PrimeVue)
app.use(DialogService)
app.use(ConfirmationService)
app.use(ToastService)

app.directive('tooltip', Tooltip);

export const toast = app.config.globalProperties.$toast

const i18n = createI18n({
    legacy: false,
    locale: 'en',
    messages: {
        en: Object.assign({}, en_punishments, en_rules),
    }
})

app.use(i18n)


const routes: RouteRecordRaw[] = [
    {path: "/", component: ChallengeBuilder},
    {
        path: "/resourcepack",
        component: ResourcePack,
        beforeEnter: (_to, _from, next) => {
            // Create a link element
            const link = document.createElement('a');
            // Set the file URL
            link.href = '/testpack.zip'; // Replace with your file URL
            // Set the download attribute with the desired file name
            link.setAttribute('download', 'testpack.zip');
            // Append the link to the document body
            document.body.appendChild(link);
            // Trigger the click event to start the download
            link.click();
            // Remove the link from the document body after the download starts
            document.body.removeChild(link);

            //next(false)
        }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes: routes
})

app.use(router)

export const BASE_IMG_URL = process.env.NODE_ENV === 'production' ? 'https://challenges-builder.s3.eu-central-1.amazonaws.com' : ''

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
            description_key: 'Breaking blocks is prohibited!',
        },
        noItem: {
            id: 'noItem',
            label: "NoItem",
            description_key: 'Picking up items is prohibited!'
        },
        noMobKill: {
            id: 'noMobKill',
            label: "NoMobKill",
            description_key: 'Killing mobs is prohibited!'
        },
        noDeath: {
            id: 'noDeath',
            label: "NoDeath",
            description_key: 'Dying is prohibited!'
        },
        /*
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
        },
        */
    }
}

export const useRulesViewStore = defineStore('rulesView', {
    state: () => (rulesView)
})

const goalsView: GoalsView = {
    allgoals: {
        mobGoal: {
            id: 'mobGoal',
            description: 'Kill mobs to complete the goal!',
            label: 'MobGoal',
        },
        itemGoal: {
            id: 'itemGoal',
            description: 'Collect items to complete the goal!',
            label: 'ItemGoal'
        },
        blockbreakGoal: {
            id: 'blockbreakGoal',
            description: 'Break blocks to complete the goal!',
            label: 'BlockBreakGoal'
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
        },
        deathPunishment: {
            id: 'deathPunishment',
            label: 'Death',
            description: 'TODO'
        },
        randomEffectPunishment: {
            id: 'randomEffectPunishment',
            label: 'Random Effect',
            description: 'TODO'
        },
        randomItemPunishment: {
            id: 'randomItemPunishment',
            label: 'Random Item',
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