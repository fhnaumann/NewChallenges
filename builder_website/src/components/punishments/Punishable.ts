import { ref, watch, computed } from 'vue'
import { useToast } from 'primevue/usetoast';
import {
  type Affects,
  type BasePunishmentConfig,
  type PunishmentName,
  type PunishmentsConfig,
} from '../model/punishments'
import type { LocalPunishmentProps, PunishmentProps } from './DefaultPunishment.vue'
import {
  useConfigStore,
  useDefaultConfigStore,
  useJSONSchemaConfigStore,
} from '@/main'
import { useValidatable } from '../validatable'
import { useValidator } from '../validator'
import type { Model } from '../model/model';
import { storeToRefs } from 'pinia'
import type { RuleName } from '../model/rules';

export function usePunishable(props: PunishmentProps, assignWhenCreated: BasePunishmentConfig) {
  const config = useConfigStore().model
  // const config = useConfigStore().model
  const defaultConfig = useDefaultConfigStore()
  const JSONSchemaConfig = useJSONSchemaConfigStore()
  console.log("usePunishable was called", props.global)

  const toast = useToast()
  const validator = useValidator()
  //const active = ref<boolean>(isPunishmentAlreadyActive())
  const active = ref<boolean>(isPunishmentAlreadyActive())
  watch(active, (newActive) => {
    console.log("watching active, new:", newActive)
    updateStore(config, newActive)
  })
  //const affects = ref<Affects>(getExistingAffects())
  const affects = ref<Affects>(getExistingAffects())
  watch(affects, newAffects => {
    console.log("new affects", newAffects)
    console.log(getPunishmentBasePath())
    getPunishmentBasePath()[props.punishmentView.id]!.affects = newAffects
  })

  function updateStore(model: Model, newActive: boolean) {
    if(newActive) {
      console.log("type is:")
      console.log(typeof getPunishmentBasePath(model))
      //Object.assign(getPunishmentBasePath(model)[props.punishmentView.id], structuredClone(assignWhenCreated))
      getPunishmentBasePath(model)[props.punishmentView.id] = structuredClone(assignWhenCreated)
    }
    else {
      delete getPunishmentBasePath(model)[props.punishmentView.id]
    }
  }

  function getExistingAffects() {
    const punishments: PunishmentsConfig = getPunishmentBasePath()
    if (Object.keys(punishments).length === 0) {
      return assignWhenCreated.affects
    }
    if(!Object.hasOwn(getPunishmentBasePath(), props.punishmentView.id)) {
      return assignWhenCreated.affects
    }
    return getPunishmentBasePath()[props.punishmentView.id]!.affects
  }

  function isPunishmentAlreadyActive(): boolean {
    const punishments: PunishmentsConfig = getPunishmentBasePath()
    if (Object.keys(punishments).length === 0) {
      return false
    }
    console.log(props.punishmentView.id)
    console.log("active?", Object.hasOwn(getPunishmentBasePath(), props.punishmentView.id))
    return Object.hasOwn(getPunishmentBasePath(), props.punishmentView.id)
  }

  function getPunishmentBasePath(innerModel: Model = config): PunishmentsConfig {
    console.log("model ", config)
    if (props.global) {
      return innerModel.rules.enabledGlobalPunishments
    } else {
      return innerModel.rules.enabledRules[(props as LocalPunishmentProps).ruleName as RuleName]!.punishments!
    }
  }

  function getDefaultAffectsOptions(): string[] {
    // Workaround: In this context we are only interested in getting a list of possible values.
    // The typescript -> json schema tool does not create a top level enum, instead every class that holds an 'affects' field has its own enum.
    // Therefore we are returning the possible values from a single punishment (end in this case), but it should be the same for any punishment
    // due to the structure in the typescript interfaces with its 'Affects' type being reused in every interface.
    return JSONSchemaConfig.EndPunishmentConfig.properties.affects.enum
  }

  return {
    active,
    affects,
    getPunishmentBasePath,
    isPunishmentAlreadyActive,
    getDefaultAffectsOptions,
    updateStore,
  }
}

export function usePunishableCommons() {

  const config = useConfigStore().model

  function getPunishmentBasePath(innerModel: Model = config, props: PunishmentProps): PunishmentsConfig {
    if(props.global) {
      return innerModel.rules.enabledGlobalPunishments
    } else {
      return innerModel.rules.enabledRules[(props as LocalPunishmentProps).ruleName!]!.punishments!
    }
  }

  return { getPunishmentBasePath }
}