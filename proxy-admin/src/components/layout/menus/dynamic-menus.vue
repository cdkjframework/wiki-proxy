<template>
  <div
    class="menu-list"
    :style="{
      height: state.isCollapse ? 'calc(100%)' : 'calc(100%)',
      width: !state.isCollapse ? '200px' : '64px',
    }"
  >
    <div class="menu-logo">
      <img v-if="!state.isCollapse" src="@/assets/images/logo.png" alt="logo" height="100"/>
    </div>
    <div class="iconfont-button" style="display: none">
      <icon
        @click="state.isCollapse = !state.isCollapse"
        :size="30"
        type="icon-a-lujing2040"
      />
    </div>
    <el-menu
      router
      :default-active="state.defaultActive"
      :default-openeds="state.defaultOpeneds"
      background-color="#072550"
      text-color="#C7C7C7"
      active-text-color="#FFFFFF"
      class="menu-vertical-style"
      :collapse="state.isCollapse"
    >
      <el-menu-item index="/admin/home">
        <el-icon>
          <icon :size="18" type="icon-a-zu1288"/>
        </el-icon>
        <template #title>首页</template>
      </el-menu-item>
      <div v-for="(menu, index) in state.menus" :key="index">
        <el-menu-item v-if="!menu.children" :index="menu.code">
          <el-icon>
            <icon :size="18" :type="menu.icons || 'icon-a-zu2551'"/>
          </el-icon>
          <template #title>{{ menu.name }}</template>
        </el-menu-item>
        <el-sub-menu v-else :index="'index-'+index">
          <template #title>
            <el-icon>
              <icon :size="18" :type="menu.icons || 'icon-a-zu2551'"/>
            </el-icon>
            <span>{{ menu.name }}</span>
          </template>
          <div
            v-for="(item, idx) in menu.children"
            :key="'for' + index + '-' + idx"
          >
            <el-menu-item v-if="!item.children" :index="item.code">
              <el-icon v-if="item.icons">
                <icon :size="18" :type="item.icons"/>
              </el-icon>
              <template #title>{{ item.name }}</template>
            </el-menu-item>
            <el-sub-menu v-else :index="'sub' + index + '-' + idx">
              <template #title>
                <el-icon v-if="item.icons">
                  <icon :size="18" :type="item.icons"/>
                </el-icon>
                <span>{{ item.name }}</span>
              </template>
              <el-menu-item
                v-for="(children, cIdx) in item.children"
                :key="'group' + index + '-' + cIdx"
                :index="children.code"
              >{{ children.name }}
              </el-menu-item>
            </el-sub-menu>
          </div>
        </el-sub-menu>
      </div>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import {Storage} from "@/utils/storage";
import {onMounted, reactive, watch} from "vue";
import {ROUTER_RESOURCES} from "@/utils/enums/cache-enum";
import {IconFont} from "@/components/iconfont";
import {useRouter} from "vue-router";
import type {RmsResource} from "@/model/rms/RmsResource";

const route = useRouter()
const icon = IconFont;

const props = defineProps({
  isCollapse: false,
});

const state = reactive({
  isCollapse: false,
  selectedKeys: [] as string[],
  defaultActive: '',
  defaultOpeneds: [],
  menus: [] as RmsResource[],
});
watch(
  () => props.isCollapse,
  (isCollapse) => {
    console.log("isCollapse", isCollapse);
    state.isCollapse = isCollapse;
  }
);

onMounted(() => {
  state.defaultActive = route.currentRoute.value.path
  const tabs = document.getElementsByClassName("tabs-view-content");
  if (tabs) {
    tabs[0].setAttribute("style", "");
  }
  // 加载菜单
  const resources = Storage.get(ROUTER_RESOURCES) as RmsResource[];
  if (resources) {
    const menuList = resources?.filter(
      (f: { level: number; hide: number }) =>
        f.level == 1 && (f.hide === 0 || !f.hide)
    );
    const firstMenus = JSON.parse(JSON.stringify(menuList))
    firstMenus.forEach((menus: any, index: number) => {
      const children = JSON.parse(JSON.stringify(menus['children'] || []))
      menus['children'] = null
      childrenMenus(menus, index, children);
    });
    state.menus = firstMenus as any;
  }
});

const childrenMenus = (menus: any, index: number, resources: any[]) => {
  const children = resources?.filter(
    (f: { parentId: number; hide: number }) => f.parentId === menus.id && (f.hide === 0 || !f.hide)
  );
  if (!children || children.length === 0) {
    return;
  }
  children.forEach((e: any, index: number) => {
    const child = JSON.parse(JSON.stringify(e['children'] || []))
    e['children'] = null
    childrenMenus(e, index, child);
  });
  const defaultActive = children.find(({code = ''}) => code === state.defaultActive)
  if (defaultActive) {
    state.defaultOpeneds.push('index-' + index)
    if (defaultActive.level === 3) {
      state.defaultOpeneds.push('sub' + index + '-' + children.indexOf(defaultActive))
    }
  }
  menus["children"] = children;
};
</script>
