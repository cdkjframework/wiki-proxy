<template>
  <el-breadcrumb separator="/" class="bread-crab">
    <el-breadcrumb-item class="bread-crab-item" v-for="(item, index) in state.breadcrumb" :key="index"
                        :to="{ path: item.code }">
      <span :class="item.code?'link':''">{{ $t(item.meta.title) }}</span>
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup lang="ts">
import {onMounted, reactive, watch} from "vue";
import {useRoute} from "vue-router";

import {Storage} from "@/utils/storage";
import {ROUTER_RESOURCES} from "@/utils/enums/cache-enum";

import router from "@/router";

const route = useRoute();

const state = reactive({
  breadcrumb: [] as any
})

onMounted(() => {
  build(route.path)
})

watch(
    () => route.path,
    (path) => {
      build(path)
    }
);

const build = (path: string) => {
  const resources = Storage.get(ROUTER_RESOURCES);
  if (resources) {
    buildCrumbs(path)
  } else {
    systemCrumbs(path)
  }
}

const systemCrumbs = (path: string) => {
  const resources = router.getRoutes()
  for (let index in resources) {
    const route = resources[index]
    state.breadcrumb = [{code: route.path, meta: {title: route.meta.local}}]
    if (route.children && route.children.length > 0) {
      systemRecursion(route.children, route.path, path)
    }
  }
  state.breadcrumb.splice(0, 0, {code: '/', meta: {title: 'page.index'}})
}

const systemRecursion = (resources: any, path: string, href: string) => {
  let pathValue = ''
  for (let index in resources) {
    const route = resources[index]
    pathValue = path + '/' + route.path
    if (pathValue === href) {
      state.breadcrumb.push({code: null, meta: {title: route.meta.local}})
      continue
    }
    if (route.children && route.children.length > 0) {
      systemRecursion(route.children, pathValue, href)
    }
  }
}

const buildCrumbs = (path: string) => {
  // 加载菜单
  const resources = Storage.get(ROUTER_RESOURCES);
  const item = resources?.find((f: any) => f.code === path)
  state.breadcrumb.splice(0, 0, item)
  if (!item?.parentId) {
    return
  }
  recursion(resources, item?.parentId)
}

const recursion = (resources: any, parentId: any) => {
  const parent = resources.find((f: any) => f.id === parentId)
  state.breadcrumb.splice(0, 0, parent)
  if (!parent.parentId) {
    recursion(resources, parent.parentId)
  }
}
</script>
<style lang="less">
.bread-crab {
  text-align: left;
  padding: 10px 20px;
  margin-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;

  .bread-crab-item {
    .link {
      color: rgba(0, 0, 0, .45);
      font-weight: normal;
    }
  }
}
</style>
