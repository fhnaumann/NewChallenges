<template>
    <div class="flex flex-col space-y-5">
        <p class="text-xl">Configuration:</p>
        <div>
            
            <MobGoalEntry v-for="entry in mobGoalEntries" :key="entry" :selectedMob="entry" :possible-mobs="allMobs.filter(mob => entry != mob ? !mobGoalEntries.includes(mob) : true)" @update:selectedMob="(newSelectedMob) => updateMobGoalEntries(newSelectedMob, entry)" @deleteEntry="deleteEntry"/>
            <MobGoalEntryPlaceholder :possible-mobs="allMobs.filter(mob => !mobGoalEntries.includes(mob))" @transferDataFromPlaceHolderToNewInstance="transferDataFromPlaceHolderToNewInstance"/>
        </div>
    </div>
    
</template>

<script setup lang="ts">
import { ref, defineComponent, toRef } from 'vue'

import MultiSelect from 'primevue/multiselect';
import MobGoalEntry from './MobGoalEntry.vue'

import Sidebar from 'primevue/sidebar';
import mobList from "raw-loader!../../assets/entities_alive_list.csv"
import MobGoalEntryPlaceholder from './MobGoalEntryPlaceholder.vue';
import { useConfigStore } from '@/main';

const store = useConfigStore().model

const allMobs = loadImages()
const mobGoalEntries = ref(allMobs.filter((mob) => mob.code == "ENDER_DRAGON"))

defineProps({
    goalName: String,
    data: Object,
})

function loadImages() {
    return mobList.split("\r\n").map((entry) => {
        const splitted = entry.split(",")
        return {
            code: splitted[0],
            image: splitted[1],
            label: splitted[2]
        }
    })
} 

function updateMobGoalEntries(newSelectedMob, entry) {
    if(newSelectedMob == entry) {
        // The user has clicked the same item that was already selected -> do nothing
        return
    }
    // From now on the scenario is:
    // The user has changed their selection inside the SAME MobGoalEntry component to a different mob 
    console.log(newSelectedMob)
    console.log(entry)
    delete store.goals.MobGoal.settings.mobs[entry.code] // delete config part, since it's a different variable now
    const previousMobIndex = mobGoalEntries.value.indexOf(entry)
    mobGoalEntries.value[previousMobIndex] = newSelectedMob // replace the old selected mob with the new one in the list


    
}

function deleteEntry(mobToDelete) {
    console.log("to delete: " + mobToDelete)
    mobGoalEntries.value.splice(mobGoalEntries.value.indexOf(mobToDelete), 1)
    console.log(mobGoalEntries.value)
    delete store.goals.MobGoal.settings.mobs[mobToDelete.code]
}

function transferDataFromPlaceHolderToNewInstance(selectedMob) {
    mobGoalEntries.value.push(selectedMob)
}

defineComponent({
    MultiSelect,
    MobGoalEntry

})

</script>