import { onMounted, ref } from 'vue'
import type { Model } from '@criteria-interfaces/model'
import type { DataConfig, MCEvent } from '@criteria-interfaces/live'
import { ALL_MATERIAL_DATA } from '@/composables/data_row_loaded'
import { BASE_IMG_URL } from '@/constants'
import { useChallengeState } from '@/stores/challenge_state'

export function useFetcher(challengeID: string) {

  const challenge_state = useChallengeState()

  const loaded = ref(false)
  const error = ref<string | null>(null)

  const ws = new WebSocket(
    `wss://oaxuru4o1c.execute-api.eu-central-1.amazonaws.com/production/?client_type=live-website&challenge_ID=${challengeID}`
  )

  async function fetchEvents(challengeID: string) {
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
    console.log('LocalStorage challenge file empty or outdated. Fetching from S3...')
    let eventsFromDynamoDB = await fetchEventsFromDynamoDB(challengeID)
    eventsFromDynamoDB = eventsFromDynamoDB.filter(value => !['start', 'resume', 'pause', 'end'].includes(value.eventType))
    console.log(eventsFromDynamoDB)
    console.log('Storing in localstorage', eventsFromDynamoDB)
    localStorage.setItem(`challenge-events-${challengeID}`, JSON.stringify(eventsFromDynamoDB))
    challenge_state.events = eventsFromDynamoDB
  }

  async function fetchEventsFromDynamoDB(challengeID: string): Promise<MCEvent<any>[]> {
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
    const response = await fetch(`https://existing-challenges-s3uploadbucket-rlhqqq6a5mfn.s3.eu-central-1.amazonaws.com/${challengeID}.json`)

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

  onMounted(async () => {
    await fetchData()
    await preloadImages()
    loaded.value = true
    console.log("finished")
    //timeEstimation.value = events.value.length !== 0 ? events.value[events.value.length - 1].timestamp : 0
    //resume()

  })

  return {
    reFetchData,
    loaded,
    error,
    ws
  }
}