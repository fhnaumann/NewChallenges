<template>
  <div class="bg-background-color min-h-screen">
    <HeaderBar />
    <div class="flex flex-col items-center space-y-20">
      <p
        class="text-7xl text-center font-extrabold bg-gradient-to-r from-primary to-pink-500 text-transparent bg-clip-text py-4">
        {{ t('homeview.slogan') }}</p>
      <div class="flex flex-col sm:flex-row sm:space-x-4 space-y-4 sm:space-y-0">
        <InputText class="w-full sm:w-96 h-16 text-2xl" v-model="challengeID" size="large"
                   :placeholder="t('homeview.challengeIDEnterPlaceholder')" />
        <Button class="h-16 text-2xl w-full sm:w-auto" :label="t('homeview.confirm_btn')" raised size="large" @click="handleViewChallenge" />
      </div>
      <div class="flex flex-col space-y-12">
        <div>
          <p class="text-5xl font-bold text-center text-color">{{ t('homeview.mini-tutorial.title') }}</p>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 grid-rows-6 sm:grid-rows-3 xl:grid-rows-2 gap-8 place-items-center">
          <div
            class="group ease-in-out duration-300 flex flex-col w-[26rem] min-h-52 h-52 space-y-2 rounded-xl px-2 pt-2 bg-card border border-content-border hover:border-goal-accent">
            <div class="text-3xl font-semibold text-color">
              <i18n-t keypath="homeview.mini-tutorial.step1-title">
                <template #plugin_cap>
                <span>{{ t('homeview.mini-tutorial.highlights.plugin_cap')
                  }}</span>
                </template>
              </i18n-t>
            </div>
            <div class="ml-8 font-semibold text-color">
              <i18n-t keypath="homeview.mini-tutorial.step1-text">
                <template #plugin>
                  <span
                    >{{ t('homeview.mini-tutorial.highlights.plugin')
                    }}</span>
                </template>
                <template #modrinth>
                  <a class="underline text-blue-600 hover:text-blue-800 visited:text-purple-600"
                     :href="PLUGIN_MODRINTH_URL" target="_blank">{{ t('homeview.mini-tutorial.highlights.modrinth')
                    }}</a>
                </template>
                <template #plugins_cmd>
                  <span
                    >{{ t('homeview.mini-tutorial.highlights.plugins_cmd')
                    }}</span>
                </template>
              </i18n-t>
            </div>
          </div>
          <div
            class="flex flex-col w-[26rem] min-h-52 h-52 space-y-2 rounded-xl px-2 pt-2 bg-card border border-content-border">
            <div class="text-3xl font-semibold text-color">
              <i18n-t keypath="homeview.mini-tutorial.step2-title">
                <template #challenge_cap>
                <span>{{ t('homeview.mini-tutorial.highlights.challenge_cap')
                  }}</span>
                </template>
              </i18n-t>
            </div>
            <div class="ml-8 font-semibold text-color">
              <i18n-t keypath="homeview.mini-tutorial.step2-text" class="ml-4">
                <template #challenge>
                  <span>{{ t('homeview.mini-tutorial.highlights.challenge')
                    }}</span>
                </template>
                <template #challenges_builder>
                  <a class="underline text-blue-600 hover:text-blue-800 visited:text-purple-600"
                     :href="CHALLENGE_BUILDER_URL"
                     target="_blank">{{ t('homeview.mini-tutorial.highlights.challenges_builder') }}</a>
                </template>
              </i18n-t>
            </div>
          </div>
          <div
            class="flex flex-col w-[26rem] min-h-52 h-52 space-y-2 rounded-xl px-2 pt-2 bg-card border border-content-border">
            <div class="text-3xl font-semibold text-color">
              <i18n-t keypath="homeview.mini-tutorial.step3-title">
                <template #challenge_cap>
                <span>{{ t('homeview.mini-tutorial.highlights.challenge_cap')
                  }}</span>
                </template>
              </i18n-t>
            </div>
            <div class="ml-8 font-semibold text-color">
              <i18n-t keypath="homeview.mini-tutorial.step3-text">
                <template #download_settings_file>
                  <span>{{ t('homeview.mini-tutorial.highlights.download_settings_file') }}</span>
                </template>
                <template #download>
                  <span>{{ t('homeview.mini-tutorial.highlights.download') }}</span>
                </template>
                <template #settings_folder>
                  <span>{{ t('homeview.mini-tutorial.highlights.settings_folder') }}</span>
                </template>
                <template #challenge>
                  <span
                    >{{ t('homeview.mini-tutorial.highlights.challenge')
                    }}</span>
                </template>
              </i18n-t>
            </div>
          </div>
          <div
            class="flex flex-col w-[26rem] min-h-52 h-52 space-y-2 rounded-xl px-2 pt-2 bg-card border border-content-border">
            <div class="text-3xl font-semibold text-color">
              <i18n-t keypath="homeview.mini-tutorial.step4-title">
                <template #challenge_cap>
                <span>{{ t('homeview.mini-tutorial.highlights.challenge_cap')
                  }}</span>
                </template>
              </i18n-t>
            </div>
            <div class="ml-8 font-semibold text-color">
              <i18n-t keypath="homeview.mini-tutorial.step4-text" class="ml-4">
                <template #challenge>
                  <span
                    >{{ t('homeview.mini-tutorial.highlights.challenge')
                    }}</span>
                </template>
                <template #challenge_list>
                  <span
                    >{{ t('homeview.mini-tutorial.highlights.challenge_list')
                    }}</span>
                </template>
              </i18n-t>
            </div>
          </div>
          <div
            class="flex flex-col w-[26rem] min-h-52 h-52 space-y-2 border border-content-border rounded-xl px-2 pt-2 bg-gradient-to-r from-primary to-pink-500">
            <div class="text-3xl font-semibold text-color-emphasis">
              <i18n-t keypath="homeview.mini-tutorial.step5-title">
                <template #live_challenge_cap>
                <span>{{ t('homeview.mini-tutorial.highlights.live_challenge_cap')
                  }}</span>
                </template>
              </i18n-t>
            </div>
            <div class="ml-8 font-semibold text-color-emphasis">
              <i18n-t keypath="homeview.mini-tutorial.step5-text">
                <template #live_cmd>
                  <span
                    >{{ t('homeview.mini-tutorial.highlights.live_cmd')
                    }}</span>
                </template>
              </i18n-t>
            </div>
          </div>
          <div
            class="flex flex-col w-[26rem] min-h-52 h-52 space-y-2 bg-card border border-content-border rounded-xl px-2 pt-2">
            <div class="text-3xl font-semibold text-color">
              <i18n-t keypath="homeview.mini-tutorial.step6-title">
                <template #live_challenge_cap>
                <span>{{ t('homeview.mini-tutorial.highlights.live_challenge_cap')
                  }}</span>
                </template>
              </i18n-t>
            </div>
            <div class="ml-8 font-semibold text-color">
              <i18n-t keypath="homeview.mini-tutorial.step5-text">
              </i18n-t>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">

import { useI18n } from 'vue-i18n'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import { ref, watch } from 'vue'
import { CHALLENGE_BUILDER_URL, PLUGIN_MODRINTH_URL } from '@/constants'
import { useRouter } from 'vue-router'
import HeaderBar from '@/components/HeaderBar.vue'

const { t } = useI18n()
const router = useRouter()

const challengeID = ref<string>()

function handleViewChallenge() {
  if(challengeID.value === undefined) {

    return
  }

  router.push(`/challenge/${challengeID.value}`)
}

</script>