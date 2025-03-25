import type {BaseModel} from "../BaseModel"

export interface RmsRoleResource extends BaseModel<RmsRoleResource> {

  /**
   * 角色主键
   */
  roleId?: string
  /**
   * 主键
   */
  resourceId?: string

  /**
   * 资源 ID 列表
   */
  resourceIdList?: string[]
}
