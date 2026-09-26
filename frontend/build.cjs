const fs = require('fs');
const header = fs.readFileSync('header.html', 'utf8');
const footer = fs.readFileSync('footer.html', 'utf8');
const main = fs.readFileSync('main_content.html', 'utf8');

const guestLayout = `<template>
  <div class="bg-surface font-body-md text-on-surface">
    ${header.replace(/href="#"/g, 'href="/' + '"').replace(/class="([^"]*?)hidden(.*?)flex([^"]*?)"/g, 'class="$1hidden$2flex$3"')}
    <main class="w-full pt-16 bg-surface">
      <router-view />
    </main>
    ${footer.replace(/href="#"/g, 'href="/' + '"')}
  </div>
</template>
`;
fs.writeFileSync('src/components/layout/GuestLayout.vue', guestLayout);

const homeVue = `<template>
  <div class="flex flex-col w-full font-body-md overflow-x-hidden text-on-surface">
    ${main.replace(/class="flex flex-col w-full font-body-md overflow-x-hidden text-on-surface">/, '')}
</template>
<script setup>
</script>`;
fs.writeFileSync('src/views/Home.vue', homeVue);
