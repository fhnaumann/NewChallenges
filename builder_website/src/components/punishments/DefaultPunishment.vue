<template>
    <div class="flex flex-col">
        <div class="flex justify-between" v-if="punishable">
            <div class="flex items-center space-x-2">
                <Checkbox :model-value="punishable.active.value" @input="(newActive) => updateIfValid(newActive)" :binary="true"
                    :input-id="props.punishmentView.id" />
                <label :for="props.punishmentView.id" class="ml-2" v-tooltip="{ value: t(`punishments.types.${punishmentView.id}.tooltip`), showDelay: 500, hideDelay: 250}">{{ t(`punishments.types.${punishmentView.id}.name`) }}</label>
            </div>
            <div class="flex items-center space-x-2">
                <p v-tooltip="{ value: t(`punishments.affects.affected.tooltip`), showDelay: 500, hideDelay: 250}">{{ t('punishments.affects.affected.name') }}</p>
                <Dropdown class="w-32 h-10" :pt="{
                    input: {
                        class: 'flex items-center'
                    }
                }" :model-value="punishable.affects.value" @update:model-value="updateAffectsIfValid" :options="punishable.getDefaultAffectsOptions()"
                    :disabled="!punishable.active.value" />
            </div>

        </div>
        <div class="ml-4">
            <slot :punishable="punishable" @clearOnDisabled="clearOnDisabled()"></slot>
        </div>
        
    </div>
</template>

<script setup lang="ts">
import Dropdown from 'primevue/dropdown';

import Checkbox from 'primevue/checkbox';
import { useConfigStore, useDefaultConfigStore, useJSONSchemaConfigStore } from '@/main';
import type { RuleName } from '../model/rules';
import type { Affects, BasePunishmentConfig, EndPunishmentConfig, PunishmentName } from '../model/punishments';
import { watch, ref, onMounted } from 'vue';
import { usePunishable, usePunishableCommons } from './Punishable';
import type { PunishmentView } from '../view/punishments';
import { useValidator } from '../validator';
import type { Model } from '../model/model';
import { storeToRefs } from 'pinia'
import { useI18n } from 'vue-i18n';

export interface GlobalPunishmentProps {
    punishmentView: PunishmentView
    global: true | undefined
}
export interface LocalPunishmentProps extends Omit<GlobalPunishmentProps, 'global'> {
    global: false | undefined
    ruleName: RuleName
}
export type PunishmentProps = GlobalPunishmentProps | LocalPunishmentProps

const props = defineProps<PunishmentProps>()

const emit = defineEmits(['clearOnDisabled'])

const config = useConfigStore().model
const validator = useValidator()

const { t } = useI18n()


function updateAffectsIfValid(newAffects: Affects) {
    punishable.affects.value = newAffects
}

function updateIfValid(newActive: boolean) {
    console.log("updating if valid...")

    const { valid, messages } = validator.isValid(config, (copy) => {
        punishable.updateStore(copy, newActive)
    })
    if (valid) {
        console.log(typeof punishable.active)
        console.log(punishable.active)
        if(!newActive) {
            console.log("resetting punishable affects to all")
            clearOnDisabled()
            
        }
        punishable.active.value = newActive
        //updateStore(store, newSelectedData)
    }
}

function clearOnDisabled() {
    punishable.affects.value = 'All'
    emit("clearOnDisabled")
}

const punishable = usePunishable(props, {affects: 'All'})
</script>