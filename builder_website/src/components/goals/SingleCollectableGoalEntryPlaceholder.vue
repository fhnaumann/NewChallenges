<template>
  <div class="flex flex-row items-center space-x-4 h-12">
    <p>{{ collectableTextPrefix }}</p>
    <Dropdown
      v-model="selectedData"
      :options="possibleData"
      :option-label="translateDataRow"
      :placeholder="placeholderText"
      :disabled="disabled"
      display="chip"
      :virtual-scroller-options="{ itemSize: 44 }"
      filter
      id="collectableSelection"
      :class="dropdownClass !== undefined ? dropdownClass : 'w-80'"
      @update:modelValue="
        $emit('transferDataFromPlaceHolderToNewInstance', selectedData!);
        selectedData = undefined
      "
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
      v-model="amount"
      showButtons
      :min="1"
      :max="100"
      :step="1"
      :disabled="true"
    />
  </div>
</template>

<script setup lang="ts">
  import type { DataRow } from '@/models/data_row'
  import { ref } from 'vue'
  import { useTranslation } from '@/language'
  import DataRowVisual from '@/components/DataRowVisual.vue'
  import Dropdown from 'primevue/dropdown'
  import InputNumber from 'primevue/inputnumber'

  const props = defineProps<{
    possibleData: DataRow[]
    collectableTextPrefix: string
    collectableAmountPrefix: string
    placeholderText: string
    showImage: boolean
    rawText?: (dataRow: DataRow) => string
    dropdownClass?: string
    disabled: boolean
  }>()

  const { translate, translateDataRow } = useTranslation()

  const selectedData = ref<DataRow>()
  const amount = ref(0)

  const emits = defineEmits<{
    transferDataFromPlaceHolderToNewInstance: [newSelectedData: DataRow]
  }>()
</script>
