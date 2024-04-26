<template>
    <DefaultPunishment v-bind="props" v-slot="slotProps" @clear-on-disabled="clearOnDisabled">
        <div v-if="slotProps.punishable.active.value">
            <div class="flex items-center space-x-4">
                <p v-tooltip="{ value: t(`punishments.types.randomEffectPunishment.settings.effects_at_once.tooltip`), showDelay: 500, hideDelay: 250}">{{ t("punishments.types.randomEffectPunishment.settings.effects_at_once.name") }}</p>
                <InputNumber input-style="width:32px" v-model="effectsAtOnce" show-buttons mode="decimal" :min="minEffectsAtOnce" :max="maxEffectsAtOnce" :disabled="randomizeEffectsAtOnce"/>
                <Checkbox v-model="randomizeEffectsAtOnce" :binary="true" input-id="randomizeEffectsAtOnce"/>
                <label for="randomizeEffectsAtOnce" class="ml-2" v-tooltip="{ value: t(`punishments.types.randomEffectPunishment.settings.randomized.tooltip`), showDelay: 500, hideDelay: 250}">{{ t("punishments.types.randomEffectPunishment.settings.randomized.name") }}</label>
            </div>
        </div>
    </DefaultPunishment>
</template>

<script setup lang="ts">
import DefaultPunishment, { type PunishmentProps } from './DefaultPunishment.vue';
import InputNumber from 'primevue/inputnumber';
import Checkbox from 'primevue/checkbox';
import { ref, watch } from 'vue';
import { useConfigStore, useJSONSchemaConfigStore } from '@/main';
import { usePunishableCommons } from './Punishable';
import { useI18n } from 'vue-i18n';
const props = defineProps<PunishmentProps>()

const model = useConfigStore().model
const jsonSchema = useJSONSchemaConfigStore()

const { t } = useI18n()

const { getPunishmentBasePath } = usePunishableCommons()

const minEffectsAtOnce = jsonSchema.RandomEffectPunishmentConfig.properties.effectsAtOnce.minimum
const maxEffectsAtOnce = jsonSchema.RandomEffectPunishmentConfig.properties.effectsAtOnce.maximum
const defaultEffectsAtOnce = jsonSchema.RandomEffectPunishmentConfig.properties.effectsAtOnce.default

const effectsAtOnce = ref(defaultEffectsAtOnce)
watch(effectsAtOnce, (newEffectsAtOnce) => {
    getPunishmentBasePath(model, props).randomEffectPunishment!.effectsAtOnce = newEffectsAtOnce
})

const defaultRandomizeEffectsAtOnce = jsonSchema.RandomEffectPunishmentConfig.properties.randomizeEffectsAtOnce.default

const randomizeEffectsAtOnce = ref(defaultRandomizeEffectsAtOnce)
watch(randomizeEffectsAtOnce, (isRandomizeEffectsAtOnce) => {
    effectsAtOnce.value = defaultEffectsAtOnce
    getPunishmentBasePath(model, props).randomEffectPunishment!.randomizeEffectsAtOnce = isRandomizeEffectsAtOnce
})

function clearOnDisabled() {
    effectsAtOnce.value = defaultEffectsAtOnce
    randomizeEffectsAtOnce.value = defaultRandomizeEffectsAtOnce
}


</script>