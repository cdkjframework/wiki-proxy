<template>
  <div class="dynamic-tabs">
    <el-tabs
      ref="tabsRef"
      v-model="state.tabsIndex"
      type="card"
      closable
      class="tabs-header"
      @tab-click="handleTabsClick"
      @tab-remove="handleTabsRemove"
    >
      <el-tab-pane
        v-for="tab in state.tabsRoutes"
        :key="tab.path"
        :label="tab.label"
        :name="tab.name"
      />
    </el-tabs>
  </div>
</template>

<script lang="ts" setup>

import {useRouter} from 'vue-router'
import {onMounted, reactive, ref, watch} from 'vue';

import {Storage} from '@/utils/storage'
import {TABS_ROUTES} from '@/utils/enums/cache-enum';
import type {TabPaneName, TabsPaneContext} from 'element-plus';
import {ElMessage} from 'element-plus';

const route = useRouter()
const tabsRef = ref()

const props = defineProps({
  name: null as any
})

const state = reactive({
  tabsRoutes: [] as any,
  tabsIndex: '0',
  historyUri: null as any
})

watch(
  () => [route.currentRoute.value.path, props.name],
  (newValue, oldValue) => {
    // 要执行的方法
    if (newValue[0]) {
      loadData(route.currentRoute.value)
    }
    if (newValue[1] != oldValue[1]) {
      let tabsRoutes = Storage.get(TABS_ROUTES)
      const routes = tabsRoutes.find((f: { path: any }) => f.path === newValue[1])
      if (routes) {
        handleTabsRemove(routes.name)
      }
    }
  }
)

onMounted(() => {
  loadData(route.currentRoute.value)
})

const handleTabsRemove = (paneName: TabPaneName | undefined) => {
  let tabsRoutes = Storage.get(TABS_ROUTES)
  if (!tabsRoutes) {
    ElMessage.warning('异常、请刷新重试！')
    return
  }
  if (tabsRoutes.length === 1) {
    ElMessage.warning('首页不能删除！')
    return
  }
  const pane = tabsRoutes.find((f: { name: TabPaneName | undefined; }) => f.name === paneName)
  if (!pane) {
    return;
  }
  if (pane.path === '/admin/home') {
    ElMessage.warning('首页不能删除！')
    return
  }
  const index = tabsRoutes.indexOf(pane)
  tabsRoutes.splice(index, 1)
  Storage.set(TABS_ROUTES, tabsRoutes)
  state.tabsRoutes = tabsRoutes
  let tab: any;
  if (state.historyUri) {
    tab = tabsRoutes.find((f: { name: TabPaneName | undefined; }) => f.name === state.historyUri)
  }
  if (!tab) {
    tab = tabsRoutes[index - 1]
  }
  state.historyUri = tab.name
  setTimeout(() =>
    route.replace({path: tab.path, query: tab.query})
  );
}
const handleTabsClick = (pane: TabsPaneContext, ev: Event) => {
  let tabsRoutes = Storage.get(TABS_ROUTES)
  if (!tabsRoutes) {
    ElMessage.warning('异常、请刷新重试！')
    return
  }
  const tab = tabsRoutes.find((f: { name: any }) => f.name === pane.props.name)
  state.historyUri = tab.name
  setTimeout(() =>
    route.replace({path: tab.path, query: tab.query})
  );
}

const loadData = (route: any) => {
  if (!route) {
    return
  }
  const {path, name, query} = route
  if (!path || path === '/admin' || path === '/') {
    return;
  }
  const number = Math.random().toString()
  state.tabsIndex = number
  let tabsRoutes = Storage.get(TABS_ROUTES)
  if (tabsRoutes) {
    const tab = tabsRoutes.find((f: { path: any; }) => f.path === path)
    const item = tabsRoutes.find((f: { checked: any; }) => f.checked)
    if (item) {
      state.historyUri = item.name
      item.checked = false
    }
    if (tab) {
      state.tabsIndex = tab.name
      tab.checked = true
    } else {
      tabsRoutes.push({label: name, path: path, checked: true, name: number, query})
    }
  } else {
    tabsRoutes = [] as any
    tabsRoutes.push({label: name, path: path, checked: true, name: number, query})
  }
  Storage.set(TABS_ROUTES, tabsRoutes)
  state.tabsRoutes = tabsRoutes
}

</script>
