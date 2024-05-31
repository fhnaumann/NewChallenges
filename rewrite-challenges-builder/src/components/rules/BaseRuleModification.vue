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
  import type { CriteriaKey, CriteriaType } from '@/models/model'
  import Checkbox from 'primevue/checkbox'
  import { computed, ref, watch } from 'vue'
  import PunishmentList from '@/components/rules/PunishmentList.vue'
  import { useI18n } from 'vue-i18n'
  import { useModelStore } from '@/stores/model'

  const props = defineProps<{
    criteriaType: CriteriaType,
    criteriaKey: CriteriaKey
    relativeURLToWiki: string
  }>()

  const { t } = useI18n()
  const { model } = useModelStore()

  const punishmentsEnabled = ref<boolean>(model.rules?.enabledRules?.[props.criteriaKey]?.punishments !== undefined ? Object.keys(model.rules?.enabledRules?.[props.criteriaKey]?.punishments!).length !== 0 : false)

</script>