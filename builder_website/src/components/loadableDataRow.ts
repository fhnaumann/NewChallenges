import { computed, defineComponent, ref, toRefs, watch } from "vue";
import type { Model } from "./model/model";
import materials from '../assets/materials.json'

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
    img_name: string;
    /** The minecraft translation key that is used to get the actual display label from a language json file. */
    translation_key: string;

}

export interface MaterialDataRow extends DataRow {
    /** Whether this element is considered an item (by Spigot's 'Material#isItem' method). */
    is_item: boolean;
    /** Whether this element is considered a block (by Spigot's 'Material#isBlock' method). */
    is_block: boolean;
}

export type Getter = (model: Model) => DataRow[]
export type Setter = (model: Model, value: DataRow[]) => void

export const allMaterialData: MaterialDataRow[] = materials.data;
export const allBlockMaterialData: MaterialDataRow[] = materials.data.filter(mat => mat.is_block)
export const allItemMaterialData: MaterialDataRow[] = materials.data.filter(mat => mat.is_item)