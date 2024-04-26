<template>
      <h1>Hello App!</h1>
  <p>
    <strong>Current route path:</strong> {{ $route.fullPath }}
  </p>
  <nav>
    <RouterLink to="/">Go to Home</RouterLink>
    <RouterLink to="/builder">Go to Builder</RouterLink>
    <RouterLink to="/docs">Go to Docs</RouterLink>
  </nav>
  <main>
    <RouterView />
  </main>
</template>
