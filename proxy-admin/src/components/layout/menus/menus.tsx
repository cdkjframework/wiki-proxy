export interface Menus {
  /**
   * 路由路径
   */
  code: string,
  /**
   * 名称
   */
  name: string,
  /**
   * meta
   */
  meta: Meta,
  /**
   * 菜单等级
   */
  level?: number,
  /**
   * 菜单父级ID
   */
  parentId?: string,
  /**
   * 是否隐藏
   */
  hide?: boolean,
  /**
   * 文件路径
   */
  viewPath: string,

  /**
   * 下级菜单
   */
  children: Menus[]
}

export interface Meta {
  /**
   * 标题
   */
  title: string
}