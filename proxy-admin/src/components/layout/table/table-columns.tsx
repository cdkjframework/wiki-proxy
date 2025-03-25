import type {Buttons, Options, Rules} from '@/components/layout/common/common';
import type {Card} from "@/components/layout/form/form";

/**
 * 表格属性
 */
export interface TableColumns<T = any> {
  /**
   * 类型 (index,number,text,search,switch,datetime,datePicker,date,rangePicker,cascader,image,custom,format,formatDateTime,password,textarea,空)
   * 查看枚举常量  table-enums
   */
  type?: string,
  /**
   * 名称
   */
  label: string,
  /**
   * 字段
   */
  key?: string,
  /**
   * 宽度
   */
  width?: number,

  /**
   * 排序字段(搜索所用字段)
   */
  sort?: number,
  /**
   * 宽度
   */
  labelWidth?: number,
  /**
   * 是否搜索
   */
  search?: boolean,
  /**
   * 只有当 search 为 true 时有效
   */
  form?: Card,
  /**
   * 校验
   */
  rules?: Rules,
  /**
   * 位置
   */
  align?: string,

  /**
   * 是否固定
   */
  fixed?: string,

  /**
   * 选择数据源
   */
  options?: Options[],

  /**
   * 按钮
   */
  buttons?: Buttons[],
  /**
   * 格式化内容
   */
  formatter?: any

  /**
   * 提示文字(占位符)
   */
  placeholder?: string,

  /**
   * 值 当 type = format 时有效
   */
  value?: any,

  /**
   * 开启值（中文）
   */
  active?: string,
  /**
   * 关闭值（中文）
   */
  inactive?: string,
  /**
   * 关闭值
   */
  invalue?: any
}
