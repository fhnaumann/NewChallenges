const currentStage = import.meta.env.VITE_APP_STAGE
export const BASE_IMG_URL =
  process.env.NODE_ENV === 'production' ? 'https://challenges-builder.s3.eu-central-1.amazonaws.com' : ''
export const BASE_WIKI_URL = "https://www.wiki.mc-challenges.com"

export const PLUGIN_HANGAR_URL = ""
export const PLUGIN_MODRINTH_URL = "https://modrinth.com/plugin/mc-challenges"
export const CHALLENGE_BUILDER_URL = "https://www.builder.mc-challenges.com/"

export const BASE_WEBSOCKET_URL = `wss://oaxuru4o1c.execute-api.eu-central-1.amazonaws.com/${currentStage}/?client_type=live-website`
export const BASE_CHALLENGE_EVENTS_URL = `https://ffh0phd3l8.execute-api.eu-central-1.amazonaws.com/${currentStage}/events`
export const BASE_EXISTING_CHALLENGES_S3_URL = `https://existing-challenges-${currentStage}.s3.eu-central-1.amazonaws.com`