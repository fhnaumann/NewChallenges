<template>
  <Toast />
  <div class="flex justify-center w-full h-full">
    <div class="w-full" v-if="validJSON">
      <div class="relative mx-auto mt-2 w-full">
        <div class="text-black p-4 rounded-md">
          <div class="flex justify-between items-center mb-2">
            <span class="text-gray-400">Code:</span>
            <div class="flex space-x-2">
              <Button @click="copyCodeToClipboard" label="Copy" />
              <Button @click="downloadAsFile" label="Download" />
            </div>
          </div>
          <div class="overflow-x-auto">
        <pre id="code" class="text-black bg-gray-200 rounded-lg w-full pl-2">
        {{ jsonifyConfigSettings() }}
        </pre>
          </div>
        </div>
      </div>
    </div>
    <div class="w-full overflow-y-auto scrollbar max-h-[40rem]" v-if="!validJSON">
      <div>
        <div class="flex flex-col space-y-4">
          <p class="text-3xl font-bold text-red-600">Uh oh! Looks like something went wrong!</p>
          <p>The builder website (somehow) generated invalid code. Please report this bug on
            <a href="https://github.com/fhnaumann/NewChallenges/issues/new" target="_tab" rel="noopener noreferrer"
               class="text-blue-600 underline hover:text-blue-800 hover:underline">GitHub</a>
            or <a href="https://discord.gg/EST6jR7Zdr" target="_tab" rel="noopener noreferrer"
                  class="text-blue-600 underline hover:text-blue-800 hover:underline">Discord</a>
            and provide the code below along with the steps you took.
          </p>
        </div>
        <div class="flex flex-row space-x-4">
          <div class="relative mx-auto mt-2 w-[32rem]">
            <div class="bg-surface-0 text-black p-4 rounded-md">
              <div class="flex justify-between items-center mb-2">
                <span class="text-gray-400">Code:</span>
                <Button @click="copyCodeToClipboard" label="Copy" />

              </div>
              <div class="overflow-x-auto">
                <pre id="code" class="text-black">
                {{ jsonifyConfigSettings() }}

            </pre>
              </div>
            </div>
          </div>
          <div class="relative mx-auto mt-2 w-[32em]">
            <div class="bg-surface-0 text-black p-4 rounded-md">
              <div class="flex justify-between items-center mb-2">
                <span class="text-gray-400">Error:</span>
                <Button @click="copyErrorToClipboard" label="Copy" />

              </div>
              <div class="overflow-x-auto">
                <pre id="error" class="text-black">
                {{ errorMessages }}

            </pre>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

</template>
<script setup lang="ts">
  import Button from 'primevue/button'
  import Toast from 'primevue/toast'
  import challengesSchema from '@/assets/challenges_schema.json'
  import { useToast } from 'primevue/usetoast'
  import { useModelStore } from '@/stores/model'
  import Ajv from 'ajv'
  import { inject, onMounted, ref } from 'vue'
  import type { Model } from 'criteria-interfaces'

  const toast = useToast()

  const validJSON = ref(false)
  const errorMessages = ref<string>('')
  const ajv = new Ajv({ allErrors: true })

  const model = ref<Model | null>(null)

  const dialogRef = inject('dialogRef');

  onMounted(() => {
    const params = (dialogRef as any).value.data
    model.value = params.copyModel
    validateConsistency(model.value!)
  })

  function validateConsistency(model: Model) {
    const validate = ajv.compile(challengesSchema)
    const isValid = validate(model)
    validJSON.value = isValid as boolean
    if (!isValid) {
      errorMessages.value = JSON.stringify(validate.errors, null, 2)
    }
  }

  async function copyCodeToClipboard() {
    try {
      await navigator.clipboard.writeText(jsonifyConfigSettings())
      showSuccess()
    } catch ($e) {
      alert('failed to copy')
    }
  }

  async function copyErrorToClipboard() {
    try {
      await navigator.clipboard.writeText(JSON.stringify(errorMessages.value, null, 2))
      showSuccess()
    } catch ($e) {
      alert('failed to copy')
    }
  }

  const showSuccess = () => {
    toast.add({ severity: 'success', summary: 'Copied to clipboard!', life: 1000 })
  }

  function jsonifyConfigSettings() {
    return JSON.stringify(model.value, null, 2)
  }

  function downloadAsFile() {
    const blob = new Blob([jsonifyConfigSettings()], { type: "application/json" })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = model.value!.metadata.name + ".json"
    document.body.append(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
  }

</script>