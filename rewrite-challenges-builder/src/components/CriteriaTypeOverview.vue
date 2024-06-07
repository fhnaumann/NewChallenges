<template>
  <div class="flex flex-col items-center space-y-2 py-4 bg-primary-100 rounded-xl">
    <Button :label="t(`${criteriaType}.browse_button`)" @click="showSearcher()" />
    <p class="text-3xl font-bold text-primary-900">{{ t(`${criteriaType}.global_title`) }}</p>
    <Button class="h-10" v-if="criteriaType === 'rules'" :label="t(`rules.configure_global_punishments`)" @click="showGlobalPunishments"/>
    <div class="h-10" v-else />
    <div class="flex flex-col space-y-2 w-full px-12 py-4">
      <ActiveCriteriaRow
        v-for="activeCriteria in getCriteria()"
        :key="activeCriteria"
        :criteria-code="activeCriteria"
        :criteria-type="criteriaType"
      >
      </ActiveCriteriaRow>
    </div>
  </div>
</template>

<script setup lang="ts">
import Button from 'primevue/button'
import { computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import ActiveCriteriaRow from '@/components/ActiveCriteriaRow.vue'
import type { CriteriaKey, CriteriaType } from '@/models/model'
import { getBgColor } from '@/util'
import { useModelStore } from '@/stores/model'
import { useDialog } from 'primevue/usedialog'
import PunishmentList from './rules/PunishmentList.vue'
import SearchComponent from '@/components/search/SearchComponent.vue'
import { useRouter } from 'vue-router'

const props = defineProps<{
  criteriaType: CriteriaType
}>()

const { t } = useI18n()
const dialog = useDialog()
const modelStore = useModelStore()
const router = useRouter()

function getCriteria(): CriteriaKey[] {
  if(props.criteriaType === 'rules') {
    return (modelStore.model.rules?.enabledRules !== undefined ? Object.keys(modelStore.model.rules?.enabledRules!) : []) as CriteriaKey[]
  }
  else {
    return (modelStore.model[props.criteriaType] !== undefined ? Object.keys(modelStore.model[props.criteriaType]!) : []) as CriteriaKey[]
  }
}

function showGlobalPunishments() {
  dialog.open(PunishmentList, {
    props: {
      modal: true,
      blockScroll: true,
      draggable: false,
      pt: {
        root: {
          class: 'customized-rule rounded-lg shadow-lg border-0 max-h-[90vh] w-[50vw] m-0 bg-rule-200 border-b-2 border-rule-200'
        },
        closeButton: {
          class: ['customized-rule', 'relative', 'flex items-center justify-center', 'mr-2', 'last:mr-0', 'w-7 h-7', 'border-0', 'rounded-full', 'text-surface-500', 'bg-transparent', 'transition duration-200 ease-in-out', 'hover:text-surface-700 dark:hover:text-white/80', 'hover:bg-surface-100 dark:hover:bg-[rgba(255,255,255,0.03)]', 'focus:outline-none focus:outline-offset-0 focus:ring-1', 'focus:ring-primary-500 dark:focus:ring-primary-400', 'overflow-hidden']
        }
      }
    },
    data: {
      global: true
    }
  })
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
      },
    },
    data: {
      validOption: props.criteriaType
    },
    onClose(options) {
      if(options?.data) {
        router.push(options?.data.navigateTo)
      }
    },
  })
}



</script>
