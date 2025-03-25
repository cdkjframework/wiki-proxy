import {ROUTER_RESOURCES} from '@/utils/enums/cache-enum';
import {Storage} from '@/utils/storage'
import {defineStore} from 'pinia';
import dealWithRoute from '@/router/generate-route';
import type {RmsResource} from "@/model/rms/RmsResource";

export const useStore = defineStore('main', {
    state: () => {
      return {
        menuList: [] as Array<RmsResource>
      }
    },
    /** 获取菜单，添加动态路由 **/
    actions: {
      async addDynamicRoute() {
        /* eslint-disable */
        let nowMenuList: Array<RmsResource> = [];
        if (this.menuList?.length > 0) {
          nowMenuList = this.menuList;
        } else {
          const menus = Storage.get(ROUTER_RESOURCES) as Array<RmsResource>;
          if (!menus) {
            return false
          }
          // 通过接口拿菜单数据
          nowMenuList = typeof menus === "object" ? menus : JSON.parse(menus);
          this.menuList = nowMenuList || [];
        }
        /** 将拿到的菜单去匹配生成动态路由 **/
        dealWithRoute(nowMenuList);
        return true
      }
    }
  }
)
