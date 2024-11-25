<template>
  <div>
    <div class="flex items-center justify-end space-x-2">
      <Button class="!bg-card !border-2 !border-content-border hover:!border-primary !ease-in-out" v-if="!dark" icon="pi pi-sun" icon-class="text-color" @click="dark = true"/>
      <Button class="!bg-card !border-2 !border-content-border hover:!border-primary !ease-in-out" v-else icon="pi pi-moon" icon-class="text-color" @click="dark = false"/>
    </div>
    <slot />
  </div>
</template>

<script setup lang="ts">
import Button from 'primevue/button'
import { useThemeSwitcher } from '@/composables/theme_switcher'
import { watch } from 'vue'

const { dark } = useThemeSwitcher()
watch(dark, (newDark) => {
  console.log(newDark)
})


</script>