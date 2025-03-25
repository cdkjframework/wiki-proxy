export interface ResponseBody<T = any> {

  /**
   * 状态码
   */
  code: number;

  /**
   * 总数量
   */
  total: number;

  /**
   * 消息
   */
  message: string;
  /**
   * 数据
   */
  data: T;
}
