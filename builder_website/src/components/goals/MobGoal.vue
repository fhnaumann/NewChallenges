<template>
    <DefaultGoal :goal="goalsView.allgoals.mobGoal" v-slot="slotProps">
        <div class="flex flex-col space-y-5">
            <p class="text-xl">Configuration:</p>
            <div>
                <CollectableGoalEntry v-for="[key, value] in Object.entries(selectedMobs)" :key="key"
                    :label="allMobs.find((mob: DataRow) => mob.code === key)!.label" :selectedData="key"
                    :possible-data="allMobs.filter((mob: DataRow) => key !== mob.code ? !Object.keys(selectedMobs).includes(mob.code) : true)"
                    :collectable-prefix="'Mob:'"
                    :amount-prefix="'Kills:'"
                    @update:selectedData="(newSelectedMob: DataRow) => updateSelectedData(key, newSelectedMob.code)"
                    @update:value-for-amount="(newAmount) => updateSelectedDataAmount(key, newAmount)"
                    @deleteEntry="collectableGoal.deleteDataRow" />
                <CollectableGoalEntryPlaceholder
                    :possible-mobs="allMobs.filter((mob: DataRow) => !Object.keys(selectedMobs).includes(mob.code))"
                    :collectable-prefix="'Mob:'"
                    :amount-prefix="'Kills:'"
                    :place-holder-text="'Select Mob'"
                    @transferDataFromPlaceHolderToNewInstance="(newSelectedData: DataRow) => updateSelectedData(undefined, newSelectedData.code)" />
            </div>
        </div>
    </DefaultGoal>
</template>

<script setup lang="ts">
import { ref, defineComponent, toRef } from 'vue'

import MultiSelect from 'primevue/multiselect';
import CollectableGoalEntry from './CollectableGoalEntry.vue'

import Sidebar from 'primevue/sidebar';
//import mobList from "raw-loader!../../assets/entities_alive_list.csv"
import mobList from '../../assets/entities_alive_list.csv?raw'
import CollectableGoalEntryPlaceholder from './CollectableGoalEntryPlaceholder.vue';
import { useConfigStore, useDefaultConfigStore, useGoalsViewStore, useJSONSchemaConfigStore } from '@/main';
import { useCollectableGoal } from '../collectableGoal';
import type { DataRow } from '../loadableDataRow';
import type { CollectableDataConfig, CollectableEntryConfig, GoalName } from '../model/goals';
import { toRaw } from 'vue';
import DefaultGoal from './DefaultGoal.vue';
import type { GoalView } from '../view/goals';
import { useValidator } from '../validator';
import type { Model } from '../model/model';

const store = useConfigStore().model
const defaultConfig = useDefaultConfigStore()
const JSONSchemaConfig = useJSONSchemaConfigStore()
const goalsView = useGoalsViewStore()
const validator = useValidator()

// IMPORTANT TO CLEAR THE CONFIG
// For some reason deleting the entire goal does not delete "something", which
// prevents the config from being wiped, therefore manually clearing it here
store.goals!.mobGoal = structuredClone(toRaw(defaultConfig.goals!.mobGoal))


const defaultSelectedMobs: CollectableEntryConfig = JSONSchemaConfig.MobGoalConfig.properties.mobs.default

const collectableGoal = useCollectableGoal("mobGoal" as GoalName, "mobs", mobList)

const allMobs: DataRow[] = collectableGoal.fullData
const selectedMobs: CollectableEntryConfig = collectableGoal.selectedCollectableData
selectedMobs.value = defaultSelectedMobs
// selectedMobs.value["ENDER_DRAGON"] = {amount: 1}
// collectableGoal.addDataRow(undefined, allMobs.find((mob: DataRow) => mob.code === "ENDER_DRAGON"))

function updateSelectedData(currentlySelectedData: string | undefined, newSelectedData: string) {
    console.log("updating selected data")
    if (currentlySelectedData === newSelectedData) {
        // The user has clicked the same item that was already selected -> do nothing
        return
    }



    // the number selected for each mob is unknown, but should not really matter for validation
    //collectableGoal.addDataRow(entry, newSelectedData)
    const { valid, messages } = validator.isValid(store, (copy) => {
        copy.goals!.mobGoal!.mobs[newSelectedData] = {
            amountNeeded: 1 // use default value and not the actual value, because I don't have easy access to the actual number (for now)
        }
    })
    console.log("is valid?", valid)
    if (valid) {
        console.log("assigning new selected mob", newSelectedData)
        delete selectedMobs.value[currentlySelectedData! as keyof CollectableDataConfig]
        const temp_default_value = {
            amountNeeded: 1 // use default value and not the actual value, because I don't have easy access to the actual number (for now)
        } as CollectableDataConfig
        selectedMobs.value[newSelectedData as keyof CollectableDataConfig] = temp_default_value
    }
}

function updateSelectedDataAmount(currentlySelectedData: string, newAmount: number) {
    store.goals!.mobGoal!.mobs[currentlySelectedData].amountNeeded = newAmount
}

defineComponent({
    MultiSelect,
    CollectableGoalEntry

})

</script>