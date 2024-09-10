<template>
  <canvas ref="canvasRef" :width="size" :height="size" style="display: none"></canvas>
  <img v-if="headUrl" v-tooltip.top="playerName" :src="headUrl" alt="Minecraft Head" />
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useMinecraftHead } from '@/composables/head_composable'

const {
  playerUUID = '31f477eb-1a7b-eee6-31c2-ca64d06f8f68',
  playerName = '?',
  skinTextureURL = 'http://textures.minecraft.net/texture/31f477eb1a7beee631c2ca64d06f8f68',
  size
} = defineProps<{
  playerUUID?: string
  skinTextureURL?: string
  playerName?: string
  size: number
}>()

//const { headUrl, canvas } = useMinecraftHead("http://textures.minecraft.net/texture/7fd9ba42a7c81eeea22f1524271ae85a8e045ce0af5a6ae16c6406ae917e68b5")
const { headUrl, canvas } = useMinecraftHead(skinTextureURL, size)

const canvasRef = ref(null)

onMounted(() => {
  canvas.value = canvasRef.value
})
</script>
