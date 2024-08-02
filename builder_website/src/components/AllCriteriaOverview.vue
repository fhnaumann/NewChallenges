<template>
  <div class="grid grid-cols-3 gap-x-2 gap-y-4 justify-center content-start h-screen w-screen">
    <div class="col-span-3 px-2 pt-2">
      <div class="flex flex-row space-x-2">
        <InputText class="w-full" type="text" :placeholder="t(`general.search.placeholder`)"
                   @click="showSearcher()" />
        <Button class="w-[10rem]" :label="t('general.delete_all_criteria')" @click="clearAllCriteria" severity="danger" />
      </div>
    </div>
    <CriteriaTypeOverview class="customized-rule" criteria-type="rules" />
    <CriteriaTypeOverview class="customized-goal" criteria-type="goals" />
    <CriteriaTypeOverview class="customized-setting" criteria-type="settings" />
    <div class="fixed inset-x-0 bottom-0">
      <div class="flex justify-end px-4 py-2 space-x-4">
        <Button :label="t('general.team.modify_teams')" @click="showChangeTeams()"/>
        <Button :label="t('general.change_metadata_button')" @click="showChangeMetadata()" />
        <Button :label="t('general.export.download_button')" @click="showCodeDisplay()" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import CriteriaTypeOverview from '@/components/CriteriaTypeOverview.vue'
  import SearchComponent from '@/components/search/SearchComponent.vue'
  import Dialog from 'primevue/dialog'
  import InputText from 'primevue/inputtext'
  import { useI18n } from 'vue-i18n'
  import { ref, toRaw } from 'vue'
  import { useDialog } from 'primevue/usedialog'
  import { root } from 'postcss'
  import { useRouter } from 'vue-router'
  import Button from 'primevue/button'
  import CodeDisplay from '@/components/CodeDisplay.vue'
  import { useModelStore } from '@/stores/model'
  import MetadataOverview from '@/components/MetadataOverview.vue'
  import type { Model } from '@/models/model'
  import TeamOverview from '@/components/teams/TeamOverview.vue'


  const { t } = useI18n()
  const router = useRouter()
  const dialog = useDialog()
  const { set, model } = useModelStore()

  function clearAllCriteria() {
    set('rules', {}, false)
    set('goals', {}, false)
    set('settings', {}, false)
  }

  function showSearcher() {
    dialog.open(SearchComponent, {
      props: {
        modal: true,
        showHeader: false,
        blockScroll: true,
        draggable: false,
        position: 'top',
        pt: {
          root: {
            class: 'border-0 pt-5 px-12 w-[64rem]'
          },
          content: {
            class: 'rounded-none'
          },
          mask: {
            class: 'backdrop-blur-[2px]'
          }
        }
      },
      data: {
        validOption: undefined
      },
      onClose(options) {
        if(options?.data) {
          router.push(options?.data.navigateTo)
        }
      },
    })
  }

  function showCodeDisplay() {
    // map goals to a per-team basis
    mapGoals2PerTeamBasis(model)

    dialog.open(CodeDisplay, {
      props: {
        modal: true,
        showHeader: true,
        position: "top",
        draggable: false,
        pt: {
          root: {
            class: 'w-[64rem] h-5/6 bg-surface-0'
          }
        }
      }
    })
  }

  function mapGoals2PerTeamBasis(model: Model) {
    if(model.teams !== undefined && model.teams!.length > 0) {
      model.teams?.forEach(team => {
        team.goals = structuredClone(toRaw(model.goals!))
      })
      model.goals = {}
    }

  }

  function showChangeMetadata() {
    dialog.open(MetadataOverview, {
      props: {
        modal: true,
        draggable: false
      }
    })
  }

  function showChangeTeams() {
    dialog.open(TeamOverview, {
      props: {
        modal: true,
        draggable: false
      }
    })
  }

</script>
