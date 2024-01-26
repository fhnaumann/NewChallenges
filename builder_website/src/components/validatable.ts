import { ref } from "vue"

export function useValidatable() {

    const valid = ref<boolean>(true)
    
    return valid
}