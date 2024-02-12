import { useActivatableGoal } from "./activatableGoal";
import { computed, ref, toRaw, toRefs, watch } from "vue";
import { useConfigStore } from '@/main';
//import { DataRow, useLoadableDataRow } from "./loadableDataRow";
import type { DataRow } from "./loadableDataRow";
import type { BaseGoalConfig, CollectableDataConfig, CollectableEntryConfig, GoalName, GoalPathSplitKey, GoalsConfig } from "./model/goals";
import type { RandomEffectPunishmentConfig } from "./model/punishments";
import type { Model } from "./model/model";
import { useValidator } from "./validator";

export interface AccessOperation {
    getSelectedData: (model: Model) => CollectableEntryConfig[],
    setSelectedData: (model: Model, newSelectedData: CollectableEntryConfig[]) => void,
    getSelectAllData: (model: Model) => boolean,
    setSelectAllData: (model: Model, newSelectAllData: boolean) => void
}

export function useCollectableGoal(accessOperation: AccessOperation, allData: DataRow[], defaultSelectedData: CollectableEntryConfig[], defaultSelectAllData: boolean) {
    const model = useConfigStore().model
    const validator = useValidator()

    const selectedData = ref<CollectableEntryConfig[]>([])
    watch(selectedData, newSelectedData => {
        console.log("triggered watch", newSelectedData)
        accessOperation.setSelectedData(model, newSelectedData)
    }, {deep: true})

    selectedData.value = structuredClone(toRaw(defaultSelectedData))


    const selectAllData = ref<boolean>(false)
    watch(selectAllData, newSelectAllData => {
        accessOperation.setSelectAllData(model, newSelectAllData)
    })

    selectAllData.value = defaultSelectAllData

    function updateSelectedData(currentlySelectedData: string | undefined, newSelectedData: string): void {
        if(currentlySelectedData === newSelectedData) {
            return
        }

        const newlyAdded: CollectableEntryConfig = {
            collectableName: newSelectedData
        }

        const { valid, messages } = validator.isValid(model, (copy) => {
            accessOperation.getSelectedData(copy).push(newlyAdded)
        })
        if(valid) {
            pushOrOverrideNewlyAdded(currentlySelectedData, newlyAdded)
        }
    }

    function updateSelectedDataSpecificAmount(currentlySelectedData: string, newAmount: number): void {
        const entries: CollectableEntryConfig[] = accessOperation.getSelectedData(model)
        const idx = entries.findIndex((entry: CollectableEntryConfig) => entry.collectableName === currentlySelectedData)
        if(idx === -1) {
            throw Error(`Failed to retrieve ${currentlySelectedData} from ${entries} when setting the amount.`)
        }
        const collectableData: CollectableDataConfig | undefined =  entries[idx].collectableData
        if(collectableData === undefined) {
            entries[idx].collectableData = {
                amountNeeded: newAmount
            }
        }
        else {
            collectableData.amountNeeded = newAmount
        }
    }

    function deleteDataRow(toDelete: DataRow): void {
        const entries: CollectableEntryConfig[] = accessOperation.getSelectedData(model)
        const idx = entries.findIndex((entry: CollectableEntryConfig) => entry.collectableName === toDelete.code)
        if(idx === -1) {
            throw Error(`Failed to retrieve ${toDelete.code} from ${entries} when setting the amount.`)
        }
        entries.splice(idx, 1)
    }

    function pushOrOverrideNewlyAdded(currentlySelectedData: string | undefined, newlyAdded: CollectableEntryConfig): void {
        const idx = selectedData.value.findIndex((entry: CollectableEntryConfig) => entry.collectableName === currentlySelectedData)
        if(idx === -1) {
            selectedData.value.push(newlyAdded)
        }
        else {
            selectedData.value[idx] = newlyAdded
        }
    }

    function collectableEntryConfig2DataRow(source: DataRow[], entryConfig: CollectableEntryConfig): DataRow {
        const found = source.find((dataRow: DataRow) => dataRow.code === entryConfig.collectableName)
        if(found === undefined) {
            throw Error(`Failed to map ${found} to DataRow, because no matching code can be found. Source is: ${source}`)
        }
        return found
    }

    function collectableEntryConfig2Code(source: CollectableEntryConfig[]): string[] {
        return source.map((element: CollectableEntryConfig) => element.collectableName)
    }

    function copyExclude(source: DataRow[], excluding: CollectableEntryConfig[]): DataRow[] {
        return source.filter((dataRow: DataRow) => !collectableEntryConfig2Code(excluding).includes(dataRow.code))
    }

    return {
        selectedData,
        selectAllData,
        updateSelectedData,
        updateSelectedDataSpecificAmount,
        deleteDataRow,
        collectableEntryConfig2Code,
        collectableEntryConfig2DataRow,
        copyExclude
    }

}