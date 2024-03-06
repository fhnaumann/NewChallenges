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
import { createRouter, createWebHashHistory, createWebHistory, type RouteRecordRaw } from 'vue-router';
import ResourcePack from "./components/ResourcePack.vue"

const pinia = createPinia()
const app = createApp(App)

app.use(pinia)
app.use(PrimeVue)
app.use(DialogService)
app.use(ConfirmationService)
app.use(ToastService)

app.directive('tooltip', Tooltip);

export const toast = app.config.globalProperties.$toast

const routes: RouteRecordRaw[] = [
    {path: "/", component: App},
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
    history: createWebHashHistory(),
    routes: routes
})

app.use(router)

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
        },
        noItem: {
            id: 'noItem',
            label: "NoItem",
            description: 'Picking up items is prohibited!'
        },
        noMobKill: {
            id: 'noMobKill',
            label: "NoMobKill",
            description: 'Killing mobs is prohibited!'
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
        },
        blockbreakGoal: {
            id: 'blockbreakGoal',
            description: 'TODO blockbreak goal',
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