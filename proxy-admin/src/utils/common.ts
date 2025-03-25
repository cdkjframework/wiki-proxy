import dayjs from 'dayjs';
import {ElMessage} from "element-plus";

/**
 * @description 处理首字母大写 abc => Abc
 * @param str
 */
export const changeStr = (str: string) => {
  return str.charAt(0).toUpperCase() + str.slice(1);
};

/**
 * @description 随机生成颜色
 * @param {string} type
 * @return {string}
 */
export const randomColor = (type: 'rgb' | 'hex' | 'hsl'): string => {
  switch (type) {
    case 'rgb':
      return window.crypto.getRandomValues(new Uint8Array(3)).toString();
    case 'hex':
      return `#${Math.floor(Math.random() * 0xffffff)
        .toString(16)
        .padStart(6, `${Math.random() * 10}`)}`;
    case 'hsl':
      // 在25-95%范围内具有饱和度，在85-95%范围内具有亮度
      return [360 * Math.random(), `${100 * Math.random()}%`, `${100 * Math.random()}%`].toString();
  }
};

/**
 * 复制文本
 * @param text
 */
export const copyText = (text: string) => {
  return new Promise((resolve) => {
    const copyInput = document.createElement('input'); //创建一个input框获取需要复制的文本内容
    copyInput.value = text;
    document.body.appendChild(copyInput);
    copyInput.select();
    document.execCommand('copy');
    copyInput.remove();
    resolve(true);
  });
};

/**
 * @description 判断字符串是否是base64
 * @param {string} str
 */
export const isBase64 = (str: string): boolean => {
  if (str === '' || str.trim() === '') {
    return false;
  }
  try {
    return btoa(atob(str)) == str;
  } catch (err) {
    return false;
  }
};
// 对象转JSON
export const toJSON = (obj = [] as any) => {
  return JSON.stringify(obj, (_, value) => {
    switch (true) {
      case typeof value === 'undefined':
        return 'undefined';
      case typeof value === 'symbol':
        return value.toString();
      case typeof value === 'function':
        return value.toString();
      default:
        break;
    }
    return value;
  });
};

/***
 * @description 格式化日期
 * @param time
 */
export const formatDate = (time = new Date()) => dayjs(time).format('YYYY-MM-DD HH:mm:ss');

/**
 *  @description 将一维数组转成树形结构数据
 * @param items
 * @param id
 * @param link
 */
export const generateTree = (items = [] as any, id = 0, link = 'parent') => {
  return items
    .filter((item = {} as any) => item[link] == id)
    .map((item = {} as any) => ({
      ...item,
      slots: {title: 'name'},
      children: generateTree(items, item.departmentid),
    }));
};

/***
 * @description 原生加密明文
 * @param {string} plaintext
 */
// const encryption = (plaintext: string) =>
//   isBase64(plaintext) ? plaintext : window.btoa(window.encodeURIComponent(plaintext));

/**
 * @description 原生解密
 * @param {string} ciphertext
 */
// const decryption = (ciphertext: string) =>
//   isBase64(ciphertext) ? window.decodeURIComponent(window.atob(ciphertext)) : ciphertext;

// const viewsModules = import.meta.glob('../views/**/*.vue');

// /**
//  *
//  * @param {string} viewPath 页面的路径 `@/view/${viewPath}`
//  * @param {string} viewFileName  页面文件 默认 index.vue
//  */
// export const getAsyncPage = (viewPath: string, viewFileName = 'index') => {
//   if (viewPath.endsWith('.vue')) {
//     const p = `../views/${viewPath}`;
//     const pathKey = Object.keys(viewsModules).find((key) => key === p)!;
//     // console.log('viewsModules[pathKey]', viewsModules[pathKey]);
//     return viewsModules[pathKey];
//   } else {
//     const p = `../views/${viewPath}/${viewFileName}.vue`;
//     const pathKey = Object.keys(viewsModules).find((key) => key === p)!;
//     // console.log('viewsModules[pathKey]', viewsModules[pathKey]);
//     return viewsModules[pathKey];
//     // return () => import(/* @vite-ignore */ `../views/${viewPath}/${viewFileName}.vue`);
//   }
// };

/**
 * / _ - 转换成驼峰并将view替换成空字符串
 * @param {*} name name
 */
export const toHump = (name = '') => {
  return name
    .replace(/[-/_](\w)/g, (_ = null as any, letter) => {
      return letter.toUpperCase();
    })
    .replace('views', '');
};

/*
 ** randomWord 产生任意长度随机字母数字组合
 ** randomFlag-是否任意长度 min-任意长度最小位[固定位数] max-任意长度最大位
 */
export function randomWord(randomFlag = true, min = 0, max = 100) {
  let str = '';
  let range = min;
  const arr = [
    '0',
    '1',
    '2',
    '3',
    '4',
    '5',
    '6',
    '7',
    '8',
    '9',
    'a',
    'b',
    'c',
    'd',
    'e',
    'f',
    'g',
    'h',
    'i',
    'j',
    'k',
    'l',
    'm',
    'n',
    'o',
    'p',
    'q',
    'r',
    's',
    't',
    'u',
    'v',
    'w',
    'x',
    'y',
    'z',
    'A',
    'B',
    'C',
    'D',
    'E',
    'F',
    'G',
    'H',
    'I',
    'J',
    'K',
    'L',
    'M',
    'N',
    'O',
    'P',
    'Q',
    'R',
    'S',
    'T',
    'U',
    'V',
    'W',
    'X',
    'Y',
    'Z',
  ];

  // 随机产生
  if (randomFlag) {
    range = Math.round(Math.random() * (max - min)) + min;
  }

  for (let i = 0; i < range; i++) {
    const pos = Math.round(Math.random() * (arr.length - 1));
    str += arr[pos];
  }
  return str;
}

/**
 *对数组进行分组(按某个字段)
 **/
export const groupByKey = (arr = [] as any, key = '' as any) => {
  const map = {} as any;
  const dest = [] as any;
  for (let i = 0; i < arr.length; i++) {
    const ai = arr[i] as any;
    if (!map[ai[key]]) {
      dest.push({
        key: ai[key],
        data: [ai],
      });
      map[ai[key]] = ai as any;
    } else {
      for (let j = 0; j < dest.length; j++) {
        const dj = dest[j] as any;
        if (dj.key == ai[key]) {
          dj.data.push(ai);
          break;
        }
      }
    }
  }
  return dest;
};
/**
 *codes 下拉项专用：根据key查询出下拉项options
 **/
export const getOptionsByKey = (arr: any = [], key = '', labelInValue = false) => {
  const {data} = arr.find((_: any) => _.key === key) as any;
  return data.map((item = {} as any) => ({
    label: item.constName,
    value: labelInValue === true ? `${item.constName}-${item.constCode}` : item.constCode,
  }));
};

/***
 * 根据身份证号计算出年龄
 * @IDCard 身份证号
 * */
export function getAgeByIDCard(IDCard = '' as any) {
  const age = 0;
  let yearBirth;
  let monthBirth;
  let dayBirth;
  //获取用户身份证号码
  const userCard = IDCard;
  //如果身份证号码为undefind则返回空
  if (!userCard) {
    return age;
  }
  const reg = /(^\d{15}$)|(^\d{17}([0-9]|X)$)/; //验证身份证号码的正则
  if (reg.test(userCard)) {
    if (userCard.length == 15) {
      const orgBirthday = userCard.substring(6, 12);
      //获取出生年月日
      yearBirth = `19${orgBirthday.substring(0, 2)}`;
      monthBirth = orgBirthday.substring(2, 4);
      dayBirth = orgBirthday.substring(4, 6);
    } else if (userCard.length == 18) {
      //获取出生年月日
      yearBirth = userCard.substring(6, 10);
      monthBirth = userCard.substring(10, 12);
      dayBirth = userCard.substring(12, 14);
    }
    //获取当前年月日并计算年龄
    const myDate = new Date();
    const monthNow = myDate.getMonth() + 1;
    const dayNow = myDate.getDate();
    let age = myDate.getFullYear() - yearBirth;
    if (monthNow < monthBirth || (monthNow == monthBirth && dayNow < dayBirth)) {
      age--;
    }
    //返回年龄
    return age;
  } else {
    return '';
  }
}

/**
 * 获取随机数
 * */
export const getRandom = (min = 0 as any, max = 9999 as any) => {
  let base = 1000;
  const maxStr = max;
  if (maxStr.length > 3) {
    for (let i = 4; i <= maxStr.length; i++) {
      base *= 10;
    }
  }
  const num = (Math.random() * base).toString();
  if (max) {
    const t = parseInt(num) % (max - min + 1);
    return min + t;
  } else {
    max = min;
    return parseInt(num) % (max + 1);
  }
};

/**
 * 复制消息
 */
export const copyMessage = (content: string) => {
  if (navigator.clipboard && window.isSecureContext) {
    navigator.clipboard.writeText(content).then(() => {
      ElMessage.success('复制成功！')
    }, () => {
      ElMessage.success('复制失败！')
    })
  } else {
    const aux = document.createElement("input");
    aux.setAttribute("value", content);
    document.body.appendChild(aux);
    aux.select();
    document.execCommand("copy");
    document.body.removeChild(aux);
    ElMessage.success('复制成功！')
  }
}