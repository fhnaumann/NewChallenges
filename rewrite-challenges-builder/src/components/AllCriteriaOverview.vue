<template>
  <div class="grid grid-cols-3 gap-x-2 gap-y-4 border-2 border-amber-300">
    <div class="col-span-3">
      <InputText class="w-full" type="text" :placeholder="t(`general.search.placeholder`)"
                 @click="showSearcher(); console.log('clicked')" />
    </div>

      <!--Dialog :visible="searcherVisible" modal :close-on-escape="true" :pt="{root: 'border-none', mask: {style: 'backdrop-filter: blur(2xp)'}}">
        <template #container="{ closeCallback }">
          <div class="w-[800] h-128">
            <SearchComponent />
          </div>
        </template>
      </Dialog-->


    <CriteriaTypeOverview criteria-type="rules" />
    <CriteriaTypeOverview criteria-type="goals" />
    <CriteriaTypeOverview criteria-type="settings" />
  </div>
</template>

<script setup lang="ts">
  import CriteriaTypeOverview from '@/components/CriteriaTypeOverview.vue'
  import SearchComponent from '@/components/search/SearchComponent.vue'
  import Dialog from 'primevue/dialog'
  import InputText from 'primevue/inputtext'
  import { useI18n } from 'vue-i18n'
  import { ref } from 'vue'
  import { useDialog } from 'primevue/usedialog'
  import { root } from 'postcss'

  const { t } = useI18n()

  const dialog = useDialog()

  function showSearcher() {
    dialog.open(SearchComponent, {
      props: {
        modal: true,
        showHeader: false,
        blockScroll: true,
        draggable: false,
        position: 'top',
        unstyled: true,
        pt: {
          root: {
            class: 'border-0 pt-5'
          },
          content: {
            class: 'rounded-none'
          },
          mask: {
            class: 'backdrop-blur-[2px]'
          }
        }
      },
    })
  }

</script>
