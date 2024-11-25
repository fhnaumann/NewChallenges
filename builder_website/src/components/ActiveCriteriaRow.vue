<template>
  <div class="flex items-center justify-between px-2 space-x-4 bg-primary-300 hover:bg-primary-400 rounded-lg w-full">
    <p class="truncate">{{ t(`${criteriaType}.types.${criteriaCode}.name`) }}</p>
    <div class="flex space-x-2">
      <router-link :to="`/${criteriaType}/${criteriaCode}`">
        <Button
          class="my-1"
          icon="pi pi-pencil"
          severity="success"
        />
      </router-link>
      <Button
        class="my-1"
        icon="pi pi-trash"
        severity="danger"
        @click="emit('deleteCriteria'); deleteCriteria(criteriaType, criteriaCode)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
  import { useI18n } from 'vue-i18n'
  import type { CriteriaKey, CriteriaType } from '@fhnaumann/criteria-interfaces'
  import { useRoute } from 'vue-router'
  import { computed, ref, watch } from 'vue'
  import Button from 'primevue/button'
  import { useModelStore } from '@/stores/model'
  import { deleteCriteria, getBgColor } from '@/util'

  const props = defineProps<{
    criteriaType: CriteriaType
    criteriaCode: CriteriaKey
  }>()

  const emit = defineEmits<{
    deleteCriteria: []
  }>()

  const { t } = useI18n()
  const route = useRoute()
</script>
