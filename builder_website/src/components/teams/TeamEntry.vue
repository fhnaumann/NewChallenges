<template>
  <div class="flex items-center space-x-2">
    <label>{{ teamName }}</label>
    <Button class="my-1" icon="pi pi-trash" severity="danger" @click="emit('deleteTeam', teamName)"/>
  </div>
</template>

<script setup lang="ts">

  import Button from 'primevue/button'

  const props = defineProps<{
    teamName: string
  }>()

  const emit = defineEmits<{
    deleteTeam: [teamName: string]
  }>()
</script>