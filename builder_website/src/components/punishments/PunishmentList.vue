<template>
    <div class="flex flex-col overflow-y-auto w-[40rem] h-96 max-h-96" v-if="isGlobal !== undefined">
        <div class="shrink-0 flex justify-center items-center">
            <h1 v-if="isGlobal === true" class="text-3xl">Global Punishments</h1>
            <h1 v-else class="text-3xl">Local Punishments for {{ rulesView.allrules[currentRule as RuleName].label }}</h1>
        </div>
        <div class="shrink-0 flex flex-col space-y-4 mx-2">
            <div v-for="punishment in allPunishmentsView" :key="punishment.id" >
                <HealthPunishment v-if="punishment.id === 'healthPunishment'" :punishment-view="punishment" :global="isGlobal" :rule-name="currentRule" />
                <RandomEffectPunishment v-else-if="punishment.id === 'randomEffectPunishment'" :punishment-view="punishment" :global="isGlobal" :rule-name="currentRule"/>
                <RandomItemPunishment v-else-if="punishment.id === 'randomItemPunishment'" :punishment-view="punishment" :global="isGlobal" :rule-name="currentRule"/>
                <DefaultPunishment v-else :punishment-view="punishment" :global="isGlobal" :rule-name="currentRule" :assign-when-created="{affects: 'All'}" />
                
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import PunishmentEntry from './PunishmentEntry.vue';
import { inject, defineComponent, computed, reactive, nextTick, onMounted, ref } from 'vue'
import { useConfigStore, usePunishmentsViewStore, useRulesViewStore } from '@/main';
import InputNumber from 'primevue/inputnumber';
import type { PunishmentName } from '../model/punishments';
import type { PunishmentView } from '../view/punishments';
import DefaultPunishment from './DefaultPunishment.vue';
import type { BaseRuleConfig, PunishableRuleConfig, RuleName } from '../model/rules';
import HealthPunishment from './HealthPunishment.vue';
import RandomEffectPunishment from './RandomEffectPunishment.vue';
import RandomItemPunishment from './RandomItemPunishment.vue';

const config = useConfigStore().model
const rulesView = useRulesViewStore()



const dialogRef = inject('dialogRef')

const currentRule = ref<RuleName | undefined | null>(undefined)

const punishmentsViewStore = usePunishmentsViewStore()

const isGlobal = computed<boolean | undefined>(() => {
    console.log("current rule is", currentRule.value)
    if(currentRule.value === undefined) {
        // not yet initialized (onMounted hasn't run yet)
        return undefined
    }
    return currentRule.value === null
})

onMounted(() => {
    console.log("onMounted in punishmentList")
    //currentRule.value = dialogRef.value.data.currentRule
    console.log(dialogRef)
    currentRule.value = dialogRef.value.data.currentRule
    if(!isGlobal.value) {
            createEmptyPunishmentObjectIfNotAlreadyExist()
    }
    
})


function createEmptyPunishmentObjectIfNotAlreadyExist() {
    if(!Object.hasOwn(getRuleConfig(), 'punishments')) {
        config.rules.enabledRules[currentRule.value!]!.punishments = {}
    }
    
}

/**
 * Only use if currentRule.value !== undefined (e.g. isGlobal() should be false)
 */
function getRuleConfig(): PunishableRuleConfig {
    return config.rules.enabledRules[currentRule.value!]!
}



const allPunishments: PunishmentName[] = [
    'endPunishment', 'healthPunishment', 'deathPunishment', 'randomEffectPunishment', 'randomItemPunishment'
]
const allPunishmentsView: PunishmentView[] = allPunishments.map(punishmentName => punishmentsViewStore.allpunishments[punishmentName as PunishmentName])

</script>