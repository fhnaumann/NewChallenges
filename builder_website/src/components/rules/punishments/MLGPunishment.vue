<template>
  <BasePunishment punishment-type="mlgPunishment" :model-access="modelAccess">
    <template #configuration>
      <div class="flex items-center space-x-2" >
        <label :for="`${modelAccess.where}.height`">
          {{ t(`punishments.types.mlgPunishment.settings.height.name`) }}
        </label>
        <InputNumber :model-value="heightOrDefault()" @update:model-value="updateHeight"
                     :input-id="`${modelAccess.where}.height`" :min="config.height.minimum"
                     :max="config.height.maximum" />
      </div>
    </template>
  </BasePunishment>
</template>

<script setup lang="ts">
  import BasePunishment from '@/components/rules/punishments/BasePunishment.vue'
  import type { ModelAccess } from '@/main'
  import InputNumber from 'primevue/inputnumber'
  import { useI18n } from 'vue-i18n'
  import { useModelStore } from '@/stores/model'
  import { useJSONSchemaConfig } from '@/stores/default_model'
  import type { MLGPunishmentConfig } from 'criteria-interfaces/src'

  const props = defineProps<{
    modelAccess: ModelAccess<MLGPunishmentConfig>
  }>()

  const { t } = useI18n()
  const { model, set } = useModelStore()
  const jsonSchemaConfig = useJSONSchemaConfig()
  const config = jsonSchemaConfig.MLGPunishmentConfig.properties

  function heightOrDefault(): number {
    return props.modelAccess.get(model)?.height !== undefined
      ? props.modelAccess.get(model)?.height!
      : config.height.default
  }

  function updateHeight(value: number) {
    set(`${props.modelAccess.where}.height`, value, false)
  }

</script>