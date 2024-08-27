import { defineStore } from 'pinia'
import modelSchema from '../assets/challenges_schema.json'

export const useJSONSchemaConfig = defineStore('jsonSchemaConfig', () => {
  return modelSchema.definitions
})