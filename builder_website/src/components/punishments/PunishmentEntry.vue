<template>
    <div class="flex" v-tooltip="{ value: messages[0] }">
        <div class="flex items-center space-x-4">
            <Checkbox v-model="newActive" :disabled="!valid" :binary="true"
                @input="$emit('updateSelectedPunishments', punishmentName, newActive); newAffects = 'All'"
                :inputId="punishmentName" :name="punishmentName" :value="punishmentName" />
            <label :for="punishmentName" class="ml-2 w-32"> {{ punishmentName }} </label>

        </div>
        <div class="flex items-center">
            <p>Affected:</p>
            <Dropdown v-model="newAffects" :options="['All', 'Causer']" class="scale-75 w-32 ml-auto" :disabled="!newActive"
                @update:modelValue="updateAffects" />
        </div>
    </div>
</template>

<script setup lang="ts">
import Dropdown from 'primevue/dropdown';
import Checkbox from 'primevue/checkbox';
import { defineComponent, defineProps, ref, toRef, onMounted } from 'vue'
import { useConfigStore } from '@/main';
import { isValid } from '@/validator.js'
import { useValidatable } from './validatable';
const props = defineProps({
    currentRule: String,
    punishmentName: String
})


const store = useConfigStore().model

console.log(props)
const punishmentName = toRef(props.punishmentName)
const currentRule = toRef(props.currentRule)
const newActive = ref(punishmentAlreadyActive() ? true : false)
const newAffects = ref(getInitialAffect())

const validatable = useValidatable()

const { valid, messages } = validatable.isValid((storeCopy) => {
    console.log(props.currentRule)
    if(!props.currentRule) {
        storeCopy.globalPunishments[props.punishmentName] = {}
    }
    else {
        storeCopy.rules[props.currentRule].punishments[props.punishmentName] = {}
    }
    
})

function updateAffects(affects) {
    if (localPunishmentAlreadyActive()) {
        store.rules[props.currentRule].punishments[punishmentName.value].affects = affects
    }
    if (globalPunishmentAlreadyActive()) {
        store.globalPunishments[punishmentName.value].affects = affects
    }
}

function punishmentAlreadyActive() {
    return globalPunishmentAlreadyActive() || localPunishmentAlreadyActive()
}

function globalPunishmentAlreadyActive() {
    return punishmentName.value in store.globalPunishments
}

function localPunishmentAlreadyActive() {
    return props.currentRule in store.rules && punishmentName.value in store.rules[props.currentRule].punishments
}

function getInitialAffect() {
    if (globalPunishmentAlreadyActive()) {
        return store.globalPunishments[punishmentName.value].affects
    }
    if (localPunishmentAlreadyActive()) {
        return store.rules[props.currentRule].punishments[punishmentName.value].affects  // retrieve the value that already exists
    }
    return "All"
}

defineComponent({
    Dropdown,
    Checkbox
})
</script>