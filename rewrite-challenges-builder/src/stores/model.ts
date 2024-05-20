import type { Model } from '@/models/model'
import { defineStore } from 'pinia'
import { ref, toRaw, watch } from 'vue'
import type { BlockBreakGoalConfig } from '@/models/blockbreak'

export const useModelStore = defineStore('model', () => {
  const model = ref<Model>({})

  function set(where: string, what: any, testSchematron: boolean, mapper: ((what: any) => any)=unchanged => unchanged) {
    //let shallowReference = JSON.parse(JSON.stringify(model.value))
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
    if(what !== undefined) {
      shallowReference[pList[len - 1]] = mapper(what)
    }
    else {
      //shallowReference[pList[len - 1]] = what
      delete shallowReference[pList[len - 1]]
    }
    if(what === undefined) {
      //clean(model.value)
    }
    //const cleaned = clean(model.value)
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

  return { model, set }
})
