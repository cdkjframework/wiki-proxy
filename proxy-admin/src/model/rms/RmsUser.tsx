import type {BaseModel} from "../BaseModel"
import type {RmsResource} from "./RmsResource";
import type {BmsConfigure} from '../bms/BmsConfigure.tsx';

export interface RmsUser extends BaseModel<RmsUser> {

  /**
   * 角色列表
   */
  roles?: string[]
  /**
   * 登录名称
   */
  loginName?: string
  /**
   * 登录名称
   */
  username?: string
  /**
   * 账户类型(1：老人；2：雇员；3、家属；4：志愿者)
   */
  userType?: string
  /**
   * 账户类型(1：老人；2：雇员；3、家属；4：志愿者)
   */
  userTypeName?: string
  /**
   * 姓名
   */
  displayName?: string
  /**
   * 密码
   */
  password?: string

  /**
   * 旧密码
   */
  oldPassword?: string

  /**
   * 手机
   */
  cellphone?: string
  /**
   * 座机
   */
  telephone?: string
  /**
   * 籍贯
   */
  nativePlace?: string
  /**
   * 民族
   */
  nation?: string
  /**
   * 头像
   */
  headPortrait?: string
  /**
   * 最后登录时间
   */
  lastLoginTime?: string
  /**
   * 是否锁定
   */
  locked?: number
  /**
   * 性别(1 男,2 女)
   */
  sex?: number
  /**
   * 生日
   */
  birthday?: string
  /**
   * 居住详细地址
   */
  addressDetails?: string
  /**
   * 是否可用
   */
  enabled?: number
  /**
   * 当前所在组织ID
   */
  currentOrganizationId?: string
  /**
   * 备注
   */
  remark?: string
  /**
   * 审核状态 0.待审核 1.已审核 默认0
   */
  auditStatus?: number
  /**
   * 身份证号码
   */
  idCard?: string
  /**
   * 联系地址
   */
  addressContact?: string
  /**
   * 工作职务
   */
  jobTitle?: string
  /**
   * 国际区号
   */
  areaCode?: string
  /**
   * 开放平台ID
   */
  openId?: string
  /**
   * 微信唯一ID
   */
  unionId?: string
  /**
   * 省_id
   */
  provinceId?: string
  /**
   * 市_id
   */
  cityId?: string
  /**
   * 区（县）id
   */
  countyId?: string

  /**
   * 验证码
   */
  smsCode?: string

  /**
   * 最后一次登录时间
   */
  lastDateTime?: number

  /**
   * 登录次数
   */
  loginTimes?: number

  /**
   * 资源列表
   */
  resourceList?: RmsResource[]

  /**
   * 配置列表
   */
  configureList?: BmsConfigure[]
  /**
   * 登录凭证
   */
  token?: string
}
