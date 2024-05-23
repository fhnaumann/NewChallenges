<template>
  <div class="flex flex-col items-center space-y-2">
    <Button :label="t(`${criteriaType}.browse_button`)" />
    <p>{{ t(`${criteriaType}.global_title`) }}</p>
    <div :class="`flex flex-col p-4 space-y-2 ${computedBgColor}`">
      <ActiveCriteriaRow
        v-for="activeCriteria in getCriteria()"
        :key="activeCriteria"
        :criteria-code="activeCriteria"
        :criteria-type="criteriaType"
      >
      </ActiveCriteriaRow>
    </div>
  </div>
</template>

<script setup lang="ts">
import Button from 'primevue/button'
import { computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import ActiveCriteriaRow from '@/components/ActiveCriteriaRow.vue'
import type { CriteriaKey, CriteriaType } from '@/models/model'
import { getBgColor } from '@/util'
import { useModelStore } from '@/stores/model'

const props = defineProps<{
  criteriaType: CriteriaType
}>()

const { t } = useI18n()

const modelStore = useModelStore()

function getCriteria(): CriteriaKey[] {
  if(props.criteriaType === 'rules') {
    return (modelStore.model.rules?.enabledRules !== undefined ? Object.keys(modelStore.model.rules?.enabledRules!) : []) as CriteriaKey[]
  }
  else {
    return (modelStore.model[props.criteriaType] !== undefined ? Object.keys(modelStore.model[props.criteriaType]!) : []) as CriteriaKey[]
  }
}

const computedBgColor = computed(() => getBgColor(props.criteriaType, 1))
</script>
