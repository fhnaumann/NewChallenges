<template>
  <div class="flex flex-col pt-5 items-center justify-center">
    <div class="flex items-center">
      <label class="mr-2" for="newTeam">{{ t('general.team.add') }}</label>
      <div class="flex space-x-2">
        <InputText v-model="modifyingTeam.teamName" @focusout="onFocusLeave" id="newTeam" />
        <label v-if="teamWithSameNameAlreadyExists">{{ t('general.team.already_exists') }}</label>
      </div>
    </div>
    <div class="flex flex-col">
      <div v-if="model.teams && model.teams.length > 0">
        <TeamEntry v-for="team in model.teams" :key="team.teamName" :team-name="team.teamName"
                   @deleteTeam="deleteTeam" />
      </div>
      <TeamEntry v-if="modifyingTeam.teamName?.length !== 0 && isValidTeamName(modifyingTeam.teamName)" :team-name="modifyingTeam.teamName" />
    </div>
  </div>

</template>

<script setup lang="ts">
  import Button from 'primevue/button'
  import InputText from 'primevue/inputtext'
  import { useModelStore } from '@/stores/model'
  import { useI18n } from 'vue-i18n'
  import { computed, onMounted, ref, toRaw } from 'vue'
  import type { TeamConfig } from '@fhnaumann/criteria-interfaces'
  import TeamEntry from '@/components/teams/TeamEntry.vue'

  const { t } = useI18n()
  const { set, model } = useModelStore()

  let modifyingTeam = ref<TeamConfig>({ teamName: '', goals: {} })
  if (model.teams === undefined) {
    model.teams = []
  }

  const teamWithSameNameAlreadyExists = computed(() => {
    return model.teams!.map(value => value.teamName.trim()).includes(modifyingTeam.value.teamName.trim())
  })

  function onFocusLeave() {
    if(isValidTeamName(modifyingTeam.value.teamName)) {
      model.teams!.push(structuredClone(toRaw(modifyingTeam.value)))
      modifyingTeam.value.teamName = ''
    }

  }

  function deleteTeam(teamName: string) {
    model.teams!.splice(model.teams!.findIndex(value => value.teamName === teamName), 1)
  }

  function isValidTeamName(attemptedTeamName: string) {
    return !teamWithSameNameAlreadyExists.value && attemptedTeamName.trim().length !== 0 && !attemptedTeamName.includes(" ")
  }
</script>