<template>
  <div>
    <SingleCollectableGoalEntryPlaceholder :collectable-text-prefix="collectableTextPrefix"
                                           :placeholder-text="dropdownPlaceholderText" :show-image="showImage"
                                           :disabled="disabled" :possible-data="removeSelectedDataFromPossibleData()"
                                           :collectable-amount-prefix="collectableAmountPrefix" />
    <SingleCollectableGoalEntry v-for="selected in selectedData" :key="selected.collectableName"
                                :currently-selected="selected"
                                :currently-selected-amount="selected.collectableData?.amountNeeded"
                                :collectable-text-prefix="collectableTextPrefix" :show-image="showImage"
                                :disabled="disabled" :possible-data="removeSelectedDataFromPossibleData()"
                                collectable-amount-prefix="" />
  </div>

</template>

<script setup lang="ts">


import type { DataRow } from '@/models/data_row'
import type { CollectableDataConfig, CollectableEntryConfig } from '@/models/goals'
import SingleCollectableGoalEntryPlaceholder from '@/components/goals/SingleCollectableGoalEntryPlaceholder.vue'
import SingleCollectableGoalEntry from '@/components/goals/SingleCollectableGoalEntry.vue'

const props = defineProps<{
  selectedData: CollectableEntryConfig[],
  possibleData: DataRow[],
  collectableTextPrefix: string,
  collectableAmountPrefix: string,
  dropdownPlaceholderText: string,
  showImage: boolean
  disabled: boolean
}>()

function removeSelectedDataFromPossibleData(): DataRow[] {

}

</script>