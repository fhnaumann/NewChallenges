import { useConfigStore } from '@/main'
import { js2xml } from 'xml-js'
import schematronFile from '@/assets/constraints.sch?raw'
import { Schema } from 'node-schematron'
import type { Model } from './model/model'
import { toast } from '@/main'

type DoChangesOnConfig = (config: Model) => void

const schema: Schema = Schema.fromString(schematronFile)

export function useValidator() {
  function isValid(model: Model, changes: DoChangesOnConfig) {
    console.log('rerunning validity check')

    const copy = JSON.parse(JSON.stringify(model))
    changes(copy)



    const options = { spaces: 2, compact: true }
    const xmlString = js2xml({ root: copy }, options)

    const result = schema.validateString(xmlString, { debug: true })
    const messages = result.map(violoation => violoation.message!.trim())
    if(messages.length) {
      showViolationMessage(messages)
    }
    return {
      valid: messages.length == 0,
      messages: messages ? messages : [],
    }
  }

  function showViolationMessage(messages: string[]) {
    toast.add({ 
      severity: 'error', 
      summary: 'Constraint Violation', 
      detail: messages[0], 
      life: 3000 })
  }

  return { isValid }
}
