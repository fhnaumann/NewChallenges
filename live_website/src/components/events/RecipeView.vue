<template>
  <i18n-t keypath="recipes.vuew" tag="div" class="flex items-center text-2xl">
    <template #target>
      <MaterialItem :code="craftinTypeDataRow.result" data-source="material" img-class="w-8" />
    </template>
    <template #source v-if="craftinTypeDataRow.source">
      <MaterialItem :code="craftinTypeDataRow.source!" data-source="material" img-class="w-8" />
    </template>
    <template #recipeType>
      <MaterialItem :code="craftingType2DataRow(craftinTypeDataRow.recipeType).code" data-source="material" img-class="w-8" />
    </template>
  </i18n-t>
</template>

<script setup lang="ts">
  import { useTranslation } from '@/composables/language'
  import MaterialItem from '@/components/MaterialItem.vue'
  import type { CraftingTypeDataRow } from '@criteria-interfaces/data_row'

  const props = defineProps<{
    craftinTypeDataRow: CraftingTypeDataRow
  }>()

  const { translate, craftingType2DataRow } = useTranslation()

</script>