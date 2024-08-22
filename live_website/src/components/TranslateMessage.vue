<template>
  <i18n-t :keypath="translationKey" tag="div" class="flex items-center border-blue-500 border-2">
    <template #[computedPlayer]>
      <div class="border-red-500 border-2 mx-2">
        <PlayerHead :skin-texture-u-r-l="getProp('player')!.props.skinTextureURL"
                    :player-name="getProp('player')!.props.playerName" :size="getProp('player')!.props.size" />
      </div>
    </template>
    <template v-if="hasProp('material')" #material>
      <div class="border-red-500 border-2 mx-2">
        <MaterialItem />
      </div>
    </template>
  </i18n-t>
</template>

<script setup lang="ts">

import PlayerHead from '@/components/PlayerHead.vue'
import MaterialItem from '@/components/MaterialItem.vue'
import { computed } from 'vue'

const computedPlayer = computed(() => {
  return getProp('player')!.key
})

const props = defineProps<{
  translationKey: string,
  renderProps: RenderProps[]
}>()

console.log(props)


function hasProp<T extends RenderType>(propType: T): boolean {
  return getProp(propType) !== undefined
}

function getProp<T extends RenderType>(propType: T): Extract<RenderProps, { type: T }> | undefined {
  console.log(props.renderProps)
  const found = props.renderProps?.find(value => value.type === propType)
  return found as Extract<RenderProps, { type: T }> | undefined
}

export type RenderType = 'player' | 'material'
export type RenderProps =
  | { key: string, type: 'player'; props: PlayerHeadProps }
  | { key: string, type: 'material'; props: MaterialProps };

export interface PlayerHeadProps {
  skinTextureURL: string
  playerName: string
  size: number
}

export interface MaterialProps {
  mcTranslationKey: string
}
</script>