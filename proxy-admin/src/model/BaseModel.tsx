import type {PageBaseModel} from "./PageBaseModel";

// @ts-ignore
export interface BaseModel<T = any> extends PageBaseModel {

  /**
   * 主键
   */
  id?: string
  /**
   * 备注
   */
  remark?: string
  /**
   * 删除状态
   */
  deleted?: number
  /**
   *  状态
   */
  status?: number
  /**
   * 添加时间
   */
  addTime?: string

  /**
   * 添加用户ID
   */
  addUserId?: string
  /**
   * 添加用户名称
   */
  addUserName?: string
  /**
   * 修改时间
   */
  editTime?: string
  /**
   * 修改用户名称
   */
  editUserId?: string
  /**
   * 修改用户名称
   */
  editUserName?: string
  /**
   * 机构ID
   */
  organizationId?: string
  /**
   * 机构编码
   */
  organizationCode?: string
  /**
   * 机构名称
   */
  organizationName?: string
  /**
   * 顶级机构ID
   */
  topOrganizationId?: string
  /**
   * 顶级机构编码
   */
  topOrganizationCode?: string
  /**
   * 顶级机构名称
   */
  topOrganizationName?: string
}
