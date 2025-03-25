<template>
  <div class="error">
    <div class="error-404">
      <div class="error-button">
        <div class="error-title">
          抱歉，您查找的页面不存在
        </div>
        <RouterLink :to="{ path: '/', replace: true }" class="error-back">点击这返回首页</RouterLink>
      </div>
    </div>
  </div>
</template>

<script>

</script>

<style scoped>

</style>
