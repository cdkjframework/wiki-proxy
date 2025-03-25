import type {BaseModel} from "../BaseModel";

export interface RmsResource extends BaseModel<RmsResource> {

  /**
   * 路由
   */
  code: string,

  /**
   * 层级
   */
  level: number,
  /**
   * 父级
   */
  parentId: string,
  /**
   * 路由
   */
  viewPath: string,
  /**
   * 隐藏
   */
  hide: number,

  /**
   * 名称
   */
  name: string,
  /**
   * 排序
   */
  rank: number,

  /**
   * 类型
   */
  resourceType: number,

  /**
   * meta
   */
  meta: string

  /**
   * 子集
   */
  children: RmsResource[]
}
