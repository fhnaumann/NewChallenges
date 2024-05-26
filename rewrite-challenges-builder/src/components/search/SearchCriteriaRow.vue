<template>
  <div :class="`flex flex-row flex-1 px-8 my-2 space-y-1 justify-between items-center cursor-pointer ${computedBgColor}`" @click="createEmptyCriteria(); closeDialog()">
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
  import { computed, inject } from 'vue'
  import { getBgColor, pathToCriteria } from '@/util'
  import InputIcon from 'primevue/inputicon'
  import { useRouter } from 'vue-router'
  import { useModelStore } from '@/stores/model'

  const props = defineProps<{
    criteriaType: CriteriaType
    criteriaKey: CriteriaKey
  }>()

  const { t } = useI18n()
  const router = useRouter()
  const { model, set } = useModelStore()

  const dialogRef = inject('dialogRef') as any
  const closeDialog = () => {
    dialogRef.value.close({
      navigateTo: `/${props.criteriaType}/${props.criteriaKey}`
    })
  }

  const createEmptyCriteria = () => {
    // TODO: don't overwrite an existing criteria object if one already exists
    set(`${pathToCriteria(props.criteriaType)}.${props.criteriaKey}`, {}, true)
  }

  const computedBgColor = computed(() => getBgColor(props.criteriaType, true))

</script>