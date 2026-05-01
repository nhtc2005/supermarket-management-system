<script setup>
import { containerMaxW } from '@/config.js'
import BaseLevel from '@/components/BaseLevel.vue'

const year = new Date().getFullYear()
</script>

<template>
  <footer class="px-6 py-2" :class="containerMaxW">
    <BaseLevel type="justify-end">
      <div class="text-center md:text-right w-full">
        <b>&copy; {{ year }}</b>
        <slot />
      </div>
    </BaseLevel>
  </footer>
</template>
