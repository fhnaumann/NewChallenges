<template>
    <DefaultGoal :goal="goalsView.allgoals.itemGoal">
        <div class="flex flex-col space-y-5">
            <p class="text-xl">Configuration</p>
            <div>
                <CollectableGoalEntry v-for="blockMat in collectable.selectedData.value" :key="blockMat.collectableName"
                    :possible-data="collectable.copyExclude(allBlockMaterialData, collectable.selectedData.value)"
                    :selected-data="collectable.collectableEntryConfig2DataRow(allBlockMaterialData, blockMat)"
                    :collectable-prefix="'Item:'"
                    :amount-prefix="'Amount:'"
                    @update-selected-data="collectable.updateSelectedData(blockMat.collectableName, $event.code)"
                    @update-value-for-amount="collectable.updateSelectedDataSpecificAmount(blockMat.collectableName, $event)"
                    @delete-entry="collectable.deleteDataRow"
                    :disabled="collectable.selectAllData.value || allBlocks || allItems"
                />
                <CollectableGoalEntryPlaceholder 
                    :possible-data="collectable.copyExclude(allBlockMaterialData, collectable.selectedData.value)"
                    :collectable-prefix="'Item:'"
                    :amount-prefix="'Amount:'"
                    :place-holder-text="'Select items'"
                    @transfer-data-from-place-holder-to-new-instance="collectable.updateSelectedData(undefined, $event.code)"
                    :disabled="collectable.selectAllData.value || allBlocks || allItems"
                />
            </div>
            <div class="flex flex-col space-y-4">
                <div>
                    <Checkbox v-model="collectable.selectAllData.value" @input="allItems = false; allBlocks = false" input-id="2" binary/>
                    <label for="2" class="ml-2">Collect everything</label>
                </div>
                <div>
                    <Checkbox v-model="allItems" input-id="0" binary :disabled="collectable.selectAllData.value"/>
                    <label for="0" class="ml-2">Collect all items</label>
                </div>
                <div>
                    <Checkbox v-model="allBlocks" input-id="1" binary :disabled="collectable.selectAllData.value"/>
                    <label for="1" class="ml-2">Collect all blocks</label>
                </div>
                <div>
                    <Checkbox v-model="fixedOrder" input-id="fixedOrderBlockMats" binary/>
                    <label for="fixedOrderBlockMats" class="ml-2">Fixed random order</label>
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
import { useCollectableGoal, type AccessOperation } from '../collectableGoal';
import type { CollectableEntryConfig, GoalName } from '../model/goals';
import type { DataRow } from '../loadableDataRow';
import { useValidator } from '../validator';
import { ref, watch } from 'vue';
import { allMaterialData, allBlockMaterialData, allItemMaterialData } from '../loadableDataRow';

const model = useConfigStore().model
const JSONSchemaConfig = useJSONSchemaConfigStore()
const goalsView = useGoalsViewStore()
const validator = useValidator()

// init empty
model.goals!.itemGoal = {}

const access: AccessOperation = {
    getSelectedData: (model) => model.goals?.itemGoal?.items!,
    setSelectedData: (model, newSelectedBlockMats) => model.goals!.itemGoal!.items! = newSelectedBlockMats,
}

const collectable = useCollectableGoal(
    access,
    // FIX --- only include block codes if they are also an item. A code that is just a block and not an item is not obtainable and therefore
    // cannot be part of an ItemGoal.
    allMaterialData.filter(dataRow => dataRow.is_item || (dataRow.is_block && dataRow.is_item)),
    JSONSchemaConfig.ItemGoalConfig.properties.items.default,
    false
)

const allItems = ref<boolean>(false)
watch(allItems, newAllItems => {
    if(newAllItems) {
        if(allBlocks.value) {
            allItems.value = false
            allBlocks.value = false
            collectable.selectAllData.value = true
        }
        else {
            // FIX --- In this context "allItem" refers to codes that are only items but not blocks
            collectable.overrideBulkSelectedData(allItemMaterialData.filter(dataRow => !dataRow.is_block).map(dataRow => dataRow.code))
        }
    }
    else {
        collectable.overrideBulkSelectedData(collectable.selectedData.value.map(entry => entry.collectableName))
    }
})

const allBlocks = ref<boolean>(false)
watch(allBlocks, newAllBlocks => {
    if(newAllBlocks) {
        if(allItems.value) {
            allBlocks.value = false
            allItems.value = false
            collectable.selectAllData.value = true
        }
        else {
            // FIX --- if something is a block but not an item, then it should be ignored completely as it is not obtainable
            collectable.overrideBulkSelectedData(allBlockMaterialData.filter(dataRow => dataRow.is_item).map(dataRow => dataRow.code))
        }
    }
    else {
        collectable.overrideBulkSelectedData(collectable.selectedData.value.map(entry => entry.collectableName))
    }
})

const fixedOrder = ref<boolean>(false)
watch(fixedOrder, newFixedOrder => {
    model.goals!.itemGoal!.fixedOrder! = newFixedOrder
})
fixedOrder.value = JSONSchemaConfig.ItemGoalConfig.properties.fixedOrder.default


</script>