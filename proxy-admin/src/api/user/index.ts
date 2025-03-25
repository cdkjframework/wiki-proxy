import type {BaseResponse} from '@/utils/request';
import {METHODS, request} from '@/utils/request';
import type {RmsUser} from "@/model/rms/RmsUser";

export function userLoginApi(data: any) {
  let params = ''
  for (const key in data) {
    if (params != '') {
      params += '&'
    }
    params += key + '=' + data[key]
  }
  return request<BaseResponse>({
    baseURL: '/single/security/user/login',
    method: METHODS.POST,
    data: params,
  }, {
    isDataDirectly: false
  })
}

export function userTicketLoginApi(ticket: string, grantType: string) {
  return request<BaseResponse<RmsUser>>({
    baseURL: '/single/security/ticket.html?grantType=' + grantType + '&ticket=' + ticket,
    method: METHODS.GET
  }, {
    isDataDirectly: false
  })
}

export function logoutApi(data: RmsUser = {} as RmsUser) {
  return request<BaseResponse>({
    baseURL: '/single/security/user/logout',
    method: METHODS.POST,
    data
  }, {
    isDataDirectly: false
  })
}

export function listRmsUserPageApi(data: RmsUser = {} as RmsUser) {
  return request<BaseResponse>({
    baseURL: '/single/rms/user/listRmsUserPage',
    method: METHODS.POST,
    data
  }, {
    isDataDirectly: false
  })
}

export function insertUserApi(data: RmsUser = {} as RmsUser) {
  return request<BaseResponse>({
    baseURL: '/single/rms/user/addRmsUser',
    method: METHODS.POST,
    data
  }, {
    isDataDirectly: false
  })
}

export function modifyUserApi(data: RmsUser = {} as RmsUser) {
  return request<BaseResponse>({
    baseURL: '/single/rms/user/modifyRmsUser',
    method: METHODS.POST,
    data
  }, {
    isDataDirectly: false
  })
}

export function resetApi(data: string) {
  return request<BaseResponse>({
    baseURL: '/single/rms/user/reset?id=' + data,
    method: METHODS.GET
  }, {
    isDataDirectly: false
  })
}
