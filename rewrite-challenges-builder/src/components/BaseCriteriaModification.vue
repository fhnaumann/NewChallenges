<template>
  <div class="pt-4 h-svh bg-rules-100">
    <div class="flex flex-col items-center justify-center space-y-2 w-full">
      <p :class="`text-3xl font-bold ${computedTextColor}`">{{ t('general.modification.title', { criteria: t(`${criteriaType}.types.${criteriaKey}.name`) }) }}</p>

      <div class="bg-gray-200 px-2 w-full text-center text-2xl">
        <i18n-t keypath="general.modification.wiki_banner" tag="p">
          <template #wiki>
            <span class="font-bold">wiki</span>
          </template>
          <template #criteria>
            <a class="underline text-blue-600 hover:text-blue-800 visited:text-purple-600"
               :href="`${BASE_WIKI_URL}/${relativeURLToWiki}`" target="_blank">{{ t(`${criteriaType}.types.${criteriaKey}.name`) }}</a>
          </template>
        </i18n-t>
      </div>


      <!--p class="bg-gray-200 px-2 w-full text-center">{{ t('general.modification.wiki_banner', { criteria: t(criteriaNameI18NPath) }) }}</p-->
    </div>
    <div class="flex flex-row w-screen justify-between space-x-4 h-96 border-indigo-700 border-4">
      <div :class="`h-screen pl-2 ${computedCssClass}`">
        <slot name="configuration">

        </slot>
      </div>
      <SaveOrRemoveChanges class="w-40" @deleteCriteria="emit('deleteCriteria'); deleteCriteria(criteriaType, criteriaKey)"
                           @save-changes="() => emit('saveChanges')" />
    </div>
  </div>

</template>

<script setup lang="ts">

import { useI18n } from 'vue-i18n'
import type { CriteriaKey, CriteriaType } from '@/models/model'
import SaveOrRemoveChanges from '@/components/SaveOrRemoveChanges.vue'
import i18n from '@/i18n'
import { BASE_WIKI_URL } from '@/constants'
import { computed } from 'vue'
import { deleteCriteria, getTextColor } from '@/util'

const props = defineProps<{
  criteriaType: CriteriaType,
  criteriaKey: CriteriaKey
  relativeURLToWiki: string
}>()

const emit = defineEmits<{
  deleteCriteria: [],
  saveChanges: []
}>()

const { t } = useI18n()

const computedTextColor = computed(() => getTextColor(props.criteriaType))

const computedCssClass = computed(() => {
  switch(props.criteriaType) {
    case 'rules': return 'customized-rule'
    case 'goals': return 'customized-goal'
    case 'settings': return 'customized-setting'
    default: throw Error()
  }
})
</script>