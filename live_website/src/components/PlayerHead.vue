<template>
  <canvas ref="canvasRef" :width="size" :height="size" style="display: none"></canvas>
  <img v-if="headUrl" v-tooltip.top="playerName" :src="headUrl" alt="Minecraft Head" />
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useMinecraftHead } from '@/composables/head_composable'

const {
  playerUUID = 'faf9d2ac-82c1-3a56-917a-327f0a09893b',
  playerName = '?',
  skinTextureURL = "http://textures.minecraft.net/texture/5163dafac1d91a8c91db576caac784336791a6e18d8f7f62778fc47bf146b6",
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
