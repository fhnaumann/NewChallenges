<template>
  <div class="flex flex-col pt-5 space-y-8 items-center justify-center">
    <FloatLabel>
      <InputText id="challenge-name" v-model="model.metadata.name" />
      <label for="challenge-name">{{ t("general.metadata.name.name") }}</label>
    </FloatLabel>
    <FloatLabel>
      <InputText id="challenge-created-by" v-model="model.metadata.createdBy" />
      <label for="challenge-created-by">{{ t("general.metadata.createdBy.name") }}</label>
    </FloatLabel>
    <FloatLabel >
      <InputText id="challenge-whenCreated" v-model="model.metadata.whenCreated" disabled />
      <label for="challenge-whenCreated">{{ t("general.metadata.whenCreated.name") }}</label>
    </FloatLabel>
    <FloatLabel>
      <InputText id="challenge-lastModified" v-model="model.metadata.lastModified" disabled />
      <label for="challenge-lastModified">{{ t("general.metadata.lastModified.name") }}</label>
    </FloatLabel>
    <FloatLabel>
      <InputText id="challenge-builderVersion" v-model="model.metadata.builderVersion" disabled />
      <label for="challenge-builderVersion">{{ t("general.metadata.builderVersion.name") }}</label>
    </FloatLabel>
    <FloatLabel>
      <InputText id="challenge-builderMCVersion" v-model="model.metadata.builderMCVersion" disabled />
      <label for="challenge-builderMCVersion">{{ t("general.metadata.builderMCVersion.name") }}</label>
    </FloatLabel>
    <FloatLabel>
      <InputText id="challenge-pluginVersion" v-model="model.metadata.pluginVersion" disabled />
      <label for="challenge-pluginVersion">{{ t("general.metadata.pluginVersion.name") }}</label>
    </FloatLabel>
    <FloatLabel>
      <InputText id="challenge-pluginMCVersion" v-model="model.metadata.pluginMCVersion" disabled />
      <label for="challenge-pluginMCVersion">{{ t("general.metadata.pluginMCVersion.name") }}</label>
    </FloatLabel>
  </div>
</template>

<script setup lang="ts">
import FloatLabel from 'primevue/floatlabel'
import InputText from 'primevue/inputtext'
import { useModelStore } from '@/stores/model'
import { useI18n } from 'vue-i18n'

const { model } = useModelStore()
const { t } = useI18n()

</script>