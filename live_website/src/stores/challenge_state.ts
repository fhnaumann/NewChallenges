import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Model } from '@criteria-interfaces/model'
import type { DataConfig, MCEvent } from '@criteria-interfaces/live'

export const useChallengeState = defineStore('challengeState', () => {

  const challengeFileJSON = ref<Model>()
  const events = ref<MCEvent<DataConfig>[]>([])
  return {
    challengeFileJSON,
    events
  }
})