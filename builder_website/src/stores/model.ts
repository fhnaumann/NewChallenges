import type { Model } from 'criteria-interfaces'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useModelStore = defineStore('stateModel', () => {
  const model = ref<Model>({
    metadata: {
      challengeID: generateRandomChallengeID(),
      name: 'my-challenge',
      whenCreated: new Date().toISOString(),
      lastModified: new Date().toISOString(),
      createdBy: '-',
      builderVersion: '0.0.1',
      builderMCVersion: '1.20.4',
      pluginVersion: '0.0.1',
      pluginMCVersion: '1.20.4'
    }
  })

  function mapIdxAsStringOrIndex(wherePart: string): string | number {
    if (wherePart.startsWith('[') && wherePart.endsWith(']')) {
      return parseInt(wherePart.substring(1, wherePart.length - 1))
    } else {
      return wherePart
    }
  }

  function set(where: string, what: any, testSchematron: boolean, mapper: ((what: any) => any) = unchanged => unchanged) {
    //let shallowReference = JSON.parse(JSON.stringify(model.value))
    let shallowReference = model.value

    const pList = where.split('.')
    const len = pList.length
    for (let i = 0; i < len - 1; i++) {
      const elem = mapIdxAsStringOrIndex(pList[i])
      if (!shallowReference[elem] && what !== undefined) {
        shallowReference[elem] = {}
      }

      shallowReference = shallowReference[elem]
    }
    if (what !== undefined) {
      shallowReference[pList[len - 1]] = mapper(what)
    } else {
      //shallowReference[pList[len - 1]] = what
      delete shallowReference[pList[len - 1]]
    }
    if (what === undefined) {
      //clean(model.value)
    }
    //const cleaned = clean(model.value)
  }

  function add(where: string, what: any, testSchematron: boolean, filter_func: ((elInWhere: any) => boolean)) {
    let shallowReference = model.value

    const pList = where.split('.')
    const len = pList.length
    for (let i = 0; i < len - 1; i++) {
      const elem = pList[i]
      if (!shallowReference[elem] && what !== undefined) {
        shallowReference[elem] = {}
      }

      shallowReference = shallowReference[elem]
    }
    if (!Array.isArray(shallowReference[pList[len - 1]])) {
      shallowReference[pList[len - 1]] = []
    }

    const arr = (shallowReference[pList[len - 1]] as any[])
    const idx = arr.findIndex(filter_func)
    if (idx === -1) {
      arr.push(what)
    } else {
      arr[idx] = what
    }
  }

  function clean(object: any) {
    Object.entries(object).forEach(([k, v]) => {
      if (v && typeof v === 'object') {
        clean(v)
      }
      if ((v && typeof v === 'object' && !Object.keys(v).length) || v === null || v === undefined) {
        if (Array.isArray(object)) {
          object.splice(parseInt(k), 1)
        } else {
          delete object[k]
        }
      }
    })
    return object
  }

  function generateRandomChallengeID(): string {
    const length = 12
    const charset = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'
    let result = ""
    for(let i=0; i<length; i++) {
      result += charset[Math.floor(Math.random() * charset.length)]
    }
    return result
  }

  return { model, set, add }
})
