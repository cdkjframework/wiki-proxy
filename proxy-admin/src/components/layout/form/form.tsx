import type {Options, Props, Rules} from "@/components/layout/common/common";

export interface Form {
  /**
   * card 名称
   */
  label?: any,
  /**
   * 内容
   */
  card: Card[]
}

export interface Card {
  /**
   * 类型 (checkbox,cascader,radio,select,datetime,date,datetimerange,daterange,rate,selecttime,
   * selectrangetime,timepicker,switch,timerangepicker,text,image,images,file,空,disabled )
   */
  type?: string,
  /**
   * 名称
   */
  label?: string,
  /**
   * 多选
   */
  multiple?: boolean,
  /**
   * 字段
   */
  key?: string,
  /**
   *跨度
   */
  span?: number,
  /**
   * 可清除
   */
  clearable?: boolean,
  /**
   * 选择数据源
   */
  options?: Options[]
  /**
   * 值
   */
  value?: any

  /**
   * 样式
   */
  style?: any
  /**
   * 附加
   */
  append?: boolean
  /**
   * 前置
   */
  prepend?: boolean
  /**
   * 模板类型
   */
  template?: number
  /**
   * 模板值
   */
  templateKey?: string
  /**
   * 提示文字(占位符)
   */
  placeholder?: string

  /**
   * 连接地址
   */
  action?: string
  /**
   * 格式化类型
   */
  format?: string

  /**
   * 请求头内容
   */
  headers?: any

  /**
   * 是否为换行符号
   */
  line?: boolean,

  /**
   * 开启值（中文）
   */
  activeText?: string,
  /**
   * 开启值
   */
  activeValue?: any,
  /**
   * 关闭值（中文）
   */
  inactiveText?: string,
  /**
   * 关闭值
   */
  inactiveValue?: any,

  /**
   * 是否禁用
   */
  disabled?: boolean
  /**
   * 说明
   */
  explain?: string
  /**
   * 宽度
   */
  width?: number
  /**
   * 后缀
   */
  suffix?: string
  /**
   * 图标
   */
  icon?: string
  /**
   * 大小
   */
  size?: number,
  /**
   * 最大值
   */
  max?: number,
  /**
   * 最小值
   */
  min?: number,
  /**
   * 值
   */
  text?: string,

  /**
   * 验证属性
   */
  rules?: Rules[]
  /**
   * 属性
   */
  props?: Props
}
