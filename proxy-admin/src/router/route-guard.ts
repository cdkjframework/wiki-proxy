import router from '@/router/index';
import {ACCESS_TOKEN_KEY} from '@/utils/enums/cache-enum';
import {Storage} from '@/utils/storage'
// 引入第三方插件 进度条
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
// NProgress 配置
NProgress.configure({showSpinner: false});

/** 路由拦截 **/
router.beforeEach((to, from, next) => {
  const token = Storage.get(ACCESS_TOKEN_KEY);
  if (!token) {
    if (to.path.indexOf('admin') === -1 || to.path.indexOf('/login') > -1) {
      next();
    } else {
      next('/login')
    }
  } else {
    NProgress.start();
    if (to.matched.length > 0 || from.path !== '/') {
      next();
      NProgress.done();
    } else {
      // 防止页面刷新
      if (from.path == '/' && to.matched.length <= 0) {
        next(to.path);
      } else {
        next('/404')
      }
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done();
})

router.onError((_e) => {
  console.error('路由错误', _e)
})
