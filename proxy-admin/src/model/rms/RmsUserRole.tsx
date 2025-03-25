import type {BaseModel} from "../BaseModel"

export interface RmsUserRole extends BaseModel<RmsUserRole> {

  /**
   * 账户ID
   */
  userId?: string
  /**
   * 角色ID
   */
  roleId?: string

  /**
   * 用户ID列表
   */
  userIds?: string[]
}
