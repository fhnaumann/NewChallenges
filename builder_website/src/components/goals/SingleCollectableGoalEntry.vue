<template>
  <div class="flex flex-row items-center space-x-4 h-12">
    <p>{{ collectableTextPrefix }}</p>
    <Dropdown
      v-model="compCurrentlySelected"
      :options="possibleData"
      :option-label="translateDataRow"
      :disabled="disabled"
      display="chip"
      :virtual-scroller-options="{ itemSize: 44 }"
      filter
      :id="compCurrentlySelected.code"
      :class="dropdownClass !== undefined ? dropdownClass : 'w-80'"
    >
      <template #value="slotProps">
        <div
          v-if="slotProps.value"
          class="flex justify-start items-center space-x-2"
        >
          <DataRowVisual
            :translation-key="slotProps.value.translation_key"
            :img-path="slotProps.value.img_name"
            :show-image="showImage"
            :raw-text="rawText?.(slotProps.value)"
          />
        </div>
      </template>
      <template #option="slotProps">
        <div class="flex justify-start items-center space-x-2">
          <DataRowVisual
            :translation-key="slotProps.option.translation_key"
            :img-path="slotProps.option.img_name"
            :show-image="showImage"
            :raw-text="rawText?.(slotProps.option)"
          />
        </div>
      </template>
    </Dropdown>
    <p>{{ collectableAmountPrefix }}</p>
    <InputNumber
      :model-value="valueOrDefault()"
      @update:model-value="(value: number) => set(`${modelAccess.where}.collectableData.amountNeeded`, value, false)"
      showButtons
      :min="1"
      :max="100"
      :step="1"
      :button-layout="'stacked'"
      :disabled="disabled"
    />
    <Button
      v-if="currentlySelected"
      label="Delete"
      :class="getBgColor('goals', true)"
      @click="$emit('deleteEntry', currentlySelected)"
      :disabled="disabled"
    />
  </div>
</template>

<script setup lang="ts">
  import DataRowVisual from '@/components/DataRowVisual.vue'
  import Dropdown from 'primevue/dropdown'
  import InputNumber from 'primevue/inputnumber'
  import Button from 'primevue/button'
  import type { DataRow } from '@/models/data_row'
  import { computed, ref } from 'vue'
  import type { CollectableDataConfig, CollectableEntryConfig } from '@/models/goals'
  import { useTranslation } from '@/language'
  import { useModelStore } from '@/stores/model'
  import type { ModelAccess } from '@/main'
  import { useJSONSchemaConfig } from '@/stores/default_model'
  import { getBgColor } from '@/util'

  const props = defineProps<{
    modelAccess: ModelAccess<CollectableEntryConfig>
    currentlySelected: DataRow
    currentlySelectedAmount: number | undefined
    possibleData: DataRow[]
    collectableTextPrefix: string
    collectableAmountPrefix: string
    showImage: boolean
    rawText?: (dataRow: DataRow) => string
    dropdownClass?: string
    disabled: boolean
  }>()

  const emit = defineEmits<{
    updateCurrentlySelected: [value: DataRow]
    deleteEntry: [dataRowToDelete: DataRow]
  }>()

  const compCurrentlySelected = computed({
    get: () => props.currentlySelected,
    set: (value: DataRow) => emit('updateCurrentlySelected', value),
  })

  const { translate, translateDataRow } = useTranslation()
  const { model, set } = useModelStore()
  const jsonSchemaConfig = useJSONSchemaConfig()

  function valueOrDefault(): number {
    return props.modelAccess.get(model)?.collectableData?.amountNeeded !== undefined
      ?
      props.modelAccess.get(model)?.collectableData?.amountNeeded!
      :
      jsonSchemaConfig.CollectableDataConfig.properties.amountNeeded.default
  }

</script>
