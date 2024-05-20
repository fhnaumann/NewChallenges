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
      class="w-full md:w-80"
      @update:modelValue="
        $emit('transferDataFromPlaceHolderToNewInstance', selectedData!)
        selectedData = undefined
      "
    >
      <template #value="slotProps">
        <div
          v-if="slotProps.value"
          class="flex justify-start items-center space-x-2"
        >
          <CollectableRow
            :translation-key="slotProps.value.translation_key"
            :img-path="slotProps.value.img_name"
            :show-image="showImage"
          />
        </div>
      </template>
      <template #option="slotProps">
        <div class="flex justify-start items-center space-x-2">
          <CollectableRow
            :translation-key="slotProps.option.translation_key"
            :img-path="slotProps.option.img_name"
            :show-image="showImage"
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
      :disabled="!selectedData"
      inputStyle="width:32px"
    />
  </div>
</template>

<script setup lang="ts">
  import type { DataRow } from '@/models/data_row'
  import { ref } from 'vue'
  import { useTranslation } from '@/language'
  import CollectableRow from '@/components/DataRowVisual.vue'

  const props = defineProps<{
    possibleData: DataRow[]
    collectableTextPrefix: string
    collectableAmountPrefix: string
    placeholderText: string
    showImage: boolean
    disabled: boolean
  }>()

  const { translate, translateDataRow } = useTranslation()

  const selectedData = ref<DataRow>()
  const amount = ref(0)

  const emits = defineEmits<{
    transferDataFromPlaceHolderToNewInstance: [newSelectedData: DataRow]
  }>()
</script>
