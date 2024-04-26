<template>
    <div v-html="pageContent"></div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute } from 'vue-router';

const route = useRoute()

let pageContent = null

onMounted(async () => {
    const page = route.params.page || 'index'
    const response = await fetch(`/docs/${page}.html`)
    pageContent = await response.text()
})
</script>