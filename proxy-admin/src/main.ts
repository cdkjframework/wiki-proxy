import {type App, createApp, type DirectiveBinding} from 'vue'
import './style.css'
import AppElement from './App.vue'
import {CkeditorPlugin} from '@ckeditor/ckeditor5-vue';
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import ElementUI from 'element-plus'

import router from './router'
import {store} from './store'
import i18n from './i18n'
import 'element-plus/dist/index.css'
import './router/route-guard'

let app: App<Element> = createApp(AppElement)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

/* 定义全局变量 */
app.config.globalProperties.$isCollapse = false
app.config.globalProperties.$overall = {
  isCollapse: false,
  wsMessage: [] as any
}
app.directive('preventReClick', {
    mounted(el: HTMLElement, binding: DirectiveBinding): void {
      el.addEventListener('click', (): void => {
        if (el.style.pointerEvents != 'none') {
          el.style.pointerEvents = 'none'
          const timeout = binding.value || 1000
          setTimeout((): void => {
            el.style.pointerEvents = 'all'
          }, timeout)
        }
      })
    }
  }
)
app.config.globalProperties.$t = (key: any, value: any) => {
  return i18n.global.t(key, value)
}
const setupApp = (): void => {
  app.use(store)
    .use(router)
    .use(ElementUI)
    .use(CkeditorPlugin)
    .mount('#app')
}
setupApp()