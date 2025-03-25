/**
 * 按钮属性
 */
export interface Buttons<T = any> {
  /**
   * 类型
   */
  type?: string

  /**
   * 选择类型
   */
  method: any

  /**
   * 是否为连接类型
   */
  link?: boolean

  /**
   * ICON图片
   */
  icon?: string

  /**
   * 样式
   */
  style?: any

  /**
   * 名称
   */
  text: any
}

/**
 * 选择属性
 */
export interface Options<T = any> {
  /**
   * 名称
   */
  label: string

  /**
   * 值
   */
  value: any
  /**
   * 子级
   */
  children?: Options[]
}
export interface Rules<T = any> {
  /**
   * 字段名称
   */
  key?: string
  /**
   * 类型 date array email number
   */
  type?: string
  /**
   * 是否验证
   */
  validate?: string
  /**
   * 提示消息
   */
  message?: string
  /**
   * 自定义正则
   */
  regex?: string
  /**
   * 方法
   */
  validator?: undefined,
  /**
   * 最小值
   */
  min?: number,
  /**
   * 最大值
   */
  max?: number,
  /**
   * 触发事件
   */
  trigger: string,

  /**
   * 是否验证
   */
  required: true
}

export interface Props<T = any> {

  /**
   * 值
   */
  value?: string,

  /**
   * 标签
   */
  label?: string,
  /**
   * 子级
   */
  children?: string,

  /**
   * 是否全选
   */
  checkStrictly?: boolean,

  /**
   * 是否多选
   */
  multiple?: boolean
}
