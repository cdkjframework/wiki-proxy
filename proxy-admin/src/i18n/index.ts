import {createI18n} from 'vue-i18n';

import enLocale from 'element-plus/es/locale/lang/en'
import zhLocale from 'element-plus/es/locale/lang/zh-cn'
import zhTwLocale from 'element-plus/es/locale/lang/zh-tw'

import en from './langs/en'
import cn from './langs/cn'
import tw from './langs/tw'

const messages = {
  en: {
    ...en,
    ...enLocale,
  },
  cn: {
    ...cn,
    ...zhLocale,
  },
  tw: {
    ...tw,
    ...zhTwLocale,
  }
};

//注册i8n实例并引入语言文件
const index = createI18n({
  legacy: false,
  globalInjection: true,
  locale: localStorage.lang || 'cn',		//默认显示的语言
  messages
})
export default index; //将i18n暴露出去，在main.ts中引入挂载

/**
 * 国际化转换工具函数，主要用于处理动态路由的title
 * @param {string} message message
 * @param isI18n  默认为true，获取对应的翻译文本,否则返回本身
 * @returns message
 */
export function transformI18n(message: string, isI18n: boolean = true): string {
  if (!message) {
    return '';
  }

  // 处理动态路由的title, 格式 {zh_CN:"",en_US:""}
  if (typeof message === 'object') {
    // return message;
    return message[index.global?.locale.value];
  } else if (isI18n) {
    return index.global.t(message) || message;
  }
  return message;
}
