<template>
  <div>
    <SingleCollectableGoalEntryPlaceholder :collectable-text-prefix="collectableTextPrefix"
                                           :placeholder-text="dropdownPlaceholderText" :show-image="showImage"
                                           :disabled="disabled" :possible-data=" !disabled ? removeSelectedDataFromPossibleData() : []"
                                           :collectable-amount-prefix="collectableAmountPrefix"
                                           @transferDataFromPlaceHolderToNewInstance="(selectedData: DataRow) => updateSelectedData(undefined, selectedData)"
    />

    <div v-if="renderSelection">
      <SingleCollectableGoalEntry v-for="selected in modelAccess.get(model)" :key="selected.collectableName"
                                  :model-access="{
                                  get: model => selected,
                                  where: buildWhere(selected.collectableName),
                                  testSchematron: modelAccess.testSchematron
    }"
                                  :currently-selected="collectableEntryConfig2DataRow(allPossibleData, selected)"
                                  :currently-selected-amount="selected.collectableData?.amountNeeded"
                                  :collectable-text-prefix="collectableTextPrefix" :show-image="showImage"
                                  :disabled="disabled" :possible-data="removeSelectedDataFromPossibleData()"
                                  :collectable-amount-prefix="collectableAmountPrefix"
                                  @updateCurrentlySelected="(newlySelectedData: DataRow) => updateSelectedData(collectableEntryConfig2DataRow(allPossibleData, selected), newlySelectedData)"
                                  @deleteEntry="deleteDataRow"
      />
    </div>
  </div>

</template>

<script setup lang="ts">


  import type { DataRow } from '@/models/data_row'
  import type { CollectableDataConfig, CollectableEntryConfig } from '@/models/goals'
  import SingleCollectableGoalEntryPlaceholder from '@/components/goals/SingleCollectableGoalEntryPlaceholder.vue'
  import SingleCollectableGoalEntry from '@/components/goals/SingleCollectableGoalEntry.vue'
  import { ref, useModel } from 'vue'
  import type { ModelAccess } from '@/main'
  import { useModelStore } from '@/stores/model'

  const props = defineProps<{
    modelAccess: ModelAccess<CollectableEntryConfig[]>
    allPossibleData: DataRow[],
    collectableTextPrefix: string,
    collectableAmountPrefix: string,
    dropdownPlaceholderText: string,
    showImage: boolean
    renderSelection: boolean
    disabled: boolean
  }>()

  const { model, set, add } = useModelStore()

  function buildWhere(code: string): string {
    return `${props.modelAccess.where}.[${props.modelAccess.get(model)?.findIndex((entry: CollectableEntryConfig) => entry.collectableName === code)}]`
  }


  function updateSelectedData(currentlySelectedData: DataRow | undefined, newSelectedData: DataRow) {
    if (currentlySelectedData === newSelectedData) {
      // the user clicked on a SingleCollectableGoalEntry (not placeholder) and selected the collectable that was already selected => nothing changed
      return
    }
    const newlyAdded: CollectableEntryConfig = {
      collectableName: newSelectedData.code,
      collectableData: {},
    }
    pushOrOverrideNewlyAdded(currentlySelectedData, newlyAdded)
  }

  function pushOrOverrideNewlyAdded(currentlySelectedData: DataRow | undefined, newlyAdded: CollectableEntryConfig) {
    add(props.modelAccess.where, newlyAdded, props.modelAccess.testSchematron, elInWhere => (elInWhere as CollectableEntryConfig).collectableName == currentlySelectedData?.code)
  }

  function deleteDataRow(toDelete: DataRow) {
    console.log("deleting", toDelete.code, "at", buildWhere(toDelete.code))
    //set(buildWhere(toDelete.code), undefined, false)
    const entries = props.modelAccess.get(model)!
    const idx: number = entries.findIndex((entry: CollectableEntryConfig) => entry.collectableName == toDelete.code)
    if(idx === -1) {
      throw Error(`Failed to retrieve ${toDelete.code} from ${entries} when setting the amount.`)
    }
    set(props.modelAccess.where, entries.toSpliced(idx, 1), false)
  }

  function removeSelectedDataFromPossibleData(): DataRow[] {
    if (props.modelAccess.get(model) === undefined) {
      return props.allPossibleData
    }
    const asCodes: string[] = collectableEntryConfig2Code(props.modelAccess.get(model)!)
    return props.allPossibleData.filter((dataRow: DataRow) => !asCodes!.includes(dataRow.code))
  }

  function collectableEntryConfig2Code(source: CollectableEntryConfig[]): string[] {
    return source.map((element: CollectableEntryConfig) => element.collectableName)
  }

  function collectableEntryConfig2DataRow(source: DataRow[], entryConfig: CollectableEntryConfig): DataRow {
    const found = source.find((dataRow: DataRow) => dataRow.code === entryConfig.collectableName)
    if (found === undefined) {
      throw Error(`Failed to map ${found} to DataRow, because no matching code can be found. Source is: ${source}`)
    }
    return found
  }

</script>