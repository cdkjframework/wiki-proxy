import type {BaseModel} from "../BaseModel"

export interface RmsOrganization extends BaseModel<RmsOrganization> {

  /**
   * 机构类型0：普通，1：评估细分
   */
  organizationType?: number
  /**
   * 编码
   */
  code?: string
  /**
   * 名称
   */
  name?: string
  /**
   * 机构_标识
   */
  parentId?: string

  /**
   * 联系人
   */
  contact?: string
  /**
   * 联系电话
   */
  phone?: string

  /**
   * 联系电话
   */
  addressId?: string

  /**
   * 地址
   */
  address?: string
  /**
   * 备注
   */
  remark?: string
  /**
   * 经度
   */
  longitude?: number
  /**
   * 纬度
   */
  latitude?: number

  /**
   * 服务时间
   */
  serviceDate?: string
}
