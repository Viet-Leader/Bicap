<template>
  <header class="app-header">
    <div class="logo">
      <span class="logo-text">BICAP</span>
    </div>
    <div class="actions">
      <slot name="actions" />
      <div class="user-profile">
        <span class="avatar">U</span>
      </div>
    </div>
  </header>
</template>

<style scoped lang="scss">
.app-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $space-4;
  background: rgba(15, 22, 41, 0.8);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  position: sticky;
  top: 0;
  z-index: 10;
}
.logo-text {
  color: $primary-green;
  font-size: 24px;
  font-weight: 700;
  font-family: 'Space Grotesk', sans-serif;
  letter-spacing: 1px;
}
.actions {
  display: flex;
  align-items: center;
  gap: $space-4;
}
.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: $primary-cyan;
  color: $bg-deep;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  cursor: pointer;
}
</style>
