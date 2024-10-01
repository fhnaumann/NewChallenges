/** @type {import('tailwindcss').Config} */
const colors = require('tailwindcss/colors')

const convert = (color) => {
  return `color-mix(in srgb, ${color} calc(100% * <alpha-value>), transparent)`;
};

export default {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  darkMode: ['selector'] ,
  theme: {
    extend: {
      backgroundImage: {
        'main-img': "url('@/assets/initial_bg.png')"
      },
      colors: {
        "primary-hover": convert('var(--p-primary-hover-color)'),
        "primary-active": convert('var(--p-primary-active-color)'),
        "content-border": convert('var(--p-content-border-color)'),
        "content-hover-background": convert('var(--p-content-hover-background)'),
        "content-hover": convert('var(--p-content-hover-color)'),
        "accent": convert('var(--accent)'),


        "background-color": convert('var(--background-color)'),
        card: convert('var(--card)'),
        "goal-accent": convert('var(--goal-accent)'),
      },
    },
  },
  plugins: [require('tailwindcss-primeui')],
}