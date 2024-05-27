import materials from '@/assets/data_rows/materials.json'
import entity_types from '@/assets/data_rows/entity_types.json'
import type { CollectableDataConfig, CollectableEntryConfig } from '@/models/goals'

/**
 * A "data row" containing the necessary information for a "single point of information".
 * For example a data row could be a single block, mob, item, etc.
 */
export interface DataRow {
  /** The internal code that is used for building and accessing paths in the configuration store/file. */
  code: string

  /** The path to the associated image. */
  img_path: string
  /** The minecraft translation key that is used to get the actual display label from a language json file. */
  mc_translation_key: string
}

export interface MaterialDataRow extends DataRow {
  /** Whether this element is considered an item (by Spigot's 'Material#isItem' method). */
  is_item: boolean
  /** Whether this element is considered a block (by Spigot's 'Material#isBlock' method). */
  is_block: boolean
}

export interface EntityTypeDataRow extends DataRow {
  /* Nothing to add for now */
}

export const ALL_MATERIAL_DATA: MaterialDataRow[] = materials.data
export const ALL_IS_ITEM_MATERIAL_DATA: MaterialDataRow[] = ALL_MATERIAL_DATA.filter(value => value.is_item)
export const ALL_IS_BLOCK_MATERIAL_DATA: MaterialDataRow[] = ALL_MATERIAL_DATA.filter(value => value.is_block)
export const ALL_ENTITY_TYPE_DATA: EntityTypeDataRow[] = entity_types.data

export function fromCode2DataRow(code: string): DataRow {
  let match: DataRow | undefined = ALL_MATERIAL_DATA.find(value => value.code === code)
  if (match !== undefined) {
    return match!
  }
  match = ALL_ENTITY_TYPE_DATA.find(value => value.code === code)
  if (match !== undefined) {
    return match!
  }
  throw new Error(`Couldn't find match for ${JSON.stringify(code)}.`)
}

export function fromCodeArray2DataRowArray(codes: string[] | undefined): DataRow[] | undefined {
  return codes?.map(value => fromCode2DataRow(value))
}

export function fromDataRow2Code(dataRow: DataRow): string {
  return dataRow.code
}

export function fromDataRowArray2CodeArray(dataRows: DataRow[]): string[] {
  return dataRows.map(value => fromDataRow2Code(value))
}

export function fromDataRowArray2CollectableEntryArray(dataRows: DataRow[], defaultData: CollectableDataConfig | undefined = undefined): CollectableEntryConfig[] {
  if(defaultData === undefined) {
    defaultData = {}
  }
  return dataRows.map(value => {
    return {
      collectableName: value.code,
      collectableData: defaultData
    }
  })
}
