<template>
  <div class="guest-layout">
    <header class="guest-header">
      <div class="container flex justify-between items-center">
        <router-link to="/" class="logo-text">BICAP</router-link>
        <nav class="flex gap-4">
          <router-link to="/login" class="text-cyan">Đăng nhập</router-link>
        </nav>
      </div>
    </header>
    <main>
      <router-view />
    </main>
  </div>
</template>

<style scoped lang="scss">
.guest-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.guest-header {
  padding: $space-4 0;
  background: rgba(15, 22, 41, 0.5);
  backdrop-filter: blur(10px);
  position: sticky;
  top: 0;
  z-index: 100;
}
.logo-text {
  color: $primary-green;
  font-size: 24px;
  font-weight: 700;
  font-family: 'Space Grotesk', sans-serif;
  letter-spacing: 1px;
}
</style>
