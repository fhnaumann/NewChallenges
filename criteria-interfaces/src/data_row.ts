/**
 * A "data row" containing the necessary information for a "single point of information".
 * For example a data row could be a single block, mob, item, etc.
 */
export interface DataRow {
  /** The internal code that is used for building and accessing paths in the configuration store/file. */
  code: string

  /** The path to the associated image. */
  img_name: string
  /** The minecraft translation key that is used to get the actual display label from a language json file. */
  translation_key: string
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

export interface DeathMessageDataRow extends DataRow {
  deathMessage: string
}

export interface CraftingTypeDataRow extends DataRow {
  result: string,
  recipeType: string
  source?: string
}



