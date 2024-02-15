<template>
    <DefaultGoal :goal="goalsView.allgoals.blockbreakGoal">
        <div class="flex flex-col space-y-5">
            <p class="text-xl">Configuration</p>
            <div>
                <CollectableGoalEntry v-for="blockMat in collectable.selectedData.value" :key="blockMat.collectableName"
                    :possible-data="collectable.copyExclude(allBlockMaterialData, collectable.selectedData.value)"
                    :selected-data="collectable.collectableEntryConfig2DataRow(allBlockMaterialData, blockMat)"
                    :collectable-prefix="'Block:'"
                    :amount-prefix="'Amount:'"
                    @update-selected-data="collectable.updateSelectedData(blockMat.collectableName, $event.code)"
                    @update-value-for-amount="collectable.updateSelectedDataSpecificAmount(blockMat.collectableName, $event)"
                    @delete-entry="collectable.deleteDataRow"
                    :disabled="collectable.selectAllData.value"
                />
                <CollectableGoalEntryPlaceholder 
                    :possible-data="collectable.copyExclude(allBlockMaterialData, collectable.selectedData.value)"
                    :collectable-prefix="'Block:'"
                    :amount-prefix="'Amount:'"
                    :place-holder-text="'Select blocks'"
                    @transfer-data-from-place-holder-to-new-instance="collectable.updateSelectedData(undefined, $event.code)"
                    :disabled="collectable.selectAllData.value"
                />
            </div>
            <div class="flex flex-col space-y-4">
                <div>
                    <Checkbox v-model="collectable.selectAllData.value" input-id="selectAllBlockMats" binary/>
                    <label for="selectAllBlockMats" class="ml-2">Break all blocks once</label>
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
import { useConfigStore, useDefaultConfigStore, useJSONSchemaConfigStore, useGoalsViewStore } from '@/main';
import { useValidator } from '../validator';
import DefaultGoal from './DefaultGoal.vue';
import CollectableGoalEntry from './CollectableGoalEntry.vue'
import CollectableGoalEntryPlaceholder from './CollectableGoalEntryPlaceholder.vue';
import type { CollectableEntryConfig, GoalName, GoalsConfig } from '../model/goals';
import { allBlockMaterialData, type DataRow, type MaterialDataRow } from '../loadableDataRow';
import { useCollectableGoal, type AccessOperation } from '../collectableGoal';
import { ref, toRaw, watch } from 'vue';
import Checkbox from 'primevue/checkbox';
import type { BlockBreakGoalConfig } from '../model/blockbreak';

const store = useConfigStore().model
const defaultConfig = useDefaultConfigStore()
const JSONSchemaConfig = useJSONSchemaConfigStore()
const goalsView = useGoalsViewStore()
const validator = useValidator()

// init empty
store.goals!.blockbreakGoal = {}

const access: AccessOperation = {
    getSelectedData: (model) => model.goals?.blockbreakGoal?.broken!,
    setSelectedData: (model, newSelectedBlockMats) => model.goals!.blockbreakGoal!.broken! = newSelectedBlockMats,
    getSelectAllData: (model) => model.goals?.blockbreakGoal?.allBlocks!,
    setSelectAllData: (model, newSelectAllBlockMats) => model.goals!.blockbreakGoal!.allBlocks! = newSelectAllBlockMats
}

const collectable = useCollectableGoal(
    access,
    allBlockMaterialData,
    JSONSchemaConfig.BlockBreakGoalConfig.properties.broken.default,
    JSONSchemaConfig.BlockBreakGoalConfig.properties.allBlocks.default
)

const defaultFixedOrder = JSONSchemaConfig.BlockBreakGoalConfig.properties.fixedOrder.default

const fixedOrder = ref<boolean>(false)
watch(fixedOrder, newFixedOrder => {
    store.goals!.blockbreakGoal!.fixedOrder! = newFixedOrder
})
fixedOrder.value = defaultFixedOrder

</script>