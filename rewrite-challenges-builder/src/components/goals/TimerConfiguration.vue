<template>
  <div class="flex flex-col space-y-2">
    <div class="flex flex-row items-center">
      <Checkbox v-model="enabled" :binary="true" :input-id="modelAccess.where" />
      <label :for="modelAccess.where" class="ml-2">{{ t('goals.timer.enable') }}</label>
    </div>
    <div v-if="enabled" class="flex flex-col space-y-2">
      <div class="flex flex-row items-center space-x-2">
        <p>{{ t("goals.timer.orderPrefix") }}</p>
        <InputNumber :model-value="orderOrDefault()" @update:model-value="updateOrder" :min=1 :max=100 />
      </div>
      <div class="flex flex-row items-center space-x-2">
        <p>{{ t('goals.timer.minTimerPrefix') }}</p>
        <InputNumber :model-value="sec2Minutes(minTimeSecOrDefault())" @update:model-value="updateMin"
                     :min="sec2Minutes(config.minTimeSeconds.minimum)" :max="sec2Minutes(maxTimeSecOrDefault())" />
        <p>{{ t('goals.timer.maxTimerPrefix') }}</p>
        <InputNumber :model-value="sec2Minutes(maxTimeSecOrDefault())" @update:model-value="updateMax"
                     :min="sec2Minutes(minTimeSecOrDefault())" :max="sec2Minutes(config.maxTimeSeconds.maximum)" />
      </div>
      <Slider class="w-96" :model-value="[sec2Minutes(minTimeSecOrDefault()), sec2Minutes(maxTimeSecOrDefault())]"
              @update:model-value="updateModel" range
              :min="sec2Minutes(config.minTimeSeconds.minimum)" :max="sec2Minutes(config.maxTimeSeconds.maximum)" />
    </div>
  </div>
</template>

<script setup lang="ts">


  import { useJSONSchemaConfig } from '@/stores/default_model'
  import type { ModelAccess } from '@/main'
  import type { Timeable } from '@/models/goals'
  import { useModelStore } from '@/stores/model'
  import Slider from 'primevue/slider'
  import InputNumber from 'primevue/inputnumber'
  import { ref, watch } from 'vue'
  import { useI18n } from 'vue-i18n'
  import Checkbox from 'primevue/checkbox'

  const props = defineProps<{
    modelAccess: ModelAccess<Timeable>
  }>()

  const { model, set } = useModelStore()
  const jsonSchemaConfig = useJSONSchemaConfig()
  const { t } = useI18n()

  const enabled = ref<boolean>(false)

  watch(() => enabled.value, value => {
    console.log('new enabled', value)
    if (!value) {
      set(`${props.modelAccess.where}.minTimeSeconds`, undefined, false)
      set(`${props.modelAccess.where}.maxTimeSeconds`, undefined, false)
    }
  })

  function updateOrder(value: number) {
    set(`${props.modelAccess.where}.order`, value, false)
  }

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

  function updateModel(value: number[]) {
    updateMin(value[0])
    updateMax(value[1])
  }

  // take default values from any goal config instead of the specific one (for now)
  const config = jsonSchemaConfig.BlockBreakGoalConfig.properties

  function minTimeSecOrDefault(): number {
    return props.modelAccess.get(model)?.minTimeSeconds !== undefined
      ?
      props.modelAccess.get(model)?.minTimeSeconds!
      : config.minTimeSeconds.default
  }

  function maxTimeSecOrDefault(): number {
    return props.modelAccess.get(model)?.maxTimeSeconds !== undefined
      ?
      props.modelAccess.get(model)?.maxTimeSeconds!
      : config.maxTimeSeconds.default
  }

  function orderOrDefault(): number {
    return props.modelAccess.get(model)?.order !== undefined
      ?
      props.modelAccess.get(model)?.order!
      : 1
  }

  function sec2Minutes(seconds: number): number {
    return seconds / 60
  }

  function minutes2Sec(minutes: number): number {
    return minutes * 60
  }
</script>
