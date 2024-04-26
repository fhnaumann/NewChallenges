
<template>
    <DefaultPunishment v-bind="props" v-slot="slotProps" @clear-on-disabled="clearOnDisabled">
        <div v-if="slotProps.punishable.active.value" class="">
            <div class="flex items-center space-x-4">
                <p v-tooltip="{ value: t(`punishments.types.healthPunishment.settings.hearts_lost.tooltip`), showDelay: 500, hideDelay: 250}">{{ t("punishments.types.healthPunishment.settings.hearts_lost.name") }}</p>
                <InputNumber input-style="width:32px" :model-value="heartsLost" @update:model-value="(newHeartsLost: number) => updateIfActive(slotProps.punishable, newHeartsLost)" show-buttons mode="decimal" :min="minHeartsLost" :max="maxHeartsLost" :disabled="randomizeHeartsLost"/>
                <Checkbox v-model="randomizeHeartsLost" :binary="true" input-id="randomizeHeartsLost"/>
                <label  for="randomizeHeartsLost" class="ml-2" v-tooltip="{ value: t(`punishments.types.healthPunishment.settings.randomized.tooltip`), showDelay: 500, hideDelay: 250}">{{ t(`punishments.types.healthPunishment.settings.randomized.name`) }}</label>
            </div>
            
        </div>
    </DefaultPunishment>

</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import DefaultPunishment, { type GlobalPunishmentProps, type LocalPunishmentProps, type PunishmentProps } from './DefaultPunishment.vue';
import { usePunishable, usePunishableCommons } from './Punishable';
import type { Affects, BasePunishmentConfig, HealthPunishmentConfig } from '../model/punishments';
import type { RuleName } from '../model/rules';
import type { PunishmentView } from '../view/punishments';
import { useConfigStore, useJSONSchemaConfigStore } from '@/main';
import InputNumber from 'primevue/inputnumber';
import Checkbox from 'primevue/checkbox';
import { useI18n } from 'vue-i18n';
const props = defineProps<PunishmentProps>()

const config = useConfigStore()
const jsonSchema = useJSONSchemaConfigStore()

const { t } = useI18n()

const { getPunishmentBasePath } = usePunishableCommons()

function updateIfActive(punishable: any, newHeartsLost: number) {
    if(punishable.active.value) {
        heartsLost.value = newHeartsLost
    }
}

const minHeartsLost = jsonSchema.HealthPunishmentConfig.properties.heartsLost.minimum
const maxHeartsLost = jsonSchema.HealthPunishmentConfig.properties.heartsLost.maximum
const defaultHeartsLost = jsonSchema.HealthPunishmentConfig.properties.heartsLost.default
const defaultRandomizeHeartsLost = jsonSchema.HealthPunishmentConfig.properties.randomizeHeartsLost.default

const heartsLost = ref(defaultHeartsLost)
watch(heartsLost, (newHeartsLost) => {
    getPunishmentBasePath(config.model, props).healthPunishment!.heartsLost = newHeartsLost
})
const randomizeHeartsLost = ref(defaultRandomizeHeartsLost)
watch(randomizeHeartsLost, (isRandomizeHeartsLost) => {
        heartsLost.value = defaultHeartsLost // reset hearts lost, since it will be disabled now
        //randomizeHeartsLost.value = isRandomizeHeartsLost
        getPunishmentBasePath(config.model, props).healthPunishment!.randomizeHeartsLost = isRandomizeHeartsLost
})

function clearOnDisabled() {
    heartsLost.value = defaultHeartsLost
    randomizeHeartsLost.value = defaultRandomizeHeartsLost
}

</script>