import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useVarHelperStore = defineStore('var_helper', () => {
  const killAllMobsOnce = ref<boolean>(false)

  const breakAllBlocksOnce = ref<boolean>(false)

  const placeAllBlocksOnce = ref<boolean>(false)

  const collectEveryItemOnce = ref<boolean>(false)
  const collectAllItemsOnce = ref<boolean>(false)
  const collectAllBlockItemsOnce = ref<boolean>(false)

  const allDeathMessagesOnce = ref<boolean>(false)

  const allRecipesOnce = ref<boolean>(false)
  const allCraftingRecipesOnce = ref<boolean>(false)
  const allSmeltingRecipesOnce = ref<boolean>(false)
  const allSmithingUpgradesOnce = ref<boolean>(false)

  return {
    killAllMobsOnce,
    breakAllBlocksOnce,
    placeAllBlocksOnce,
    collectEveryItemOnce,
    collectAllItemsOnce,
    collectAllBlockItemsOnce,
    allDeathMessagesOnce,
    allRecipesOnce,
    allCraftingRecipesOnce,
    allSmeltingRecipesOnce,
    allSmithingUpgradesOnce
  }
})