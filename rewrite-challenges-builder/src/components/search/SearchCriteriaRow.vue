<template>
  <div :class="`flex flex-row px-8 my-2 space-y-1 justify-between items-center cursor-pointer ${computedBgColor}`">
    <div class="flex flex-col justify-center">
      <p class="text-2xl font-bold">{{ t(`${criteriaType}.types.${criteriaKey}.name`) }}</p>
      <p>{{ t('general.search.criteria_category', { category: t(`${criteriaType}.category`) }) }}</p>
    </div>
    <InputIcon>
      <i class="pi pi-angle-right" />
    </InputIcon>
  </div>
</template>

<script setup lang="ts">

  import type { CriteriaKey, CriteriaType } from '@/models/model'
  import { useI18n } from 'vue-i18n'
  import { computed } from 'vue'
  import { getBgColor } from '@/util'
  import InputIcon from 'primevue/inputicon'

  const props = defineProps<{
    criteriaType: CriteriaType
    criteriaKey: CriteriaKey
  }>()

  const { t } = useI18n()

  const computedBgColor = computed(() => getBgColor(props.criteriaType, 2))

</script>