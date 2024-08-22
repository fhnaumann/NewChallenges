<template>
  <div v-if="!loaded" class="flex h-screen">
    <ProgressSpinner class="m-auto" />
  </div>
  <div v-else-if="error" class="flex h-screen">
    <h1 class="m-auto text-4xl">{{ error }}</h1>
  </div>
  <div v-else class="flex min-h-screen flex-col">
    <div class="fixed top-0 left-1/2 z-10 border-2 border-black">
      <Button label="Add latest event" @click="addEvent" />
      <Button label="Delete latest event" @click="deleteEvent" />
      <p>{{ timeEstimation }}</p>
    </div>
    <div class="mt-20 relative flex-1 min-h-screen z-5" ref="scrollContainer">
      <div class="absolute left-1/2 top-0  border-2 border-purple-500">
          <svg class="border-2 border-blue-500" width="50" :height="svgHeight">
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
          </svg>
        <div ref="bottomOfSVG">
        </div>
      </div>
      <div class="absolute left-1/2 top-0 z-5">
        <div class="border-2 border-red-500 z-5">
          <NoBlockBreakRuleEventBox class="absolute z-5"
                                    v-for="(event, index) in events"
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
import type { Data, MCEvent } from '@/MCEvent'
import { useInterval } from '@vueuse/core'
import type { Model, NoBlockBreakRuleDataConfig } from 'criteria-interfaces'
import ProgressSpinner from 'primevue/progressspinner'
import NoBlockBreakRuleEventBox from '@/components/NoBlockBreakRuleEventBox.vue'
import PlayerHead from '@/components/PlayerHead.vue'

const router = useRouter()
const route = useRoute()

const startX = 25
const startY = 5
const lineLength = 100
const lineLengthPerSecond = 2

const hoveredCircle = ref<string>('')

const events = ref<MCEvent<Data>[]>([])
const circles = ref([])

watch(events, () => {
  console.log("updated events")
  console.log(events)
})

const bottomOfSVG = ref(null)

function addEvent() {
  console.log("add Event")

  events.value.push({
    challengeID: route.params.challenge_id as string,
    eventID: 'myEvent',
    timestamp: timeEstimation.value,
    eventType: 'noBlockPlace',
    data: { playerUUID: 'wand555' }
  })
  bottomOfSVG.value.scrollIntoView({behavior: 'smooth'})
  //window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'})

}

function deleteEvent() {
  events.value.splice(events.value.length - 1, 1)
  circles.value.splice(circles.value.length - 1, 1)
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


const challengeFileJSON = ref<Model>()

async function fetchEvents(challengeID: string) {
  const localStorageEvents = localStorage.getItem(`challenge-events-${challengeID}`)
  if (localStorageEvents) {
    console.log('Fetched events from local storage')
    try {
      events.value = JSON.parse(localStorageEvents!)
      return
    } catch (error) {
      console.log('Unknown challenge events. Fetching from DynamoDB instead...')
    }
    console.log('LocalStorage events empty or outdated. Fetching from DynamoDB...')
  }
  console.log('LocalStorage challenge file empty or outdated. Fetching from S3...')
  const eventsFromDynamoDB = await fetchEventsFromDynamoDB(challengeID)

  console.log(eventsFromDynamoDB)
  console.log('Storing in localstorage', eventsFromDynamoDB)
  localStorage.setItem(`challenge-events-${challengeID}`, JSON.stringify(eventsFromDynamoDB))
  events.value = eventsFromDynamoDB
}

async function fetchEventsFromDynamoDB(challengeID: string) {
  return fetch(`https://ffh0phd3l8.execute-api.eu-central-1.amazonaws.com/events?challenge_ID=${challengeID}`)
    .then(value => value.json())
}

async function fetchChallengeFile(challengeID: string) {
  try {
    // Step 1: Fetch the HEAD request to get ETag
    const headResponse = await fetch(`https://existing-challenges-s3uploadbucket-rlhqqq6a5mfn.s3.eu-central-1.amazonaws.com/${challengeID}.json`, {
      method: 'HEAD'
    })

    if (!headResponse.ok) {
      error.value = 'Challenge does not exist. Make sure that the challenge is uploaded!'
      throw new Error('Failed to fetch HEAD request')
    }

    const responseETag = headResponse.headers.get('etag')
    const challenge = localStorage.getItem(`challenge-${challengeID}`)

    if (challenge) {
      console.log('Fetched challenge file from local storage')
      console.log(challenge)
      try {
        const challengeJSON = JSON.parse(challenge)
        if (challengeJSON.etag === responseETag) {
          challengeFileJSON.value = challengeJSON.data
          return
        }
      } catch (error) {
        console.log('Unknown challenge format. Fetching from S3 instead...')
      }
    }

    console.log('LocalStorage challenge file empty or outdated. Fetching from S3...')
    const s3Response = await fetchChallengeFileFromS3(challengeID)
    const challengeJSON = await s3Response.json() // Await the JSON parsing

    console.log(challengeJSON)
    const toStore = { etag: responseETag, data: challengeJSON }
    console.log('Storing in localstorage', toStore)
    localStorage.setItem(`challenge-${challengeID}`, JSON.stringify(toStore))
    challengeFileJSON.value = challengeJSON

  } catch (error) {
    console.error('Error fetching challenge file:', error)
  }
}

async function fetchChallengeFileFromS3(challengeID: string) {
  const response = await fetch(`https://existing-challenges-s3uploadbucket-rlhqqq6a5mfn.s3.eu-central-1.amazonaws.com/${challengeID}.json`)

  if (!response.ok) {
    throw new Error('Failed to fetch challenge file from S3')
  }

  return response
}

const { counter: timeEstimation, reset, pause, resume } = useInterval(10, {
  controls: true,
  immediate: false
})

const loaded = ref(false)
const error = ref<string | null>(null)

onMounted(async () => {
  const challengeID: string = route.params.challenge_id as string
  await fetchChallengeFile(challengeID)
  await fetchEvents(challengeID)
  loaded.value = true
  timeEstimation.value = events.value.length !== 0 ? events.value[events.value.length - 1].timestamp : 0
  resume()

})


</script>