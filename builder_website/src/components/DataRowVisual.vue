<template>
  <div class="flex justify-center space-x-2">
    <img
      v-if="showImage"
      class="w-6"
      :src="BASE_IMG_URL + '/rendered_images/' + imgPath"
      @error="($event.target as HTMLInputElement).src = '/unknown.png'"
    />
    <div>{{ translate(translationKey) }}</div>
  </div>
</template>

<script setup lang="ts">
  /**
   * DataRow - Represents a "code" together with a translation id to display the label in the
   * specified language as well as an optional image. If 'showImage' is set to true but no image can
   * be found, then the generic 'questionmark' image is used instead.
   *
   */

  import { BASE_IMG_URL } from '@/constants'
  import { useTranslation } from '@/language'

  const props = defineProps<{
    /**
     * The path/name of the image including the file ending (e.g: minecraft_stone.png).
     */
    imgPath: string
    translationKey: string
    showImage: boolean
  }>()

  const { translate, translateDataRow } = useTranslation()
</script>
