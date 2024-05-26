import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useVarHelperStore = defineStore('var_helper', () => {
  const killAllMobsOnce = ref<boolean>(false)
  const breakAllBlocksOnce = ref<boolean>(false)
  return { killAllMobsOnce, breakAllBlocksOnce }
})