<template>
  <img v-if="dataSource === 'material'" :class="imgClass" v-tooltip.top="translateDataRow(asDataRow)" :src="BASE_IMG_URL + '/rendered_images/' + asDataRow.img_name"
       @error="($event.target as HTMLInputElement).src = '/unknown.png'" :alt="code" />
</template>

<script setup lang="ts">
import { BASE_IMG_URL } from '@/constants'
import { fromCode2DataRow } from '@/composables/data_row_loaded'
import { useTranslation } from '@/language'

export type DataSource = 'material' | 'entity_type' | 'death_message' | 'crafting_recipe'

const props = defineProps<{
  code: string,
  dataSource: DataSource
  imgClass: string
}>()

const asDataRow = fromCode2DataRow(props.code)

const { translate, translateDataRow } = useTranslation()

console.log(props.code)

</script>