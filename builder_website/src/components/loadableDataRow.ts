import { computed, defineComponent, ref, toRefs, watch } from "vue";
import type { Model } from "./model/model";

/**
 * A "data row" containing the necessary information for a "single point of information".
 * For example a data row could be a single block, mob, item, etc.
 * @interface DataRow
 * @member {string} The internal code that is used for building and accessing paths in the configuration store/file.
 * @member {string} The path to the associated image.
 * @member {string} The label that is displayed to the user.
*/
export interface DataRow {
    /** The internal code that is used for building and accessing paths in the configuration store/file. */
    code: string;
    
    /** The path to the associated image. */
    image: string;
    /** The label that is displayed to the user. */
    label: string;
    /** Whether this element is considered an item (by Spigot's 'Material#isItem' method). */
    isItem: boolean;
    /** Whether this element is considered a block (by Spigot's 'Material#isBlock' method). */
    isBlock: boolean;
}

export type Getter = (model: Model) => DataRow[]
export type Setter = (model: Model, value: DataRow[]) => void

export function useLoadableDataRow(rawLoadedDataFile: string) {
    /**
     * Contains every row exactly once. Essentially an in-memory copy of the rawLoadedDataFile
     */
    const fullData: readonly DataRow[] = loadData(rawLoadedDataFile).sort((a, b) => a.label.localeCompare(b.label))
    /**
     * Contains the data rows that are currently selected
     */
    const selectedData = ref<DataRow[]>([])
    /*const selectedData = computed<DataRow[]>(() => {
        console.log("triggered read")
        return getSelectedData(model)
    })*/

    function loadData(rawLoadedDataFile: string): DataRow[] {
        return rawLoadedDataFile.split("\r\n").map((entry: string) => {
            const splitted: string[] = entry.split(",")
            return {
                code: splitted[0],
                image: splitted[1],
                label: splitted[2],
                isItem: splitted[3] === 'true',
                isBlock: splitted[4] === 'true'
            }
        })
    }
    return {fullData, selectedData}
}