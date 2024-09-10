<template>
  <div v-if="!loaded" class="flex h-screen">
    <ProgressSpinner class="m-auto" />
  </div>
  <div v-else-if="error" class="flex h-screen">
    <h1 class="m-auto text-4xl">{{ error }}</h1>
  </div>
  <div v-else class="flex min-h-screen flex-col customized-setting bg-main-img bg-cover">
    <div class="fixed top-0 left-1/2 transform -translate-x-1/2 z-10 border-2 border-black">
      <div>
        <Button label="Add latest event" @click="addEvent" />
        <Button label="Delete latest event" @click="deleteEvent" />
        <Button label="Refetch" @click="reFetchData" />
        <p v-if="!started">{{ t('misc.not_started') }}</p>
        <p v-else-if="paused">{{ t('misc.paused', {time: formatTime(timeEstimation)}) }}</p>
        <p v-else-if="finished">{{ t('misc.finished') }}</p>
        <p v-else>{{ timeEstimation }}</p></div>
    </div>
    <div class="fixed top-0 right-0 border-2 z-10 border-black">
      <RightSideBar :challenge="challengeFileJSON!" :events="events" />
    </div>
    <div class="mt-20 relative flex-1 min-h-screen z-5" ref="scrollContainer">
      <div class="absolute left-1/2 top-0 transform -translate-x-1/2">
        <svg class="drop-shadow-2xl" width="50" :height="svgHeight">
          <g ref="lines" class="pointer-events-none"></g>
          <circle class="fill-setting-800" :cx="startX" :cy="startY" r="8" />
          <circle
            v-for="event in events"
            :class="`${getCriteriaColorFrom(event)} fill-primary-800`"
            :key="event.eventID"
            :cx="startX"
            :cy="startY + lineLengthPerSecond * event.timestamp"
            r="8"
            @mouseover="console.log('MOUSE ENTER')"
            @mouseleave="console.log('MOUSE LEAVE')"
          />
        </svg>
        <div ref="bottomOfSVG"></div>
      </div>
      <div class="absolute left-1/2 top-0 z-5">
        <div class="z-5">
          <EventContainer
            class="absolute z-5 drop-shadow-2xl"
            v-for="(event, index) in events"
            :key="'textbox-' + event.eventID"
            :style="{
              left: `${determineXPositionForEventContainer(index)}px`,
              top: `${startY - 13 + lineLengthPerSecond * event.timestamp}px`
            }"
            :mc-event="event"
          />
          <!--BlockBreakEventBox
            class="absolute z-5"
            v-for="event in events"
            :key="'textbox-' + event.eventID"
            :style="{
              left: `${startX}px`,
              top: `${startY + lineLengthPerSecond * event.timestamp}px`
            }"
            :data="{
              broken: 'stone',
              player: {
                playerName: 'wand555',
                playerUUID: 'c41c9dcf-20cb-406d-aacd-bde2320283c6',
                skinTextureURL:
                  'http://textures.minecraft.net/texture/78c0ae51af17c299a4cff889054f04db731f490483614fa14588c45822fb6970'
              },
              amount: 1,
              appliedPunishments: [],
              timestamp: 5
            }"
          /-->
        </div>
      </div>
    </div>
    <div class="fixed bottom-0 left-0 w-full">
      <p>FOOTER</p>
    </div>
  </div>
</template>

<style>
@tailwind base;
@tailwind components;
@tailwind utilities;

@layer utilities {
  @keyframes draw {
    0% {
      stroke-dashoffset: 2;
    }
    100% {
      stroke-dashoffset: 0;
    }
  }
  @keyframes fadeIn {
    0% {
      opacity: 1;
      transform: scale(0);
    }
    50% {
      opacity: 1;
      transform: scale(1.5);
    }
    100% {
      opacity: 1;
      transform: scale(1);
    }
  }
  .animate-draw-line {
    stroke-dasharray: 2; /* Length of the line */
    stroke-dashoffset: 0; /* Start with the line hidden */
    animation: draw 1s forwards;
    animation-timing-function: linear;
  }

  .animate-fade-circle-in {
    opacity: 0;
    animation: fadeIn 2s forwards;
    transform-origin: center;
    transform-box: fill-box;
  }
}
</style>

<script setup lang="ts">
import Button from 'primevue/button'
import { useRoute, useRouter } from 'vue-router'
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { useInterval } from '@vueuse/core'
import type { DataConfig, MCEvent, Model, NoBlockBreakRuleDataConfig } from 'criteria-interfaces'
import ProgressSpinner from 'primevue/progressspinner'
import BlockBreakEventBox from '@/components/events/BlockBreakEventBox.vue'
import PlayerHead from '@/components/PlayerHead.vue'
import RightSideBar from '@/components/RightSideBar.vue'
import { useFetcher } from '@/composables/fetcher'
import EventContainer from '@/components/events/EventContainer.vue'
import { useUtil } from '@/composables/util'
import { useChallengeState } from '@/stores/challenge_state'
import { storeToRefs } from 'pinia'
import { useI18n } from 'vue-i18n'
import { useTimeable } from '@/composables/timable'

const router = useRouter()
const route = useRoute()
const { formatTime } = useTimeable()
const { t } = useI18n()

const startX = 25
const startY = 20
const lineLength = 100
const lineLengthPerSecond = 2

const lines = ref()

const { getCriteriaColorFrom } = useUtil()

const started = ref(false)
const paused = ref(false)
const finished = ref(false)

function addLine(index: number, skipAnimation = false) {
  index = index - 1
  const newLine = document.createElementNS('http://www.w3.org/2000/svg', 'line')
  newLine.setAttribute('x1', startX)
  newLine.setAttribute('y1', (startY + lineLengthPerSecond * index) as unknown as string)
  newLine.setAttribute('x2', startX)
  newLine.setAttribute('y2', startY + lineLengthPerSecond * (index + 1))
  newLine.setAttribute('stroke', '#1e293b') // customized-setting-800
  newLine.setAttribute('stroke-width', '6')
  if (!skipAnimation) {
    newLine.classList.add('animate-draw-line')
  }
  lines.value.appendChild(newLine)
}

function setLineTo(index: number) {
  if (index < lines.value.length) {
    console.log("set line to", index)

    const result = lines.value.splice(index)
    console.log(result)
  } else {
    const diff = index - lines.value.length
    for (let i = 0; i < diff; i++) {
      addLine(index + i, false)
    }
  }
}

function determineXPositionForEventContainer(index: number): number {
  // index % 2 == 0 ? startX : (startX - 250)
  let xPos: number

  const heightThreshold = 20 // just a guess, needs tweaking
  const amountOfEventsThatOccupySameSpace = getEventsThatAreInRangeFrom(index, heightThreshold).length
  const widthPerEventContainer = 250
  if (index % 2 == 0) {
    xPos = amountOfEventsThatOccupySameSpace * widthPerEventContainer // position even events on the right
  } else {
    xPos = -256 - amountOfEventsThatOccupySameSpace * widthPerEventContainer // position odd events on the left
  }

  return xPos
}

function getEventsThatAreInRangeFrom(
  sourceIndex: number,
  rangeAsTimestampSeconds: number
): MCEvent<any>[] {
  const sourceTimestamp = events.value[sourceIndex].timestamp
  const timestampRangeEnd = Math.max(sourceTimestamp - rangeAsTimestampSeconds, 0)
  return events.value.filter(
    (value, index) =>
      sourceIndex !== index &&
      sourceIndex % 2 === index % 2 &&
      value.timestamp >= timestampRangeEnd &&
      value.timestamp <= sourceTimestamp
  )
}

const bottomOfSVG = ref(null)

function addEvent() {
  console.log('add Event')

  const fakeEvent = {
    challengeID: route.params.challenge_id as string,
    eventID: 'myEvent',
    timestamp: timeEstimation.value,
    eventType: 'noBlockPlace',
    data: { playerUUID: 'wand555', timestamp: timeEstimation.value }
  } as MCEvent<any>

  events.value.push(fakeEvent)

  bottomOfSVG.value.scrollIntoView({ behavior: 'smooth' })
  //window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'})
}

function deleteEvent() {
  events.value.splice(events.value.length - 1, 1)
}

const svgHeight = computed(() => {
  return 100 + lineLengthPerSecond * timeEstimation.value
})



const {
  counter: timeEstimation,
  reset,
  pause,
  resume
} = useInterval(1000, {
  controls: true,
  immediate: false,
  callback(count) {
    addLine(count)
  }
})

const { challengeFileJSON, events } = storeToRefs(useChallengeState())

const { reFetchData, loaded, error, ws } = useFetcher(route.params.challenge_id as string)

ws.onopen = (ev) => {
  console.log('Connected to server!')
  console.log(ev)
}

ws.onclose = (ev) => {
  console.log('Disconnected from server!')
}
ws.onmessage = (ev) => {
  console.log('Received message:', ev.data)
  const mcEvent = JSON.parse(ev.data) as MCEvent<any>
  setLineTo(mcEvent.timestamp)
  timeEstimation.value = mcEvent.timestamp // account for any drift that may have occurred on the MC server

  handleIncomingEvent(mcEvent)
}

function handleIncomingEvent(mcEvent: MCEvent<any>) {
  if(mcEvent.eventType === 'start') {
    started.value = true
    resume()
  }
  else if(mcEvent.eventType === 'pause') {
    paused.value = true
    pause()
  }
  else if(mcEvent.eventType === 'resume') {
    paused.value = false
    resume()
  }
  else if(mcEvent.eventType === 'end') {
    finished.value = true
    pause()
  }
  else {
    events.value.push(mcEvent)
  }

}

watch(loaded, (newValue) => {
  if (newValue) {
    console.log('Successfully loaded')
    timeEstimation.value =
      events.value.length !== 0 ? events.value[events.value.length - 1].timestamp : 0

    started.value = timeEstimation.value > 0
    paused.value = events.value!.at(-1)?.eventType === 'pause'
    finished.value = events.value!.at(-1)?.eventType === 'end'
  }
})

watch(lines, (newLines) => {
  if (newLines) {
    for (let i = 0; i < timeEstimation.value; i++) {
      addLine(i, true)
    }
  }
})
</script>
