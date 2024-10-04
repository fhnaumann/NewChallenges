import { onMounted, ref } from 'vue'
import type { Model } from '@criteria-interfaces/model'
import type { DataConfig, MCEvent } from '@criteria-interfaces/live'
import { ALL_MATERIAL_DATA } from '@/composables/data_row_loaded'
import {
  BASE_CHALLENGE_EVENTS_URL,
  BASE_EXISTING_CHALLENGES_S3_URL,
  BASE_IMG_URL,
  BASE_WEBSOCKET_URL
} from '@/constants'
import { useChallengeState } from '@/stores/challenge_state'
import { useGoalAccess } from '@/composables/goal_access'
import { useSanitizer } from '@/composables/sanitizer'

export function useFetcher(initialChallengeID: string) {

  const challengeID: string = initialChallengeID

  const challenge_state = useChallengeState()

  const loaded = ref(false)
  const error = ref<string | null>(null)

  const ws = new WebSocket(`${BASE_WEBSOCKET_URL}&challenge_ID=${challengeID}`)

  async function fetchEvents(challengeID: string) {
    // update no caching for events, just always fetch from dynamodb
    /*
    const localStorageEvents = localStorage.getItem(`challenge-events-${challengeID}`)
    if (localStorageEvents) {
      console.log('Fetched events from local storage')
      try {
        challenge_state.events = JSON.parse(localStorageEvents!)
        return
      } catch (error) {
        console.log('Unknown challenge events. Fetching from DynamoDB instead...')
      }
      console.log('LocalStorage events empty or outdated. Fetching from DynamoDB...')
    }

     */
    console.log('LocalStorage events empty or outdated. Fetching from S3...')
    let eventsFromDynamoDB = await fetchEventsFromDynamoDB(challengeID)
    console.log(eventsFromDynamoDB)
    eventsFromDynamoDB = eventsFromDynamoDB.filter(value => !['start', 'resume', 'pause', 'end'].includes(value.eventType))
    console.log(eventsFromDynamoDB)
    console.log('Storing in localstorage', eventsFromDynamoDB)
    localStorage.setItem(`challenge-events-${challengeID}`, JSON.stringify(eventsFromDynamoDB))
    challenge_state.events = eventsFromDynamoDB
  }

  async function fetchEventsFromDynamoDB(challengeID: string): Promise<MCEvent<any>[]> {
    console.log("fetching", `${BASE_CHALLENGE_EVENTS_URL}?challenge_ID=${challengeID}`)
    return fetch(`${BASE_CHALLENGE_EVENTS_URL}?challenge_ID=${challengeID}`)
      .then(value => value.json())
  }

  async function fetchChallengeFile(challengeID: string) {
    try {
      // Step 1: Fetch the HEAD request to get ETag
      const headResponse = await fetch(`${BASE_EXISTING_CHALLENGES_S3_URL}/${challengeID}.json`, {
        method: 'HEAD'
      })

      if (!headResponse.ok) {
        error.value = 'loading.challenge_not_exist'
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
            challenge_state.challengeFileJSON = challengeJSON.data
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
      challenge_state.challengeFileJSON = challengeJSON

    } catch (error) {
      console.error('Error fetching challenge file:', error)
    }
  }

  async function fetchChallengeFileFromS3(challengeID: string) {
    const response = await fetch(`${BASE_EXISTING_CHALLENGES_S3_URL}/${challengeID}.json`)

    if (!response.ok) {
      throw new Error('Failed to fetch challenge file from S3')
    }

    return response
  }

  async function preloadImages() {
    return Promise.all(ALL_MATERIAL_DATA.map(value => {
      return new Promise<void>((resolve, reject) => {
        const image = new Image()
        image.src = BASE_IMG_URL + '/rendered_images/' + value.img_name
        image.onerror = event => {
          image.src = '/unknown.png'
        }
        resolve()
      })
    })).then(value => console.log("after promise"))
  }

  async function fetchData() {
    await fetchChallengeFile(challengeID)
    await fetchEvents(challengeID)
  }

  async function reFetchData() {
    localStorage.removeItem(`challenge-${challengeID}`)
    localStorage.removeItem(`challenge-events-${challengeID}`)
    await fetchData()
  }

  async function performSanityCheck() {
    const { addMissingEvents } = useSanitizer()
    addMissingEvents()

  }

  onMounted(async () => {
    await fetchData()
    await preloadImages()
    await performSanityCheck()
    loaded.value = true
    console.log("finished")
    //timeEstimation.value = events.value.length !== 0 ? events.value[events.value.length - 1].timestamp : 0
    //resume()

  })

  return {
    challengeID,
    reFetchData,
    loaded,
    error,
    ws
  }
}