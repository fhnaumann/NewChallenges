<template>
  <BaseCriteriaModification v-bind="props">
    <template #configuration>
      <slot name="configuration"></slot>
      <Checkbox v-model="punishmentsEnabled" binary input-id="punishmentsEnabled" />
      <label for="punishmentsEnabled" class="ml-2">{{ t('punishments.local_enable') }}</label>
      <PunishmentList v-if="punishmentsEnabled" :criteria-key="criteriaKey" :global=false />
    </template>
  </BaseCriteriaModification>
</template>

<script setup lang="ts">

  import BaseCriteriaModification from '@/components/BaseCriteriaModification.vue'
  import type { CriteriaKey, CriteriaType } from 'criteria-interfaces'
  import Checkbox from 'primevue/checkbox'
  import { computed, ref, watch } from 'vue'
  import PunishmentList from '@/components/rules/PunishmentList.vue'
  import { useI18n } from 'vue-i18n'
  import { useModelStore } from '@/stores/model'
  import type { EndPunishmentConfig } from 'criteria-interfaces'

  const props = defineProps<{
    criteriaType: CriteriaType,
    criteriaKey: CriteriaKey
    relativeURLToWiki: string
  }>()

  const { t } = useI18n()
  const { model, set } = useModelStore()

  if(model.rules?.enabledRules?.[props.criteriaKey]?.punishments === undefined) {
    set(`rules.enabledRules.${props.criteriaKey}.punishments.endPunishment`, { affects: 'all' } as EndPunishmentConfig, false)
  }

  const punishmentsEnabled = ref<boolean>(model.rules?.enabledRules?.[props.criteriaKey]?.punishments !== undefined ? Object.keys(model.rules?.enabledRules?.[props.criteriaKey]?.punishments!).length !== 0 : false)

  watch(punishmentsEnabled, (newValue) => {
    if(!newValue) {
      set(`rules.enabledRules.${props.criteriaKey}.punishments`, undefined, false)
    }
  })

</script>