import type {AxiosRequestConfig} from 'axios';
import axios from 'axios';
import {ElMessage} from 'element-plus';
import encryption from '@/utils/encryption';
import {ACCESS_TOKEN_KEY} from './enums/cache-enum';
import {Storage} from './storage';
import type {ResponseBody} from '@/model/ResponseBody';
import router from "@/router/index"

export const METHODS = {
  GET: 'GET',
  POST: 'POST',
  PUT: 'PUT',
  PATCH: 'PATCH',
  DELETE: 'DELETE',
  OPTIONS: 'OPTIONS',
  FILE: 'FILE',
  EXPORT: 'EXPORT'
}
// 环境变量
// const isDev = process.env;

/* 参数加密开关 */
const closeParameterEncryption = true;

const UNKNOWN_ERROR = '未知错误，请重试';

/** 真实请求的路径前缀 */
const baseApiUrl = ''// process.env.VITE_BASE_API;
/** mock请求路径前缀 */
// const baseMockUrl = process.env.VITE_MOCK_API;
/** mock总开关 */
const isMock = false;
const service = axios.create({
  baseURL: isMock ? '' : baseApiUrl,
  timeout: 60000,
});

export interface RequestOptions {
  /**
   * 当前接口权限, 不需要鉴权的接口请忽略， 格式：sys:user:add
   */
  permCode?: string;
  /**
   * 是否直接获取data，而忽略ElMessage等
   */
  isDataDirectly?: boolean;
  /**
   * 请求成功是提示信息
   */
  success?: string;
  /**
   * 请求失败是提示信息
   */
  error?: string;
  /**
   * 是否mock数据请求
   */
  isMock?: boolean;
  /**
   * 请求类型
   */
  methods?: string;
}

service.interceptors.request.use(
  (config) => {
    const token = Storage.get(ACCESS_TOKEN_KEY) || 'rof3y6wPISqNNdH8ZciIc2xDCB4lIGawIviDrblWej6cHNeMO1/SYS4RMfd+B5ns';
    if (token && config.headers) {
      config.headers['token'] = token;
      config.headers['X-Requested-With'] = 'XMLHttpRequest';
      config.headers['Access-Control-Allow-Origin'] = '*';
      config.headers['Access-Control-Allow-Headers'] = 'X-Requested-With';
      config.headers['Access-Control-Allow-Methods'] = 'POST,GET';
      config.headers['X-Requested-token'] = closeParameterEncryption ? '' : 'token';
      config.headers['X-Requested-route'] = location.hash.replace('#', '');
      config.headers['Content-Type'] = config.headers.contentType || 'application/json';
    }
    return config;
  },
  (error) => {
    Promise.reject(error);
  },
);

service.interceptors.response.use(
  (response) => {
    let res = response.data;
    if (typeof res === "string") {
      res = JSON.parse(res)
    }
    if (response.status !== 200) {
      ElMessage.error(res.message || UNKNOWN_ERROR);
      // throw other
      const error = new Error(res.message || UNKNOWN_ERROR) as Error & { code: any };
      error.code = res.code;
      return Promise.reject(error);
    }
    // if the custom code is not 200, it is judged as an error.
    if (!(!res.code && res.length > 0) && res.code !== 0) {
      ElMessage.error(res.message || UNKNOWN_ERROR);

      // Illegal token
      if (res.code === 11001 || res.code === 11002) {
        window.localStorage.clear();
        window.location.reload();
      }
      // throw other
      return Promise.reject(res);
    } else {
      return response;
    }
  },
  (error) => {
    if (error.code === 'ECONNABORTED') {
      error.ElMessage = '请求超时！'
      return Promise.reject(error);
    }
    // 处理 422 或者 500 的错误异常提示
    const errMsg = error?.response?.data?.message ?? UNKNOWN_ERROR;
    Storage.clear()
    if (error.response.status === 401) {
      ElMessage({
        type: "error",
        message: errMsg,
        onClose: () => {
          router.replace("/login").then(r => {
            console.log(r)
          });
        },
      });
    } else if (error.response.status === 403) {
      ElMessage({
        type: "error",
        message: errMsg,
        onClose: () => {
          router.replace("/login").then(r => {
            console.log(r)
          });
        },
      });
    } else if (error.response.status === 404) {
      ElMessage({
        type: "error",
        message: '请求错误'
      });
      return Promise.reject(error);
    } else {
      ElMessage.error(errMsg ? errMsg : UNKNOWN_ERROR);
      error.ElMessage = errMsg ? errMsg : UNKNOWN_ERROR;
      return Promise.reject(error);
    }
  },
);

export type BaseResponse<T = any> = Promise<ResponseBody<T>>;

/**
 *
 * @param method - request methods
 * @param url - request url
 * @param data - request data or params
 */
export const request = async <T = any>(
  config: AxiosRequestConfig,
  options: RequestOptions = {},
): Promise<any> => {
  try {
    const {data, method = METHODS.POST} = config;
    const {success, error, permCode, isMock = false, isDataDirectly = true} = options;

    // 如果当前是需要鉴权的接口 并且没有权限的话 则终止请求发起
    if (permCode) {
      ElMessage.error('你没有访问该接口的权限，请联系管理员！');
      return;
    }

    //请求加密
    const parameter = closeParameterEncryption ? data :
      encryption.encrypt(JSON.stringify(data));
    switch (method) {
      case 'GET':
        config.params = parameter
        config.paramsSerializer = (parameter: any) => {
          return parameter
        }
        break
      case 'FILE':
        config.method = METHODS.POST
        const sBoundary = '---------------------------' + Date.now().toString(16);
        if (!config.headers) {
          config.headers = {}
        }
        config.headers.contentType = 'multipart/form-data; boundary=' + sBoundary;
        config.data = data
        break
      case 'EXPORT':
        config.method = METHODS.POST
        config.responseType = 'blob'
        config.data = parameter
        break
      default:
        config.data = parameter
        break
    }

    /**
     * 请求后台接口数据
     */
    const res = await service.request(config);
    let json = res.data
    if (!closeParameterEncryption) {
      json = JSON.parse(encryption.decrypt(json))
    }
    if (typeof json === "string") {
      json = JSON.parse(json)
    }
    if (method === 'EXPORT') {
      const fileName = res.headers['filename']
      const url = URL.createObjectURL(new Blob([json]))
      const a = document.createElement('a')
      document.body.appendChild(a) // 此处增加了将创建的添加到body当中
      a.href = url
      a.download = fileName
      a.target = '_blank'
      a.click()
      // 将a标签移除
      a.remove()
    } else {
      const data = typeof json === 'object' ? json : JSON.parse(json)
      if (res.headers) {
        const token = res.headers['token']
        if (token) {
          Storage.set(ACCESS_TOKEN_KEY, token)
        }
      }

      //console.log('请求结果', data);
      if (data.code === 0 && success) {
        ElMessage.success(success);
      } else if (data.code === 100) {
        Storage.clear()
        ElMessage({
          type: "error",
          message: error,
          onClose: () => {
            router.replace("/login").then(r => {
              console.log(r)
            });
          },
        });
      } else if (error) {
        ElMessage.error(error);
      }
      return isDataDirectly ? data.data : data;
    }
  } catch (error: any) {
    return Promise.reject(error);
  }
};
