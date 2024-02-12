<template>
    <div class="space-y-4">
      <h1 class="border-b border-black text-4xl">Goals</h1>
      <div class="mx-auto px-4">
        <Toast />
        <div class="flex flex-col space-y-4">
        <Button
          @click="showGoalSelection"
          class="cursor-pointer w-full h-20 bg-gray-600 py-1 rounded text-2xl text-white "
          label="Add new goal"
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
                v-for="activeGoal in activeGoalsView"
                :key="activeGoal.id"
                :pt="{
                  header: {
                    class: 'width: 32rem',
                  },
                }"
              >
                <template #header>
                  <span class="flex justify-between items-center gap-2 w-full">
                    <p>{{ activeGoal.label }}</p>
                    <button
                      class="cursor-pointer bg-red-700 py-1 px-1 rounded text-white"
                      @click="deleteActiveGoal(activeGoal.id)"
                      >Deactivate</button
                    >
                  </span>
                </template>
                <MobGoalVue
                  ref="mobGoal"
                  v-if="activeGoal.id === 'mobGoal'"
                />
                <ItemGoalVue v-if="activeGoal.id === 'itemGoal'"/>
                <BlockBreakGoal v-if="activeGoal.id === 'blockbreakGoal'" />
              </AccordionTab>
            </Accordion>
          </ScrollPanel>
        </div>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { useActivatableRule } from '../activatableRule.js'
  import ScrollPanel from 'primevue/scrollpanel'
  import Button from 'primevue/button'
  import Accordion from 'primevue/accordion'
  import AccordionTab from 'primevue/accordiontab'
  import { defineComponent, ref, defineAsyncComponent, computed, watch } from 'vue'
  
  import {
    useConfigStore,
    useDefaultConfigStore,
    useGoalsViewStore,
    useRulesViewStore,
  } from '@/main'
  import { useDialog } from 'primevue/usedialog'
  import Toast from 'primevue/toast'
  import { useToast } from 'primevue/usetoast'
  import type { EnabledRules, RuleName } from '../model/rules'
import type { GoalName, GoalsConfig } from '../model/goals'
import type DefaultGoalVue from './DefaultGoal.vue'
import MobGoalVue from './MobGoal.vue'
import GoalSelector from './GoalSelector.vue'
import ItemGoalVue from './ItemGoal.vue'
import BlockBreakGoal from './BlockBreakGoal.vue'
  
  // const GoalSelector = defineAsyncComponent(() => import('./GoalSelector.vue'))
  const config = useConfigStore().model
  const defaultConfig = useDefaultConfigStore()
  const goalsViewStore = useGoalsViewStore()
  
  const dialog = useDialog()
  const toast = useToast()
  
  const activeGoals = computed<GoalsConfig>(() => {
    console.log("ABC")
    console.log(config.goals)
    return config.goals
  })
  const activeGoalsView = computed(() =>
    Object.keys(activeGoals.value).map(
      goalName => goalsViewStore.allgoals[goalName as GoalName],
    ),
  )
watch(config.rules, (newRules) => {
  console.log(newRules)
}, {deep: true})

  const mobGoal = ref(undefined)
  
  const activeGoalReferences = [mobGoal]

  function deleteActiveGoal(goalName: GoalName): void {
    delete config.goals[goalName]
    activeGoalReferences.forEach(reference => {
      (reference.value! as any).cleanupFunc
    })
  }
  
  function showGoalSelection() {
    dialog.open(GoalSelector, {
      props: {
        modal: true,
        header: 'Select Goals',
      },
    })
  }
  </script>
  