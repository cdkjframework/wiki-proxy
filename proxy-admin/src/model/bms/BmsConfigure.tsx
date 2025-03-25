import type {BaseModel} from "../BaseModel"

export interface BmsConfigure extends BaseModel<BmsConfigure> {

  /**
   * 分组编码
   */
  groupCode?: string
  /**
   * 分组名称
   */
  groupName?: string
  /**
   * 配置名称
   */
  configName?: string
  /**
   * 键
   */
  configKey?: string
  /**
   * 值
   */
  configValue?: string
  /**
   * 控制类型（input,select）
   */
  controlType?: string
  /**
   * 控件值
   */
  defaultValue?: string
  /**
   * 备注
   */
  remark?: string
  /**
   * 参数值
   */
  argument?: string
  /**
   * 排序
   */
  sort?: number

  /**
   * 子配置
   */
  children?: BmsConfigure[]
}
