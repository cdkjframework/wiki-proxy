package com.framewiki.network.proxy.model.enums;

import com.framewiki.network.proxy.model.FrameResultModel;
import org.apache.commons.lang3.StringUtils;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.model.enums
 * @ClassName: FrameResultEnum
 * @Description: 客户端服务端返回码
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public enum FrameResultEnum {
	/**
	 * 成功
	 */
	SUCCESS("1000", "成功"),
	/**
	 * 未知的通信类型
	 */
	UNKNOWN_INTERACTIVE_TYPE("3001", "未知的通信类型"),
	/**
	 * 不存在请求
	 */
	NO_HAS_SERVER_LISTEN("3002", "不存在请求的监听接口"),
	/**
	 * 未知错误
	 */
	FAIL("9999", "未知错误");

	/**
	 * 错误码
	 */
	private final String code;

	/**
	 * 错误信息
	 */
	private final String name;

	/**
	 * 构造方法
	 *
	 * @param code 错误码
	 * @param name 错误信息
	 */
	FrameResultEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	/**
	 * 根据错误码获取枚举
	 *
	 * @param code 错误码
	 * @return 枚举
	 */
	public static FrameResultEnum getEnumByCode(String code) {
		if (StringUtils.isBlank(code)) {
			return null;
		}
		for (FrameResultEnum e : FrameResultEnum.values()) {
			if (e.code.equals(code)) {
				return e;
			}
		}
		return null;
	}

	/**
	 * 转换为返回结果
	 *
	 * @return 返回结果
	 */
	public FrameResultModel toResultModel() {
		return FrameResultModel.of(this);
	}

	/**
	 * 获取错误码
	 *
	 * @return
	 */
	public String getCode() {
		return code;
	}

	/**
	 * 获取错误信息
	 *
	 * @return
	 */
	public String getName() {
		return name;
	}
}
