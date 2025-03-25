<template>
  <el-config-provider :locale="localeLang()">
    <el-container :style="{ height: 'calc(100vh)' }">
      <el-aside :width="state.menusWidth" v-if="!state.path">
        <dynamic-menus ref="dynamicMenusRef" :isCollapse="state.isCollapse"/>
      </el-aside>
      <el-container :width="state.rightWidth">
        <el-header v-if="!state.path" style="height: auto !important;">
          <dynamic-head ref="dynamicHeadRef" @click="callBack"/>
        </el-header>
        <dynamic-tabs
            v-if="!state.path"
            :name="state.name"
            ref="dynamicTabsRef"
        />
        <el-main class="tabs-view-content">
          <router-view #="{ Component }">
            <component @appClick="callBack" :is="Component"/>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </el-config-provider>
</template>

<script setup lang="ts">

import '@/assets/style/index.less'
import {reactive, ref, watch, watchEffect} from "vue";
import {useRoute} from "vue-router";
import {useStore} from "@/store/baseData/index";
import {Storage} from "@/utils/storage";
import {ACCESS_TOKEN_KEY} from "@/utils/enums/cache-enum";
import {transformI18n} from "@/i18n";
import zhLocale from "element-plus/es/locale/lang/zh-cn";

import DynamicMenus from "@/components/layout/menus/dynamic-menus.vue";
import DynamicHead from "@/components/layout/head/dynamic-head.vue";
import DynamicTabs from "@/components/layout/tabs/dynamic-tabs.vue";
import type {Arguments} from "@/model/Arguments.tsx";

const dynamicMenusRef = ref(DynamicMenus);
const dynamicHeadRef = ref(DynamicHead);
const dynamicTabsRef = ref(DynamicTabs);

const route = useRoute();
const store = useStore();

const state = reactive({
  path: true,
  isCollapse: false,
  name: null as any,
  menusWidth: "201px",
  rightWidth: "calc(100% - 201px)",
});

watch(
    () => route.path,
    (path) => {
      state.name = '';
      let token = Storage.get(ACCESS_TOKEN_KEY);
      if (!path && !token) {
        return;
      }
      state.path = path === "/" || !path || path === "/login" || !token;
    }
);
const callBack = (data: Arguments) => {
  switch (data.type || '') {
    case "closeTab":
      state.name = data.data;
      break;
    case "isCollapse":
      console.log('isCollapse', data)
      state.isCollapse = data.data;
      if (state.isCollapse) {
        state.menusWidth = "64px";
        state.rightWidth = "calc(100% - " + state.menusWidth + ")";
      } else {
        state.menusWidth = "201px";
        state.rightWidth = "calc(100% - " + state.menusWidth + ")";
      }
      break;
  }
};

const localeLang = () => {
  return zhLocale;
};

if (Storage.get(ACCESS_TOKEN_KEY)) {
  // 防止刷新页面后动态路由消失
  store.addDynamicRoute();
}

watchEffect(() => {
  if (route.meta?.title) {
    const title = document.title?.split(" - ")[0];
    document.title =
        transformI18n(title) + " - " + transformI18n(route.meta.local as string);
  }
});
</script>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}

.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}

.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>
