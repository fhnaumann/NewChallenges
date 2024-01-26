import { ref } from "vue"

export interface Searchable {
    label: string,
}

export function useSearchable(options: Searchable[]) {

    const searchFieldValue = ref<string>('')

    function getPartialMatches(): Searchable[] {
        if(!searchFieldValue.value.trim()) {
            return options
        }
        else {
            return options.filter((searchable) => searchable.label.toLowerCase().includes(searchFieldValue.value.toLowerCase()))
        }         
    }
    return { searchFieldValue, getPartialMatches }
}