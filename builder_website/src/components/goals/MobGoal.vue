<template>
    <DefaultGoal :goal="goalsView.allgoals.mobGoal">
        <div class="flex flex-col space-y-5">
            <p class="text-xl">Configuration</p>
            <div>
                <CollectableGoalEntry v-for="entityType in collectable.selectedData.value" :key="entityType.collectableName"
                    :possible-data="collectable.copyExclude(allEntityTypeData, collectable.selectedData.value)"
                    :selected-data="collectable.collectableEntryConfig2DataRow(allEntityTypeData, entityType)"
                    :collectable-prefix="'Mob:'"
                    :amount-prefix="'Amount:'"
                    @update-selected-data="collectable.updateSelectedData(entityType.collectableName, $event.code)"
                    @update-value-for-amount="collectable.updateSelectedDataSpecificAmount(entityType.collectableName, $event)"
                    @delete-entry="collectable.deleteDataRow"
                    :disabled="collectable.selectAllData.value"
                />
                <CollectableGoalEntryPlaceholder 
                    :possible-data="collectable.copyExclude(allEntityTypeData, collectable.selectedData.value)"
                    :collectable-prefix="'Mob:'"
                    :amount-prefix="'Amount:'"
                    :place-holder-text="'Select mobs'"
                    @transfer-data-from-place-holder-to-new-instance="collectable.updateSelectedData(undefined, $event.code)"
                    :disabled="collectable.selectAllData.value"
                />
            </div>
            <div class="flex flex-col space-y-4">
                <div>
                    <Checkbox v-model="collectable.selectAllData.value" input-id="0" binary/>
                    <label for="0" class="ml-2">Collect all mobs</label>
                </div>
                <div>
                    <Checkbox v-model="fixedOrder" input-id="fixedOrderEntityTypes" binary/>
                    <label for="fixedOrderEntityTypes" class="ml-2">Fixed random order</label>
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
import { allEntityTypeData } from '../loadableDataRow';

const model = useConfigStore().model
const JSONSchemaConfig = useJSONSchemaConfigStore()
const goalsView = useGoalsViewStore()
const validator = useValidator()

// init empty
//model.goals!.mobGoal = {}

const access: AccessOperation = {
    getSelectedData: (model) => model.goals?.mobGoal?.mobs!,
    setSelectedData: (model, newSelectedBlockMats) => model.goals!.mobGoal!.mobs! = newSelectedBlockMats,
}

const collectable = useCollectableGoal(
    access,
    allEntityTypeData,
    access.getSelectedData(model) !== undefined ? access.getSelectedData(model) : [],
    false
)

const fixedOrder = ref<boolean>(model.goals!.mobGoal!.fixedOrder!)
watch(fixedOrder, newFixedOrder => {
    model.goals!.mobGoal!.fixedOrder! = newFixedOrder
})
//fixedOrder.value = JSONSchemaConfig.MobGoalConfig.properties.fixedOrder.default


</script>