import { ref, watch } from 'vue'

export function useThemeSwitcher() {

    const dark = ref(false) // light SWITCH dark

    watch(dark, (newDarkValue: boolean) => {
        if(newDarkValue) {
            document.documentElement.classList.add('dark');
        }
        else {
            document.documentElement.classList.remove('dark');
        }
    })

    return {
        dark
    }
}