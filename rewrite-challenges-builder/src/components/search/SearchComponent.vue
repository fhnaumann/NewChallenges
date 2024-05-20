<template>
  <div>
    <InputText class="w-full" type="text" :placeholder="t(`general.search.placeholder`)" v-model="searchFieldValue" />
  </div>
  <div>
    <DataView :value="getPartialMatches()" paginator :rows="5">
      <template #list="slotProps">
        <div class="flex p-4 items-center w-[32rem] border-2 border-black">
          <TransitionGroup tag="div" :css="false" @before-enter="onBeforeEnter"
                           @enter="onEnter"
                           @leave="onLeave">
              <SearchCriteriaRow class="w-full h-25 border-2 border-green-500" v-for="(item, index) in slotProps.items" :key="index" :criteria-type="(item as Searchable).criteriaType"
                                 :criteria-key="(item as Searchable).criteriaKey" />
            <!--p v-for="(item, index) in slotProps.items" :key="index">{{ index }}</p-->
          </TransitionGroup>
        </div>
      </template>
      <template #empty>
        <div>
          <p>{{ t('general.search.no_matches') }}</p>
        </div>
      </template>
    </DataView>
  </div>

</template>

<script setup lang="ts">
  import DataView from 'primevue/dataview'
  import InputText from 'primevue/inputtext'
  import { type Searchable, useSearchable } from '@/searchable'
  import SearchCriteriaRow from '@/components/search/SearchCriteriaRow.vue'
  import { useI18n } from 'vue-i18n'
  import gsap from 'gsap'

  const { searchFieldValue, getPartialMatches } = useSearchable(undefined)

  const { t } = useI18n()

  function onBeforeEnter(el) {
    el.style.opacity = 0
    el.style.height = 0
  }

  function onEnter(el, done) {
    gsap.to(el, {
      opacity: 1,
      height: el.height,
      delay: el.dataset.index * 0.15,tus
      onComplete: done,
    })
  }

  function onLeave(el, done) {
    gsap.set(el, {height: "auto"})
    gsap.to(el, {
      opacity: 0,
      height: 0,
      delay: el.dataset.index * 0.15,
      onComplete: done,
    })
  }

</script>