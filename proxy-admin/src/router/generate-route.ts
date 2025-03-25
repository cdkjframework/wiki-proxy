import router from './index';
import type {RouteRecordRaw} from 'vue-router';
import type {RmsResource} from "@/model/rms/RmsResource";
// 导入
// @ts-ignore
const modules = import.meta.glob('@/views/**/**.vue')

// 所有的前端页面路由放到这来，然后通过这拿去注册路由
const routerList: Array<RmsResource> = [];

// 将后端菜单数据与前端路由匹配
const dealWithRoute = (use: Array<RmsResource>, all: Array<RmsResource> = routerList) => {
  if (routerList.length === 0) {
    all = use
  }
  const result: Array<RmsResource> = [];
  all.forEach((_all) => {
    use.forEach((_use) => {
      // 这匹配值根据后台传的值来定
      if (_use.name == _all.name) {
        if (_use.children && _use.children.length > 0) {
          dealWithRoute(_use.children, _all.children);
        }
        result.push(_all);
      }
    });
  });
  addDynamicRoute(result);
};

// 添加动态路由,parent默认为home是首页最外层的路由name名
const addDynamicRoute = (useRoute: Array<RmsResource>, code: string = '') => {
  const routes: Array<RmsResource> = useRoute.filter(f => f.resourceType === 1)
  for (let i = 0; i < routes.length; i++) {
    const route = routes[i]
    let view = route.viewPath ? route.viewPath : 'admin/home/LoginView.vue'
    if (view.indexOf('.vue') === -1) {
      view += '.vue'
    }
    const path = code + (route.code ? route.code : '')
    const com = modules[`/src/views/${view}`]
    if (!com) {
      continue
    }
    const routePath: RouteRecordRaw = {
      path: path,
      children: [],
      name: route.name,
      component: modules[`/src/views/${view}`],
    }
    router.addRoute(routePath);
    if (route.children && route.children.length > 0) {
      // 递归添加动态路由
      addDynamicRoute(route.children, path);
    }
  }
};

export default dealWithRoute;
