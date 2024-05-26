<template>
  <div>
    <div class="flex-1">
      <IconField icon-position="left">
        <InputIcon>
          <i class="pi pi-search" />
        </InputIcon>
        <InputText class="w-full" type="text" :placeholder="t(`general.search.placeholder`)"
                   v-model="searchFieldValue" ref="dialogSearchText" />
      </IconField>
    </div>
    <div>
      <DataView :value="getPartialMatches()" paginator :rows="5">
        <template #list="slotProps">
          <div class="flex flex-col p-4 items-center w-full h-[32rem]">
            <SearchCriteriaRow class="w-full h-20 rounded-lg" v-for="(item, index) in slotProps.items"
                               :key="index" :criteria-type="(item as Searchable).criteriaType"
                               :criteria-key="(item as Searchable).criteriaKey" />
          </div>
        </template>
        <template #empty>
          <div>
            <p>{{ t('general.search.no_matches') }}</p>
          </div>
        </template>
      </DataView>
    </div>
  </div>
</template>

<script setup lang="ts">
  import DataView from 'primevue/dataview'
  import InputText from 'primevue/inputtext'
  import { type Searchable, useSearchable } from '@/searchable'
  import SearchCriteriaRow from '@/components/search/SearchCriteriaRow.vue'
  import { useI18n } from 'vue-i18n'
  import IconField from 'primevue/iconfield'
  import InputIcon from 'primevue/inputicon'
  import gsap from 'gsap'
  import { computed, inject, onMounted, ref, watch } from 'vue'

  const { searchFieldValue, getPartialMatches } = useSearchable(undefined)

  const { t } = useI18n()

  const dialogRef = inject('dialogRef') as any
  watch(dialogRef, () => {
    console.log(dialogRef)
  })

  const dialogSearchText = ref(null)

  onMounted(() => {
    console.log('mounted')
    console.log(dialogRef)
    console.log(dialogSearchText.value)
    dialogSearchText.value!.$el.focus()
  })

  const size = computed(() => {
    if (window.innerWidth < 640) {
      return 'w-11/12 h-3/4'
    } else if (window.innerWidth < 1024) {
      return 'w-3/4 h-2/3'
    } else {
      return 'w-1/2 h-1/2'
    }
  })

  function onBeforeEnter(el) {
    el.style.opacity = 0
    el.style.height = 0
  }

  function onEnter(el, done) {
    gsap.to(el, {
      opacity: 1,
      height: el.height,
      delay: el.dataset.index * 0.15,
      onComplete: done,
    })
  }

  function onLeave(el, done) {
    gsap.set(el, { height: 'auto' })
    gsap.to(el, {
      opacity: 0,
      height: 0,
      delay: el.dataset.index * 0.15,
      onComplete: done,
    })
  }

</script>