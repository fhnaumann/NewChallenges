<template>
  <div class="customized-setting flex flex-col items-center space-y-4">
    <p class="text-7xl text-center font-extrabold bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500 text-transparent bg-clip-text py-4">{{ t("homeview.slogan") }}</p>
    <div class="flex space-x-4">
      <InputText class="w-96 h-16 text-2xl" v-model="challengeID" size="large" :placeholder="t('homeview.challengeIDEnterPlaceholder')" />
      <Button class="h-16 text-2xl" :label="t('homeview.confirm_btn')" @click="handleViewChallenge" />
    </div>
    <div class="flex flex-col space-y-4">
      <div>
        <p class="text-5xl font-bold text-center text-primary-800">{{ t("homeview.mini-tutorial.title") }}</p>
      </div>
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 grid-rows-6 sm:grid-rows-3 lg:grid-rows-2 gap-8">
        <div class="flex flex-col w-[26rem] min-h-52 h-52 space-y-2 bg-primary-300 border-2 border-primary-700 rounded-md px-2 pt-2">
          <div class="text-3xl font-semibold text-primary-800">
            <i18n-t keypath="homeview.mini-tutorial.step1-title">
              <template #plugin_cap>
                <span class="bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500 text-transparent bg-clip-text">{{ t('homeview.mini-tutorial.highlights.plugin_cap')
                  }}</span>
              </template>
            </i18n-t>
          </div>
          <div class="ml-8 font-semibold">
            <i18n-t keypath="homeview.mini-tutorial.step1-text">
              <template #plugin>
                <span class="bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500 text-transparent bg-clip-text">{{ t("homeview.mini-tutorial.highlights.plugin") }}</span>
              </template>
              <template #modrinth>
                <a class="underline text-blue-600 hover:text-blue-800 visited:text-purple-600"
                   :href="PLUGIN_MODRINTH_URL" target="_blank">{{ t('homeview.mini-tutorial.highlights.modrinth') }}</a>
              </template>
              <template #plugins_cmd>
                <span class="bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500 text-transparent bg-clip-text">{{ t("homeview.mini-tutorial.highlights.plugins_cmd") }}</span>
              </template>
            </i18n-t>
          </div>
        </div>
        <div class="flex flex-col w-[26rem] min-h-52 h-52 space-y-2 bg-primary-300 border-2 border-primary-700 rounded-md px-2 pt-2">
          <div class="text-3xl font-semibold text-primary-800">
            <i18n-t keypath="homeview.mini-tutorial.step2-title">
              <template #challenge_cap>
                <span class="bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500 text-transparent bg-clip-text">{{ t('homeview.mini-tutorial.highlights.challenge_cap')
                  }}</span>
              </template>
            </i18n-t>
          </div>
          <div class="ml-8 font-semibold">
            <i18n-t keypath="homeview.mini-tutorial.step2-text" class="ml-4">
              <template #challenge>
                <span class="bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500 text-transparent bg-clip-text">{{ t("homeview.mini-tutorial.highlights.challenge") }}</span>
              </template>
              <template #challenges_builder>
                <a class="underline text-blue-600 hover:text-blue-800 visited:text-purple-600"
                   :href="CHALLENGE_BUILDER_URL" target="_blank">{{ t('homeview.mini-tutorial.highlights.challenges_builder') }}</a>
              </template>
            </i18n-t>
          </div>
        </div>
        <div class="flex flex-col w-[26rem] min-h-52 h-52 space-y-2 bg-primary-300 border-2 border-primary-700 rounded-md px-2 pt-2">
          <div class="text-3xl font-semibold text-primary-800">
            <i18n-t keypath="homeview.mini-tutorial.step3-title">
              <template #challenge_cap>
                <span class="bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500 text-transparent bg-clip-text">{{ t('homeview.mini-tutorial.highlights.challenge_cap')
                  }}</span>
              </template>
            </i18n-t>
          </div>
          <div class="ml-8 font-semibold text-pretty">
            <i18n-t keypath="homeview.mini-tutorial.step3-text">
              <template #download_settings_file>
                <span >{{ t("homeview.mini-tutorial.highlights.download_settings_file") }}</span>
              </template>
              <template #download>
                <span >{{ t("homeview.mini-tutorial.highlights.download") }}</span>
              </template>
              <template #settings_folder>
                <span >{{ t("homeview.mini-tutorial.highlights.settings_folder") }}</span>
              </template>
              <template #challenge>
                <span class="bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500 text-transparent bg-clip-text">{{ t("homeview.mini-tutorial.highlights.challenge") }}</span>
              </template>
            </i18n-t>
          </div>
        </div>
        <div class="flex flex-col w-[26rem] min-h-52 h-52 space-y-2 bg-primary-300 border-2 border-primary-700 rounded-md px-2 pt-2">
          <div class="text-3xl font-semibold text-primary-800">
            <i18n-t keypath="homeview.mini-tutorial.step4-title">
              <template #challenge_cap>
                <span class="bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500 text-transparent bg-clip-text">{{ t('homeview.mini-tutorial.highlights.challenge_cap')
                  }}</span>
              </template>
            </i18n-t>
          </div>
          <div class="ml-8 font-semibold">
            <i18n-t keypath="homeview.mini-tutorial.step4-text" class="ml-4">
              <template #challenge>
                <span class="bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500 text-transparent bg-clip-text">{{ t("homeview.mini-tutorial.highlights.challenge") }}</span>
              </template>
              <template #challenge_list>
                <span class="bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500 text-transparent bg-clip-text">{{ t("homeview.mini-tutorial.highlights.challenge_list") }}</span>
              </template>
            </i18n-t>
          </div>
        </div>
        <div class="flex flex-col w-[26rem] min-h-52 h-52 space-y-2 bg-primary-300 border-2 border-primary-700 rounded-md px-2 pt-2">
          <div class="text-3xl font-semibold text-primary-800">
            <i18n-t keypath="homeview.mini-tutorial.step5-title">
              <template #live_challenge_cap>
                <span class="bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500 text-transparent bg-clip-text">{{ t('homeview.mini-tutorial.highlights.live_challenge_cap')
                  }}</span>
              </template>
            </i18n-t>
          </div>
          <div class="ml-8 font-semibold">
            <i18n-t keypath="homeview.mini-tutorial.step5-text">
              <template #live_cmd>
                <span class="bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500 text-transparent bg-clip-text">{{ t("homeview.mini-tutorial.highlights.live_cmd") }}</span>
              </template>
            </i18n-t>
          </div>
        </div>
        <div class="flex flex-col w-[26rem] min-h-52 h-52 space-y-2 bg-primary-300 border-2 border-primary-700 rounded-md px-2 pt-2">
          <div class="text-3xl font-semibold text-primary-800">
            <i18n-t keypath="homeview.mini-tutorial.step6-title">
              <template #live_challenge_cap>
                <span class="bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500 text-transparent bg-clip-text">{{ t('homeview.mini-tutorial.highlights.live_challenge_cap')
                  }}</span>
              </template>
            </i18n-t>
          </div>
          <div class="ml-8 font-semibold">
            <i18n-t keypath="homeview.mini-tutorial.step5-text">
            </i18n-t>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">

import { useI18n } from 'vue-i18n'
import Button from 'primevue/button'
import PlayerHead from './PlayerHead.vue'
import TranslateMessageWithHeadOnly from '@/components/TranslateMessageWithHeadOnly.vue'
import TranslateMessage, { type RenderProps } from '@/components/TranslateMessage.vue'
import MaterialItem from '@/components/MaterialItem.vue'
import type { PlayerConfig } from '@criteria-interfaces/live'
import { type NoBlockBreakRuleDataConfig } from '@criteria-interfaces/blockbreak'
import InputText from 'primevue/inputtext'
import { ref, watch } from 'vue'
import { CHALLENGE_BUILDER_URL, PLUGIN_MODRINTH_URL } from '@/constants'
import { useRouter } from 'vue-router'

const { t } = useI18n()
const router = useRouter()

const challengeID = ref<string>()

function handleViewChallenge() {
  if(challengeID.value === undefined) {

    return
  }

  router.push(`/challenges/${challengeID.value}`)
}

</script>