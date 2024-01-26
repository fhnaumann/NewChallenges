<template>
  <Toast />
  <div class="flex flex-col h-screen w-screen">
      <div>
          <div class="text-center py-4">
              <h1 class="text-4xl">Challenge Settings Builder</h1>
          </div>
      </div>
      <div class="flex flex-1 justify-center h-full w-full space-x-10">
          <div class="shrink-0 border-8 border-slate-300 rounded-lg">
              <MyRules class="ml-2" />
          </div>
          <div class="shrink-0 border-8 border-slate-300 rounded-lg">
              <MyGoals class="ml-2" />
          </div>
      </div>
      <div class="flex justify-center">
          <Button @click="showSettingsFile = true"
              class="text-3xl w-full mx-10 my-5 border-4 rounded-md bg-green-500">Generate settings file</Button>
      </div>
      <DynamicDialog />
  </div>
  <Dialog class="w-3/5" v-model:visible="showSettingsFile">
      <div class="relative mx-auto mt-2 w-full">
          <div class="bg-gray-900 text-white p-4 rounded-md">
              <div class="flex justify-between items-center mb-2">
                  <span class="text-gray-400">Code:</span>
                  <Toast />
                  <Button @click="copyToClibboard"
                      class="code bg-gray-800 hover:bg-gray-700 text-gray-300 px-3 py-1 rounded-md">
                      Copy
                  </Button>

              </div>
              <div class="overflow-x-auto">
                  <pre id="code" class="text-gray-300">
                  <pre>{{ jsonifyConfigSettings() }}</pre>

              </pre>
              </div>
          </div>
      </div>
  </Dialog>
</template>

<script setup lang="ts">
//import MyTodo from './components/MyTodo.vue'
//import NoBlockBreaking from './components/NoBlockBreaking.vue';
import MyRules from './components/rules/MyRules.vue';
import DynamicDialog from 'primevue/dynamicdialog'
import Toast from 'primevue/toast';
import { useToast } from "primevue/usetoast"
import Dialog from 'primevue/dialog';
import MyGoals from './components/goals/MyGoals.vue';
import { ref, onBeforeUpdate } from "vue"
import { useConfigStore } from './main';

const store = useConfigStore()
const toast = useToast()

const showSettingsFile = ref(false)

onBeforeUpdate(() => {
  console.log("BEFORE UPDATE", store)
})

async function copyToClibboard() {
  try {
      await navigator.clipboard.writeText(jsonifyConfigSettings())
      showSuccess()
  } catch ($e) {
      alert('failed to copy')
  }
}

const showSuccess = () => {
  toast.add({ severity: "success", summary: "Copied to clipboard!", life: 1000 })
}

function jsonifyConfigSettings() {
  return JSON.stringify(store.model, null, 2)
}

</script>
