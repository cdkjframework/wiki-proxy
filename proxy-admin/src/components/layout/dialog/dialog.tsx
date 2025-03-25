export interface Dialog {
  /**
   * 是否显示
   */
  dialogVisible?: boolean,
  /**
   * 标题
   */
  title?: string,
  /**
   * 宽度
   */
  width?: string,
  /**
   * 加载
   */
  loading?: boolean,
  /**
   * 顶部距离
   */
  top?: any,
  /**
   * 按钮
   */
  button?: Button
}

export interface Button {
  /**
   * 是否显示取消按钮
   */
  cancel?: boolean,
  /**
   * 是否显示取消文字
   */
  cancelText?: string,
  /**
   * 是否显示确认按钮
   */
  confirm?: boolean,
  /**
   * 是否显示确认文字
   */
  confirmText?: string,
}