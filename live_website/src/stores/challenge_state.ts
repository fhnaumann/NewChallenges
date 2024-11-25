import { defineStore } from 'pinia'
import { computed, ref, watch } from 'vue'
import type { Model } from '@fhnaumann/criteria-interfaces'
import type { DataConfig, MCEvent } from '@fhnaumann/criteria-interfaces'

export const useChallengeState = defineStore('challengeState', () => {

  const challengeFileJSON = ref<Model>()
  const events = ref<MCEvent<DataConfig>[]>([])

  const started = ref( events.value !== undefined && events.value.length !== 0)
  const paused = ref(events.value !== undefined && (events.value.at(-1)?.eventType === 'pause'))
  const finished = ref(events.value !== undefined && (events.value.at(-1)?.eventType === 'end'))

  const running = ref(started.value && !paused.value && !finished.value)

  watch(events, (newEvents) => {
    const lastEventType = newEvents?.at(-1)?.eventType
    started.value = newEvents.length !== 0
    finished.value = lastEventType === 'end'
    running.value = lastEventType === 'start' || lastEventType === 'resume'
    paused.value = !finished.value && (lastEventType === 'pause' || !running.value)
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