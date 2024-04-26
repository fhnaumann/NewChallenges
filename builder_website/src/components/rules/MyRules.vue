<template>
  <ConfirmDialog :pt="{
    footer: {
      class: 'flex flex justify-end space-x-10'
    }
  }">

  </ConfirmDialog>
  <div class="space-y-4">
    <div class="flex items-center justify-center mt-4">
      <h1 class="text-4xl">{{ t("rules.global_title") }}</h1>
    </div>
    <div class="mx-auto px-4">
      <Toast />
      <div class="flex flex-col space-y-4">
        <Button
        :label="t('rules.configure_global_punishments')"
        @click="showGlobalPunishments"
        :badge="getGlobalActivePunishmentAmount()"
        badge-class="mr-6 scale-150"
        class="cursor-pointer w-full h-20 bg-gray-600 py-1 rounded text-2xl text-white "
        />
      <Button
        @click="showRuleSelection"
        :label="t('rules.add_new_rule')"
        class="cursor-pointer w-full h-20 bg-gray-600 py-1 rounded text-2xl text-white "
        />
      </div>

      <div class="flex mt-4">
        <ScrollPanel style="width: 100%; height: 610px">
          <Accordion
            :active-index="0"
            :pt="{
              root: {
                class: 'w-[32rem] min-w-[32rem] 2xl:w-[45rem]',
              },
            }"
          >
            <AccordionTab
              v-for="activeRule in activeRulesView.filter(rule => rule !== undefined)"
              :key="activeRule.id"
              :pt="{
                header: {
                  class: 'width: 32rem',
                },
              }"
            >
              <template #header>
                <span class="flex justify-between items-center gap-2 w-full">
                  <p>{{ t(`rules.types.${activeRule.id}.name`) }}</p>
                  <button
                    class="cursor-pointer bg-red-700 py-1 px-1 rounded text-white"
                    @click="deleteActiveRule(activeRule.id)"
                    >{{ t('rules.deactivate_button') }}</button
                  >
                </span>
              </template>
              <NoBlockBreaking
                v-if="activeRule.id === 'noBlockBreak'"
                :rule="activeRule"
              />
              <NoMobKillRule
                v-if="activeRule.id === 'noMobKill'" :rule="activeRule"
              />
              <NoItemRule
                v-if="activeRule.id === 'noItem'" :rule="activeRule"
              />
              <NoDeath
                v-if="activeRule.id === 'noDeath'"
                :rule="activeRule"
              />
              <!--NoBlockPlace
                v-if="activeRule.id === 'noBlockPlace'" :rule="activeRule" />
              <NoDamage
                v-if="activeRule.id === 'noDamage'"
                :rule="activeRule"
              />
              <NoDeath
                v-if="activeRule.id === 'noDeath'"
                :rule="activeRule"
              /-->
            </AccordionTab>
          </Accordion>
        </ScrollPanel>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useActivatableRule } from '../activatableRule.js'
import RuleEntry from './RuleEntry.vue'
import ScrollPanel from 'primevue/scrollpanel'
import Accordion from 'primevue/accordion'
import AccordionTab from 'primevue/accordiontab'
import NoBlockBreaking from './NoBlockBreaking.vue'
import { defineComponent, ref, defineAsyncComponent, computed } from 'vue'

import {
  useConfigStore,
  useDefaultConfigStore,
  useRulesViewStore,
} from '@/main'
import PunishmentList from '../punishments/PunishmentList.vue'
import ConfirmDialog from 'primevue/confirmdialog'
import Button from 'primevue/button'
import NoDamage from './NoDamage.vue'
import NoDeath from './NoDeath.vue'
import { useDialog } from 'primevue/usedialog'
import Toast from 'primevue/toast'
import { useToast } from 'primevue/usetoast'
import type { EnabledRules, RuleName } from '../model/rules'
import DefaultRule from './DefaultRule.vue'
import DefaultPunishableRule from './DefaultPunishableRule.vue'
import { useConfirm } from "primevue/useconfirm";
import NoBlockPlace from './NoBlockPlace.vue'
import NoMobKillRule from './NoMobKillRule.vue'
import { rule } from 'postcss'
import NoItemRule from './NoItemRule.vue'
import RuleSelector from './RuleSelector.vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const config = useConfigStore().model
const defaultConfig = useDefaultConfigStore()
const rulesViewStore = useRulesViewStore()

const dialog = useDialog()
const toast = useToast()
const confirm = useConfirm()

const activeRules = computed<EnabledRules>(() => config.rules.enabledRules)
const activeRulesView = computed(() =>
  Object.keys(activeRules.value).map(
    ruleName => rulesViewStore.allrules[ruleName as RuleName],
  ),
)

function deleteActiveRule(ruleName: RuleName): void {
  delete config.rules.enabledRules[ruleName]
}

function getGlobalActivePunishmentAmount() {
    return Object.keys(config.rules.enabledGlobalPunishments).length + ""
}

function showGlobalPunishments() {
  if (!Object.hasOwn(config.rules, 'enabledGlobalPunishments') || Object.keys(config.rules.enabledGlobalPunishments).length === 0) {
    console.log("resetting")
    config.rules.enabledGlobalPunishments = {}
  }

  const openPunishmentList = () => {
    dialog.open(PunishmentList, {
          props: {
            modal: true
          },
          data: {
            currentRule: null // important to set it to null
          },
        })
  }

  if(hasAtLeastOneLocalPunishmentEnabled()) {
  //if (true) {
    console.log("confirm")
    confirm.require({
      message: 'Modifying global punishments will override all local punishments. Proceeding will clear all local punishments you have set.',
      header: 'Overriding punishments',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Proceed',
      acceptClass: 'bg-orange-400 p-2',
      accept: () => {
        deleteAllLocalPunishments()
        toast.add({
          severity: 'warn', summary: 'Overriden punishments', detail: 'All local punishments have been cleared.', life: 5000
        }
        )
        openPunishmentList()
      }
    })
  }
  else {
    openPunishmentList()
  }
}

function deleteAllLocalPunishments() {
  Object.values(config.rules.enabledRules).forEach(rule => {
    delete rule.punishments
  });
}

function showRuleSelection() {
  dialog.open(RuleSelector, {
    props: {
      modal: true,
    },
  })
}

function hasAtLeastOneLocalPunishmentEnabled() {
  return Object.values(config.rules.enabledRules).some(rule => Object.hasOwn(rule, 'punishments') && Object.keys(rule.punishments!).length > 0)
}
</script>
