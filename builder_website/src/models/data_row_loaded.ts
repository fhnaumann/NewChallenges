import materials from '@/assets/data_rows/materials.json'
import entity_types from '@/assets/data_rows/entity_types.json'
import death_messages from '@/assets/data_rows/death_messages_with_dummy_data_data_source_JSON.json'
import craftables from '@/assets/data_rows/craftables.json'
import type { CollectableDataConfig, CollectableEntryConfig } from '@fhnaumann/criteria-interfaces'
import type {
  CraftingTypeDataRow,
  DataRow,
  DeathMessageDataRow,
  EntityTypeDataRow,
  MaterialDataRow,
} from '@fhnaumann/criteria-interfaces'

export const ALL_MATERIAL_DATA: MaterialDataRow[] = materials.data
export const ALL_IS_ITEM_MATERIAL_DATA: MaterialDataRow[] = ALL_MATERIAL_DATA.filter(value => value.is_item)
export const ALL_IS_BLOCK_MATERIAL_DATA: MaterialDataRow[] = ALL_MATERIAL_DATA.filter(value => value.is_block)
export const ALL_ENTITY_TYPE_DATA: EntityTypeDataRow[] = entity_types.data
export const ALL_DEATH_MESSAGES_DATA: DeathMessageDataRow[] = death_messages.data as any // TODO: death messages require img path and mc translation code, but neither really exists
export const ALL_RECIPES: CraftingTypeDataRow[] = craftables.data as any // TODO: crafting types require img path and mc translation code, but neither really exists

export function fromCode2DataRow(code: string): DataRow {
  let match: DataRow | undefined = ALL_MATERIAL_DATA.find(value => value.code === code)
  if (match !== undefined) {
    return match!
  }
  match = ALL_ENTITY_TYPE_DATA.find(value => value.code === code)
  if (match !== undefined) {
    return match!
  }
  match = ALL_DEATH_MESSAGES_DATA.find(value => value.code === code)
  if(match !== undefined) {
    return match!
  }
  match = ALL_RECIPES.find(value => value.code === code)
  if(match !== undefined) {
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