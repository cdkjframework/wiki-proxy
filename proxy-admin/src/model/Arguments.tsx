export interface Arguments<T = any> {
  /**
   * 参数类型
   */
  type?: string

  /**
   * 参数数据
   */
  data?: T,
  /**
   * 参数默认值
   */
  value?: any

  /**
   * 参数ref
   */
  ref?: any
  /**
   * 列信息
   */
  column?: any
}
