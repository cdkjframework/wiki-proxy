import type {BaseModel} from "../BaseModel"

export interface RmsRole extends BaseModel<RmsRole> {

  /**
   * 角色名称
   */
  roleName?: string
  /**
   * 备注
   */
  remark?: string
  /**
   * 系统管理角色(0：否，1：是)
   */
  systems?: number
}
