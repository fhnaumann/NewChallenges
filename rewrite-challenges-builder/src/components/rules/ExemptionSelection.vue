<template>
  <div class="flex items-center space-x-4 h-12">
    <p>{{ t('rules.exemption.label') }}</p>
    <MultiSelect
      class="grow-0 w-max-60"
      :model-value="fromCodeArray2DataRowArray(modelAccess.get(modelStore.model))"
      @update:model-value="
        (newSelected: DataRow[]) =>
          modelStore.set(
            modelAccess.where,
            newSelected,
            modelAccess.testSchematron,
            fromDataRowArray2CodeArray
          )
      "
      @selectall-change="
        (event: MultiSelectAllChangeEvent) =>
          modelStore.set(
            modelAccess.where,
            event.checked ? possibleExemptions : [],
            modelAccess.testSchematron,
            fromDataRowArray2CodeArray
          )
      "
      :options="possibleExemptions"
      :option-label="translateDataRow"
      :variant="'filled'"
      :placeholder="t('rules.exemption.placeholder')"
      display="chip"
      filter
      :virtual-scroller-options="{ itemSize: 44, delay: 100 }"
      :max-selected-labels=3
    >
      <template #option="slotProps">
        <DataRowVisual
          :img-path="(slotProps.option as DataRow).img_path"
          :translation-key="(slotProps.option as DataRow).mc_translation_key"
          :show-image="true"
        />
      </template>
    </MultiSelect>
  </div>
</template>

<script setup lang="ts">
  import { useI18n } from 'vue-i18n'
  import MultiSelect, { type MultiSelectAllChangeEvent } from 'primevue/multiselect'
  import { type DataRow, fromCodeArray2DataRowArray, fromDataRowArray2CodeArray } from '@/models/data_row'
  import DataRowVisual from '@/components/DataRowVisual.vue'
  import type { ModelAccess } from '@/main'
  import { useModelStore } from '@/stores/model'
  import { useTranslation } from '@/language'

  const { translate, translateDataRow } = useTranslation()

  const props = defineProps<{
    possibleExemptions: DataRow[]
    modelAccess: ModelAccess<string[]>
  }>()

  const modelStore = useModelStore()

  const { t } = useI18n()
</script>
