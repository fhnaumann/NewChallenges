<template>
  <div :class="`flex flex-col p-8 space-y-1 cursor-pointer ${computedBgColor}`">
    <p class="text-2xl font-bold">{{ t(`${criteriaType}.types.${criteriaKey}.name`) }}</p>
    <p>{{ t("general.search.criteria_category", { category: t(`${criteriaType}.category`) })}}</p>
  </div>
</template>

<script setup lang="ts">

  import type { CriteriaKey, CriteriaType } from '@/models/model'
  import { useI18n } from 'vue-i18n'
  import { computed } from 'vue'
  import { getBgColor } from '@/util'

  const props = defineProps<{
    criteriaType: CriteriaType
    criteriaKey: CriteriaKey
  }>()

  const { t } = useI18n()

  const computedBgColor = computed(() => getBgColor(props.criteriaType, 2))

</script>