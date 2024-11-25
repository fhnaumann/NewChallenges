<template>
  <div class="flex flex-row items-center">
    <Checkbox :model-value="fixedOrderOrDefault()" @update:model-value="updateFixedOrder" binary
              input-id="fixedOrder" />
    <label for="fixedOrder" class="ml-2">{{ t('goals.fixedOrder.name') }}</label>
  </div>
</template>

<script setup lang="ts">
  import type { ModelAccess } from '@/main'
  import type { Orderable, Timeable } from '@fhnaumann/criteria-interfaces'
  import { useModelStore } from '@/stores/model'
  import { useJSONSchemaConfig } from '@/stores/default_model'
  import { useI18n } from 'vue-i18n'
  import Checkbox from 'primevue/checkbox'

  const props = defineProps<{
    modelAccess: ModelAccess<Orderable>
  }>()

  const { model, set } = useModelStore()
  const jsonSchemaConfig = useJSONSchemaConfig()
  const { t } = useI18n()

  // take default values from any goal config instead of the specific one (for now)
  const config = jsonSchemaConfig.BlockBreakGoalConfig.properties

  function updateFixedOrder(value: boolean) {
    set(`${props.modelAccess.where}.fixedOrder`, value, false)
  }

  function fixedOrderOrDefault() {
    return props.modelAccess.get(model)?.fixedOrder !== undefined
      ?
      props.modelAccess.get(model)?.fixedOrder!
      : config.fixedOrder.default
  }

</script>