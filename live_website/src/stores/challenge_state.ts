import { defineStore } from 'pinia'
import { computed, ref, watch } from 'vue'
import type { Model } from '@criteria-interfaces/model'
import type { DataConfig, MCEvent } from '@criteria-interfaces/live'

export const useChallengeState = defineStore('challengeState', () => {

  const challengeFileJSON = ref<Model>()
  const events = ref<MCEvent<DataConfig>[]>([])

  const started = ref( events.value !== undefined && events.value.length !== 0)
  const paused = ref(events.value !== undefined && (events.value.at(-1)?.eventType === 'pause' ?? false))
  const finished = ref(events.value !== undefined && (events.value.at(-1)?.eventType === 'end' ?? false))

  const running = ref(started.value && !paused.value && !finished.value)

  watch(events, (newEvents) => {
    started.value = newEvents !== undefined && newEvents.length !== 0
    paused.value = newEvents !== undefined && (newEvents.at(-1)?.eventType === 'pause' ?? false)
    finished.value = newEvents !== undefined && (newEvents.at(-1)?.eventType === 'end' ?? false)

    running.value = started.value && !paused.value && !finished.value
  })

  return {
    challengeFileJSON,
    events,
    started,
    paused,
    finished,
    running
  }
})