export interface Node {
  /**
   * 节点类型
   * 0 Object 1 Array
   */
  type: number,

  /**
   * 内容
   */
  content?: Node[],

  /**
   * 开始符号
   */
  start?: string,

  /**
   * 结束符号
   */
  end?: string,

  /**
   * 长度
   */
  length?: number,
  /**
   * 键
   */
  suffix?: string,

  /**
   * 键
   */
  key?: string,

  /**
   * 值
   */
  value?: string,

  /**
   * 是否为 Object
   */
  isObject: boolean
}