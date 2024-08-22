<template>
  <BasePunishment punishment-type="randomEffectPunishment" :model-access="modelAccess">
    <template #configuration>
      <RandomizeValue :model-access="computedRandomizeEffectAmountModelAccess">
        <template #punishmentDetailConfiguration="detailConfigProps">
          <label :for="modelAccess.where">{{ t(`punishments.types.randomEffectPunishment.settings.effects_at_once.name`)
            }}</label>
          <InputNumber :model-value="effectAmountOrDefault(model)" @update:model-value="updateEffectAmount"
                       :input-id="modelAccess.where" :min="config.effectsAtOnce.minimum"
                       :max="config.effectsAtOnce.maximum" :disabled="detailConfigProps.randomized" />
        </template>
      </RandomizeValue>
    </template>
  </BasePunishment>
</template>

<script setup lang="ts">

  import BasePunishment from '@/components/rules/punishments/BasePunishment.vue'
  import type { ModelAccess } from '@/main'
  import type { RandomEffectPunishmentConfig } from 'criteria-interfaces'
  import { useJSONSchemaConfig } from '@/stores/default_model'
  import type { Model } from 'criteria-interfaces'
  import { useModelStore } from '@/stores/model'
  import RandomizeValue from '@/components/wrappers/RandomizeValue.vue'
  import { useI18n } from 'vue-i18n'
  import InputNumber from 'primevue/inputnumber'
  import { computed } from 'vue'

  const props = defineProps<{
    modelAccess: ModelAccess<RandomEffectPunishmentConfig>
  }>()

  const computedRandomizeEffectAmountModelAccess = computed(() => {
    return {
      get: model => effectAmountRandomizeOrDefault(model),
      where: `${props.modelAccess.where}.randomizeEffectsAtOnce`,
      testSchematron: true,
    } as ModelAccess<boolean>
  })

  const { t } = useI18n()
  const { model, set } = useModelStore()
  const jsonSchemaConfig = useJSONSchemaConfig()
  const config = jsonSchemaConfig.RandomEffectPunishmentConfig.properties

  function effectAmountOrDefault(model: Model): number {
    return props.modelAccess.get(model)?.effectsAtOnce !== undefined
      ? props.modelAccess.get(model)?.effectsAtOnce!
      : config.effectsAtOnce.default
  }

  function effectAmountRandomizeOrDefault(model: Model): boolean {
    return props.modelAccess.get(model)?.randomizeEffectsAtOnce !== undefined
      ? props.modelAccess.get(model)?.randomizeEffectsAtOnce!
      : config.randomizeEffectsAtOnce.default
  }

  function updateEffectAmount(value: number) {
    set(`${props.modelAccess.where}.effectsAtOnce`, value, false)
  }

</script>