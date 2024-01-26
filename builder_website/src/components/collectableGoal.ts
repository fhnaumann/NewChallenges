import { useActivatableGoal } from "./activatableGoal";
import { computed, ref, toRefs, watch } from "vue";
import { useConfigStore } from '@/main';
//import { DataRow, useLoadableDataRow } from "./loadableDataRow";
import type { DataRow } from "./loadableDataRow";
import { useLoadableDataRow } from "./loadableDataRow";
import type { CollectableEntryConfig, GoalName } from "./model/goals";
import type { RandomEffectPunishmentConfig } from "./model/punishments";


export function useCollectableGoal(goalName: GoalName, pathSplit: string, rawLoadedDataFile: string) {

    const store = useConfigStore().model

    const {fullData, selectedData} = useLoadableDataRow(rawLoadedDataFile)

    const selectedCollectableData = ref<CollectableEntryConfig>(fromSelectedDataToInitialCollectableData(selectedData.value))
    watch(selectedCollectableData, (newSelectedCollectableData) => {
        console.log("watching selected collectable data triggered", newSelectedCollectableData)
        store.goals[goalName][pathSplit] = newSelectedCollectableData
    }, {
        deep: true
    })


    function fromSelectedDataToInitialCollectableData(selectedData: DataRow[]): CollectableEntryConfig {
        const resultObj: CollectableEntryConfig = {}
        selectedData.forEach(row => {
            resultObj[row.code] = {
                amountNeeded: 10
            }
        })
        return resultObj
    }


    const defaultObject: Object = {amount: 1} as const

    function addDataRow(currentlySelectedData: DataRow | undefined, newSelectedData: DataRow) {
        if(currentlySelectedData === newSelectedData) {
            // The user has clicked the same item that was already selected -> do nothing
            return
        }
        if(currentlySelectedData !== undefined) {
            // delete config part, since it's a different variable now
            delete store.goals[goalName][pathSplit][currentlySelectedData.code]

            store.goals[goalName][pathSplit][newSelectedData.code] = structuredClone(defaultObject)
            // replace the old selected mob with the new one in the list
            const previousSelectedDataIndex = selectedData.value.indexOf(currentlySelectedData)
            //selectedData.value.splice(previousSelectedDataIndex, 1, newSelectedData)
            selectedData.value[previousSelectedDataIndex] = newSelectedData

            console.log("after replacing: ", selectedData.value)
        }
        else {
            console.log(store.goals)
            console.log(goalName)
            // store.goals[goalName].mobs = {} //TODO remove
            store.goals[goalName][pathSplit][newSelectedData.code] = structuredClone(defaultObject)
            selectedData.value.push(newSelectedData)
        }
        // add new config part with default object



    }

    function setCollectAmount(selectedData: DataRow, amount: number) {
        //const existingAmount = store.goals[goalName][pathSplit][selectedData.code].amountNeeded

        store.goals[goalName][pathSplit][selectedData.code].amountNeeded = amount
    }

    function deleteDataRow(dataRowToDelete: string) {
        console.log("to delete", dataRowToDelete)
        // delete store.goals[goalName][pathSplit][dataRowToDelete]
        delete selectedCollectableData.value[dataRowToDelete]
    }

    function transferDataFromPlaceHolderToNewInstance(newSelectedData: DataRow) {
        addDataRow(undefined, newSelectedData)
    }

    return { fullData, selectedCollectableData, addDataRow, setCollectAmount, deleteDataRow, transferDataFromPlaceHolderToNewInstance, ...useActivatableGoal(goalName) }

}