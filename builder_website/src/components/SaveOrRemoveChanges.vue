<template>
  <div class="flex flex-col pr-2 py-2 space-y-4">
    <Button class="flex-none" severity="danger" :label="t('general.delete')" @click="emit('deleteCriteria'); router.push('/')" />
    <Button class="grow h-32" severity="success" :label="t('general.save_changes')" @click="emit('saveChanges'); router.push('/')" />
  </div>
</template>

<script setup lang="ts">

import { useI18n } from 'vue-i18n'
import Button from 'primevue/button'
import type { CriteriaKey } from '@/models/model'
import { useRouter } from 'vue-router'

const { t } = useI18n()

const emit = defineEmits<{
  deleteCriteria: [],
  saveChanges: []
}>()

const router = useRouter()

</script>