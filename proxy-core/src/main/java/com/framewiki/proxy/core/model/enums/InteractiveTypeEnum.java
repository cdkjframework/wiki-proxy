package com.framewiki.proxy.core.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.channel
 * @ClassName: InteractiveTypeEnum
 * @Description: 交互类型enum
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@Getter
public enum InteractiveTypeEnum {
	/**
	 * 默认
	 */
	UNKNOWN("未知"),
	/**
	 * 通用回复标签
	 */
	COMMON_REPLY("通用回复标签"),
	/**
	 * 心跳
	 */
	HEART_TEST("发送心跳"),
	/**
	 * 心跳回复
	 */
	HEART_TEST_REPLY("心跳回复"),
	/**
	 * 服务端等待客户端建立连接
	 */
	SERVER_WAIT_CLIENT("需求客户端建立连接"),
	/**
	 * 客户端建立通道连接
	 */
	CLIENT_CONNECT("客户端建立通道连接"),
	/**
	 * 客户端控制端口建立连接
	 */
	CLIENT_CONTROL("客户端控制端口建立连接");
	/**
	 * 断开连接
	 */
	private final String describe;

	/**
	 * 构造方法
	 *
	 * @param describe 描述
	 */
	InteractiveTypeEnum(String describe) {
		this.describe = describe;
	}

	/**
	 * 根据name获取枚举
	 *
	 * @param name 名称
	 * @return 枚举
	 */
	public static InteractiveTypeEnum getEnumByName(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}
		for (InteractiveTypeEnum e : InteractiveTypeEnum.values()) {
			if (e.name().equals(name)) {
				return e;
			}
		}
		return null;
	}
}
