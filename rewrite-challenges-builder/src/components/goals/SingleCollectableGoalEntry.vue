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
      class="w-full md:w-80"
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
      :disabled="disabled"
      inputStyle="width:32px"
    />
    <Button
      v-if="currentlySelected"
      label="Delete"
      @click="$emit('deleteEntry', currentlySelected)"
      :disabled="disabled"
    />
  </div>
</template>

<script setup lang="ts">
  import constants from '@/constants'
  import type { DataRow } from '@/models/data_row'
  import { computed, ref } from 'vue'
  import type { CollectableDataConfig } from '@/models/goals'
  import { useTranslation } from '@/language'
  import CollectableRow from '@/components/DataRowVisual.vue'

  const props = defineProps<{
    currentlySelected: DataRow
    currentlySelectedAmount: number | undefined
    possibleData: DataRow[]
    collectableTextPrefix: string
    collectableAmountPrefix: string
    showImage: boolean
    disabled: boolean
  }>()

  const compCurrentlySelected = computed({
    get: () => props.currentlySelected,
    set: (value: DataRow) => emit('updateCurrentlySelected', value),
  })

  const amount = ref(props.currentlySelectedAmount)

  const { translate, translateDataRow } = useTranslation()

  function createModelPart(): CollectableDataConfig {
    return {
      amountNeeded: amount.value,
      currentAmount: 0,
    }
  }

  const emit = defineEmits<{
    updateCurrentlySelected: [value: DataRow]
    deleteEntry: [dataRowToDelete: DataRow]
  }>()
</script>
