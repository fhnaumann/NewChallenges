<template>
  <div v-if="!loaded" class="flex h-screen">
    <ProgressSpinner class="m-auto" />
  </div>
  <div v-else-if="error" class="flex h-screen">
    <h1 class="m-auto text-4xl">{{ error }}</h1>
  </div>
  <div v-else class="flex min-h-screen flex-col customized-setting">
    <div class="fixed top-0 left-1/2 z-10 border-2 border-black">
      <Button label="Add latest event" @click="addEvent" />
      <Button label="Delete latest event" @click="deleteEvent" />
      <p>{{ timeEstimation }}</p>
    </div>
    <div class="fixed top-0 right-0 border-2 border-black">
      <RightSideBar :challenge="challengeFileJSON!" :events="events" />
    </div>
    <div class="mt-20 relative flex-1 min-h-screen z-5" ref="scrollContainer">
      <div class="absolute left-1/2 top-0  border-2 border-purple-500">
        <!--svg class="border-2 border-blue-500" width="50" :height="svgHeight">
          <line v-for="(ignored, index) in timeEstimation" :key="index" :x1="startX"
                :y1="startY + lineLengthPerSecond*index" :x2="startX" :y2="startY + lineLengthPerSecond*(index+1)"
                stroke="black" stroke-width="2" class="animate-draw-line" />
          <circle :cx="startX" :cy="startY" r="5" fill="blue" />
          <circle
            v-for="(event, index) in events"
            :key="event.eventID"
            :cx="startX"
            :cy="startY + lineLengthPerSecond * event.timestamp"
            r="5"
            fill="red"
          />
        </svg-->
        <svg class="border-2 border-blue-500" width="50" :height="svgHeight">
          <g ref="lines">

          </g>
          <circle :cx="startX" :cy="startY" r="5" fill="blue" />
          <circle
            v-for="(event) in events"
            :key="event.eventID"
            :cx="startX"
            :cy="startY + lineLengthPerSecond * event.timestamp"
            r="5"
            fill="red"
          />
        </svg>
        <div ref="bottomOfSVG">
        </div>
      </div>
      <div class="absolute left-1/2 top-0 z-5">
        <div class="border-2 border-red-500 z-5">
          <NoBlockBreakRuleEventBox class="absolute z-5"
                                    v-for="(event) in events"
                                    :key="'textbox-' + event.eventID"
                                    :style="{left: `${startX}px`, top: `${startY + lineLengthPerSecond * event.timestamp}px`}"
                                    :data="{
        broken: 'stone',
        player: {
          playerName: 'wand555',
          playerUUID: 'c41c9dcf-20cb-406d-aacd-bde2320283c6',
          skinTextureURL: 'http://textures.minecraft.net/texture/78c0ae51af17c299a4cff889054f04db731f490483614fa14588c45822fb6970'
        },
        amount: 1,
        appliedPunishments: [],
        timestamp: 5
        }" />
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
import NoBlockBreakRuleEventBox from '@/components/NoBlockBreakRuleEventBox.vue'
import PlayerHead from '@/components/PlayerHead.vue'
import RightSideBar from '@/components/RightSideBar.vue'
import { useFetcher } from '@/composables/fetcher'

const router = useRouter()
const route = useRoute()

const startX = 25
const startY = 5
const lineLength = 100
const lineLengthPerSecond = 2

const lines = ref()

function addLine(index: number) {
  index = index - 1
  const newLine = document.createElementNS('http://www.w3.org/2000/svg', 'line')
  newLine.setAttribute('x1', startX)
  newLine.setAttribute('y1', (startY + lineLengthPerSecond * index) as unknown as string)
  newLine.setAttribute('x2', startX)
  newLine.setAttribute('y2', startY + lineLengthPerSecond * (index + 1))
  newLine.setAttribute('stroke', 'black')
  newLine.setAttribute('stroke-width', '2')
  newLine.classList.add('animate-draw-line')

  lines.value.appendChild(newLine)
}

const bottomOfSVG = ref(null)

function addEvent() {
  console.log('add Event')

  const fakeEvent = {
    challengeID: route.params.challenge_id as string,
    eventID: 'myEvent',
    timestamp: timeEstimation.value,
    eventType: 'noBlockPlace',
    data: { playerUUID: 'wand555' }
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


const ws = new WebSocket(`wss://oaxuru4o1c.execute-api.eu-central-1.amazonaws.com/production/?client_type=live-website&challenge_ID=${route.params.challenge_id}`)
ws.onopen = ev => {
  console.log('Connected to server!')
  console.log(ev)
}

ws.onclose = ev => {
  console.log('Disconnected from server!')
}
ws.onmessage = ev => {
  console.log('Received message:', ev.data)
  const mcEvent = JSON.parse(ev.data) as MCEvent<any>
  events.value.push(mcEvent)
  timeEstimation.value = mcEvent.timestamp // account for any drift that may have occurred on the MC server
}

const { counter: timeEstimation, reset, pause, resume } = useInterval(1000, {
  controls: true,
  immediate: false,
  callback(count) {
    addLine(count)
  }
})

const {
  challengeFileJSON,
  events,
  loaded,
  error
} = useFetcher(route.params.challenge_id as string)

watch(loaded, (newValue) => {
  if (newValue) {
    console.log("Successfully loaded")
    timeEstimation.value = events.value.length !== 0 ? events.value[events.value.length - 1].timestamp : 0
    resume()
  }
})

</script>