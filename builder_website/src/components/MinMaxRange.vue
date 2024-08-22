<template>
  <div class="flex flex-col space-y-2">
    <div class="flex flex-col space-y-2">
      <div class="flex flex-row items-center space-x-2">
        <p>{{ t('general.minMaxRange.minValue') }}</p>
        <InputNumber :input-id="`${modelAccess.where}.min`" :model-value="sec2Minutes(minTimeSecOrDefault())" @update:model-value="updateMin"
                     :min="sec2Minutes(absoluteMinMinValue)" :max="sec2Minutes(maxTimeSecOrDefault())" />
        <p>{{ t('general.minMaxRange.maxValue') }}</p>
        <InputNumber :input-id="`${modelAccess.where}.max`" :model-value="sec2Minutes(maxTimeSecOrDefault())" @update:model-value="updateMax"
                     :min="sec2Minutes(minTimeSecOrDefault())" :max="sec2Minutes(absoluteMaxMaxValue)" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import type { ModelAccess } from '@/main'
  import type { MinMaxRangeConfig } from 'criteria-interfaces'
  import { useModelStore } from '@/stores/model'
  import { useJSONSchemaConfig } from '@/stores/default_model'
  import { useI18n } from 'vue-i18n'
  import InputNumber from 'primevue/inputnumber'
  import Checkbox from 'primevue/checkbox'
  import Slider from 'primevue/slider'

  const props = defineProps<{
    modelAccess: ModelAccess<MinMaxRangeConfig>
    absoluteMinMinValue: number,
    defaultMinValue: number,
    defaultMaxValue: number,
    absoluteMaxMaxValue: number
  }>()

  const { model, set } = useModelStore()
  const { t } = useI18n()

  function updateMin(value: number) {
    value = minutes2Sec(value)
    if (value <= maxTimeSecOrDefault()) {
      set(`${props.modelAccess.where}.minTimeSeconds`, value, false)
    }
  }

  function updateMax(value: number) {
    value = minutes2Sec(value)
    if (value >= minTimeSecOrDefault()) {
      set(`${props.modelAccess.where}.maxTimeSeconds`, value, false)
    }
  }

  function minTimeSecOrDefault(): number {
    return props.modelAccess.get(model)?.minTimeSeconds !== undefined
      ?
      props.modelAccess.get(model)?.minTimeSeconds!
      : props.defaultMinValue
  }

  function maxTimeSecOrDefault(): number {
    return props.modelAccess.get(model)?.maxTimeSeconds !== undefined
      ?
      props.modelAccess.get(model)?.maxTimeSeconds!
      : props.defaultMaxValue
  }

  function sec2Minutes(seconds: number): number {
    return seconds / 60
  }

  function minutes2Sec(minutes: number): number {
    return minutes * 60
  }
</script>