<template>
  <div class="sticky top-0 z-50 flex">
    <Button label="Add latest event" @click="addEvent" />
    <Button label="Delete latest event" @click="deleteEvent" />
    <p>{{ timeEstimation }}</p>
  </div>
  <div class="relative">
    <div>
      <p>{{ route.params.challenge_id }}</p>
      <p>{{ events }}</p>
    </div>
    <div class="flex justify-center items-center py-10 overflow-y-auto">
      <svg width="50" :height="svgHeight">
        <line v-for="(ignored, index) in timeEstimation" :key="index" :x1="startX"
              :y1="startY + lineLengthPerSecond*index" :x2="startX" :y2="startY + lineLengthPerSecond*(index+1)"
              stroke="black" stroke-width="2" class="animate-draw-line" />
        <circle :cx="startX" :cy="startY" r="5" fill="blue" />
        <circle v-for="(event, index) in events" :key="event.eventID" :cx="startX" :cy="startY + lineLengthPerSecond*event.timestamp" r="5" fill="red" class="animate-fade-circle-in" />
      </svg>


      <!--svg width="50" :height="svgHeight">
        <template v-for="(event, index) in events" :key="event.eventID">
          <line :x1="startX" :y1="startY + lineLength*index" :x2="startX" :y2="startY + lineLength*(index+1)"
                stroke="black" stroke-width="2"
                class="animate-draw-line" />
        </template>
        <circle :cx="startX" :cy="startY" r="5" fill="blue" />
        <circle v-for="(event, index) in events" :key="event.eventID" :cx="startX" :cy="startY + lineLength*(index+1)"
                r="5" fill="red"
                class="animate-fade-circle-in" />
      </svg-->
    </div>
    <div class="flex h-12" ref="bottomDiv">
      <p>BOTTOM</p>
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
import { computed, onMounted, ref, watch } from 'vue'
import type { Data, MCEvent } from '@/MCEvent'
import { useInterval } from '@vueuse/core'

const router = useRouter()
const route = useRoute()

const startX = 25
const startY = 5
const lineLength = 100
const lineLengthPerSecond = 2

const events = ref<MCEvent<Data>[]>([])

function addEvent() {
  events.value.push({
    challengeID: route.params.challenge_id as string,
    eventID: 'myEvent',
    timestamp: timeEstimation.value,
    eventType: 'noBlockPlace',
    data: { playerUUID: 'wand555' }
  })
  bottomDiv.value!.scrollIntoView({ behaviour: 'smooth' })
}

function deleteEvent() {
  events.value.splice(events.value.length - 1, 1)
}

const svgHeight = computed(() => {
  return 100 + lineLengthPerSecond * timeEstimation.value
})

/*
const ws = new WebSocket(`wss://oaxuru4o1c.execute-api.eu-central-1.amazonaws.com/production/?client_type=live-website&challenge_ID=${route.params.challenge_id}`)
ws.onopen = ev => {
  console.log("Connected to server!")
  console.log(ev)
}

ws.onclose = ev => {
  console.log("Disconnected from server!")
}
ws.onmessage = ev => {
  console.log("Received message:", ev.data)
  events.value.push(JSON.parse(ev.data))
}
*/

events.value.push({
  challengeID: route.params.challenge_id as string,
  eventID: 'myEvent',
  timestamp: 123,
  eventType: 'noBlockPlace',
  data: { playerUUID: 'wand555' }
})


const bottomDiv = ref(null)

const { counter: timeEstimation, reset, pause, resume } = useInterval(1000, {
  controls: true,
  immediate: false,
  callback(count) {
    console.log(count)
  }
})

onMounted(() => {
  timeEstimation.value = 60*60*5
  resume()
})

const counts = ref<number[]>([])


</script>