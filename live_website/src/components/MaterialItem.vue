<template>
  <div class="drop-shadow-lg"><img v-if="dataSource === 'material'" :class="imgClass" v-tooltip.top="translateDataRow(asDataRow)"
          :src="BASE_IMG_URL + '/rendered_images/' + asDataRow.img_name"
          @error="($event.target as HTMLInputElement).src = '/unknown.png'" :alt="code" />
    <img v-else-if="dataSource === 'entity_type'" :class="imgClass" v-tooltip.top="translateDataRow(asDataRow)"
         :src="BASE_IMG_URL + '/rendered_images/' + asDataRow.img_name" :alt="code" />
    <img v-else :class="imgClass" src="/unknown.png" alt="" /></div>
</template>

<script setup lang="ts">
import { BASE_IMG_URL } from '@/constants'
import { fromCode2DataRow } from '@/composables/data_row_loaded'
import { useTranslation } from '@/composables/language'

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