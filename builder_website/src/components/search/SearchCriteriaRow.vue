<template>
  <div :class="`flex flex-row px-8 my-2 space-y-1 justify-between items-center cursor-pointer ${computedBgColor}`" @click="createEmptyOrRedirectToExistingCriteria(); closeDialog()">
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
  import { computed, inject, nextTick } from 'vue'
  import { getBgColor, pathToCriteria } from '@/util'
  import InputIcon from 'primevue/inputicon'
  import { useRouter } from 'vue-router'
  import { useModelStore } from '@/stores/model'
  import { useToast } from 'primevue/usetoast'

  const props = defineProps<{
    criteriaType: CriteriaType
    criteriaKey: CriteriaKey
  }>()

  const { t } = useI18n()
  const router = useRouter()
  const toast = useToast()
  const { model, set } = useModelStore()

  const dialogRef = inject('dialogRef') as any
  const closeDialog = () => {
    dialogRef.value.close({
      navigateTo: `/${props.criteriaType}/${props.criteriaKey}`
    })
  }

  const createEmptyOrRedirectToExistingCriteria = async () => {
    if((props.criteriaType === 'rules' && model.rules?.enabledRules?.[props.criteriaKey] !== undefined) || model[props.criteriaType]?.[props.criteriaKey] !== undefined) {
      showRedirectInsteadOfNewlyCreated()
    }
    else {
      set(`${pathToCriteria(props.criteriaType)}.${props.criteriaKey}`, {}, true)
    }
  }

  const showRedirectInsteadOfNewlyCreated = () => {
    toast.add({
      severity: 'info',
      summary: t('general.redirect_to_existing_criteria.summary', {criteria: t(`${props.criteriaType}.types.${props.criteriaKey}.name`)}),
      detail: t('general.redirect_to_existing_criteria.detail', {criteria: t(`${props.criteriaType}.types.${props.criteriaKey}.name`)}),
      life: 5000
    })
  }

  const computedBgColor = computed(() => getBgColor(props.criteriaType, true))

</script>