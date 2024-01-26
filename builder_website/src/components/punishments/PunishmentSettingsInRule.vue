<template>
    <div>
        <Toast />
        <Button class="cursor-pointer bg-gray-700 rounded-lg w-52 h-10" label="Modify punishments" :badge="activePunishmentAmount" outlined @click="showPunishments" />
    </div>
</template>

<script setup lang="ts">
import Button from 'primevue/button';
import { defineAsyncComponent, getCurrentInstance, computed, watch, ref } from 'vue';
import PunishmentList from './PunishmentList.vue';
import { useConfigStore } from '@/main';
import { useDialog } from 'primevue/usedialog';
import Toast from 'primevue/toast';
import { useToast } from "primevue/usetoast"
import type { RuleName } from '../model/rules';
import { storeToRefs } from 'pinia'

const dialog = useDialog();
const toast = useToast()
const store = useConfigStore().model

const props = defineProps({
    currentRule: String
})

const activePunishmentAmount = ref(getActivePunishmentAmount())

watch(store.rules.enabledRules, () => {
    activePunishmentAmount.value = getActivePunishmentAmount()
}, {deep: true})
function getActivePunishmentAmount() {
    console.log("rerendering active punishment amount")
    console.log(store.rules.enabledRules)
    console.log(props.currentRule)
    if(!Object.hasOwn(store.rules.enabledRules[props.currentRule as RuleName], 'punishments')) {
        return '0'
    }
    console.log("returning ", Object.keys(store.rules.enabledRules[props.currentRule as RuleName]!.punishments!).length)
    return Object.keys(store.rules.enabledRules[props.currentRule as RuleName]!.punishments!).length + ""
}

function showPunishments() {
    if(hasAtLeastOneGlobalPunishmentEnabled()) {
        showError()
        return
    }

    console.log("test")
    dialog.open(PunishmentList, {
        props: {
            modal: true,
        },
        data: {
            currentRule: props.currentRule
        }
    })
}

const showError = () => {
    toast.add({ 
            severity: 'error', 
            summary: 'Cannot modify punishments locally!', 
            detail: 'You have enabled at least one global punishments. Turn off global punishments to modify punishments locally.',
        life: 5000})
}

function hasAtLeastOneGlobalPunishmentEnabled() {
    return Object.keys(store.rules.enabledGlobalPunishments).length > 0
}

</script>