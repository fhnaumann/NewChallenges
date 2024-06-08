<template>
  <BasePunishment punishment-type="healthPunishment" :model-access="modelAccess">
    <template #configuration>
      <RandomizeValue
        :model-access="randomizeHeartsModelAccess">
        <template #punishmentDetailConfiguration="detailConfigProps">
          <label :for="modelAccess.where">{{ t(`punishments.types.healthPunishment.settings.hearts_lost.name`)
            }}</label>
          <InputNumber :model-value="heartsLostOrDefault()" @update:model-value="updateHeartsLost"
                       :input-id="modelAccess.where"
                       :min="config.heartsLost.minimum" :max="config.heartsLost.maximum"
                       :disabled="detailConfigProps.randomized" />

        </template>
      </RandomizeValue>
    </template>
  </BasePunishment>
</template>

<script setup lang="ts">

  import BasePunishment from '@/components/rules/punishments/BasePunishment.vue'
  import type { ModelAccess } from '@/main'
  import type { HealthPunishmentConfig, PunishmentName } from '@/models/punishments'
  import type { PunishableRuleConfig } from '@/models/rules'
  import { useModelStore } from '@/stores/model'
  import InputNumber from 'primevue/inputnumber'
  import Checkbox from 'primevue/checkbox'
  import RandomizeValue from '@/components/wrappers/RandomizeValue.vue'
  import { useJSONSchemaConfig } from '@/stores/default_model'
  import { useI18n } from 'vue-i18n'

  const props = defineProps<{
    modelAccess: ModelAccess<HealthPunishmentConfig>
  }>()

  const randomizeHeartsModelAccess = {
    get: ignored => heartsLostRandomizeOrDefault(),
    where: `${props.modelAccess.where}.randomizeHeartsLost`,
    testSchematron: true,
  } as ModelAccess<boolean>

  const { t } = useI18n()
  const { model, set } = useModelStore()
  const jsonSchemaConfig = useJSONSchemaConfig()
  const config = jsonSchemaConfig.HealthPunishmentConfig.properties

  function heartsLostOrDefault(): number {
    return props.modelAccess.get(model)?.heartsLost !== undefined
      ? props.modelAccess.get(model)?.heartsLost!
      : config.heartsLost.default
  }

  function heartsLostRandomizeOrDefault(): boolean {
    return props.modelAccess.get(model)?.randomizeHeartsLost !== undefined
      ? props.modelAccess.get(model)?.randomizeHeartsLost!
      : config.randomizeHeartsLost.default
  }

  function updateHeartsLost(value: number) {
    set(`${props.modelAccess.where}.heartsLost`, value, false)
  }
</script>