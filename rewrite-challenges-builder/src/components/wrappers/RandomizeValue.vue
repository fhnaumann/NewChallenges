<template>
  <div class="flex items-center">
    <div class="flex items-center space-x-2">
      <slot name="punishmentDetailConfiguration">

      </slot>
    </div>
    <div class="flex items-center space-x-2 pl-6">
      <Checkbox :model-value="modelAccess.get(model)"
                @update:model-value="(value: boolean) => set(modelAccess.where, value, modelAccess.testSchematron)"
                binary :input-id="modelAccess.where" />
      <label :for="modelAccess.where">{{ t('punishments.randomize') }}</label></div>
  </div>
</template>

<script setup lang="ts">
  import Checkbox from 'primevue/checkbox'
  import { ref } from 'vue'
  import type { ModelAccess } from '@/main'
  import type { PunishableRuleConfig } from '@/models/rules'
  import type { PunishmentName } from '@/models/punishments'
  import { useModelStore } from '@/stores/model'
  import { useI18n } from 'vue-i18n'

  const props = defineProps<{
    modelAccess: ModelAccess<boolean>,
  }>()

  const { t } = useI18n()

  const { model, set } = useModelStore()

</script>