<template>
    <DefaultGoal :goal="goalsView.allgoals.itemGoal">
        <div class="flex flex-col space-y-5">
            <p class="text-xl">Configuration</p>
            <div>
                <CollectableGoalEntry v-for="[key, value] in Object.entries(selectedItems)" :key="key"
                    :label="allItems.find((item: DataRow) => item.code === key)!.label" :selectedData="key"
                    :img="allItems.find((item: DataRow) => item.code === key)!.image"
                    :possible-data="allItems.filter((item: DataRow) => item.isItem && key !== item.code ? !Object.keys(selectedItems).includes(item.code) : true)"
                    :collectable-prefix="'Item:'"
                    :amount-prefix="'Amount:'"
                    @update:selectedData="(newSelectedItem: DataRow) => updateSelectedData(key, newSelectedItem.code)"
                    @update:value-for-amount="(newAmount) => updateSelectedDataAmount(key, newAmount)"
                    @deleteEntry="collectableGoal.deleteDataRow" />
                <CollectableGoalEntryPlaceholder
                    :possible-mobs="allItems.filter((item: DataRow) => !Object.keys(selectedItems).includes(item.code))"
                    :collectable-prefix="'Item:'"
                    :amount-prefix="'Amount:'"
                    :place-holder-text="'Select Item'"
                    @transferDataFromPlaceHolderToNewInstance="(newSelectedData: DataRow) => updateSelectedData(undefined, newSelectedData.code)"
                    />
                </div>
            <div class="flex flex-col space-y-4">
                <div>
                    <Checkbox v-model="collectAllItems" input-id="0" binary/>
                    <label for="0" class="ml-2">Collect all items</label>
                </div>
                <div>
                    <Checkbox v-model="collectAllBlocks" input-id="1" binary :disabled="collectAllItems"/>
                    <label for="1" class="ml-2">Collect all blocks</label>
                </div>
            </div>
        </div>
    </DefaultGoal>
</template>

<script setup lang="ts">
import itemList from '../../assets/items.csv?raw'
import Checkbox from 'primevue/checkbox';
import { useConfigStore, useGoalsViewStore, useJSONSchemaConfigStore } from '@/main';
import DefaultGoal from './DefaultGoal.vue';
import CollectableGoalEntry from './CollectableGoalEntry.vue'
import CollectableGoalEntryPlaceholder from './CollectableGoalEntryPlaceholder.vue';
import { useCollectableGoal } from '../collectableGoal';
import type { CollectableEntryConfig, GoalName } from '../model/goals';
import type { DataRow } from '../loadableDataRow';
import { useValidator } from '../validator';
import { ref, watch } from 'vue';

const model = useConfigStore().model
const JSONSchemaConfig = useJSONSchemaConfigStore()
const goalsView = useGoalsViewStore()
const validator = useValidator()

const collectableGoal = useCollectableGoal("itemGoal" as GoalName, "items", itemList)
const allItems: DataRow[] = collectableGoal.fullData
const selectedItems: CollectableEntryConfig = collectableGoal.selectedCollectableData

const defaultAllItems: boolean = JSONSchemaConfig.ItemGoalConfig.properties.allItems.default
const defaultAllBlocks: boolean = JSONSchemaConfig.ItemGoalConfig.properties.allBlocks.default

const collectAllItems = ref<boolean>(false)
watch(collectAllItems, (newCollectAllItems) => {
    if(newCollectAllItems) {
        collectAllBlocks.value = false
    }
    model.goals!.itemGoal!.allItems = newCollectAllItems
})
const collectAllBlocks = ref<boolean>(defaultAllBlocks)
watch(collectAllBlocks, (newCollectAllBlocks) => {
    model.goals!.itemGoal!.allBlocks = newCollectAllBlocks
})

collectAllItems.value = defaultAllItems;

function updateSelectedData(currentlySelectedData: string | undefined, newSelectedData: string) {
    if(currentlySelectedData === newSelectedData) {
        return
    }
    const { valid, messages } = validator.isValid(model, (copy) => {
        copy.goals!.itemGoal!.items[newSelectedData] = {
            amountNeeded: 1
        }
    })
    if(valid) {
        delete selectedItems.value[currentlySelectedData!]
        selectedItems.value[newSelectedData] = {
            amount: 1
        }
    }
}

function updateSelectedDataAmount(currentlySelectedData: string, newAmount: number) {
    model.goals.itemGoal!.items[currentlySelectedData].currentAmount = newAmount
}
</script>