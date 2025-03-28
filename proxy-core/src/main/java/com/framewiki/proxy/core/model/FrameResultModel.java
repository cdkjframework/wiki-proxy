package com.framewiki.proxy.core.model;

import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONObject;
import com.cdkjframework.util.log.LogUtils;
import com.framewiki.proxy.core.model.enums.FrameResultEnum;
import lombok.Data;


import java.lang.reflect.Field;


/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.model
 * @ClassName: FrameResultModel
 * @Description: 常规类型的前后端返回model
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@Data

public class FrameResultModel implements JSONAware {
	/**
	 * 日志
	 */
	private final LogUtils log = LogUtils.getLogger(FrameResultModel.class);

	/**
	 * 返回码
	 */
	private String code;

	/**
	 * 返回信息
	 */
	private String message;

	/**
	 * 数据
	 */
	private Object data;

	/**
	 * 构造方法
	 *
	 * @param code
	 * @param message
	 * @param data
	 */
	public FrameResultModel(String code, String message, Object data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	/**
	 * 构造方法
	 *
	 * @param code    状态码
	 * @param message 消息
	 * @param object  数据
	 * @return FrameResultModel
	 */
	public static FrameResultModel of(String code, String message, Object object) {
		return new FrameResultModel(code, message, object);
	}

	/**
	 * 构造方法
	 *
	 * @param resultEnum 枚举
	 * @param data       数据
	 * @return FrameResultModel
	 */
	public static FrameResultModel of(FrameResultEnum resultEnum, Object data) {
		return new FrameResultModel(resultEnum.getCode(), resultEnum.getName(), data);
	}

	/**
	 * 构造方法
	 *
	 * @param resultEnum 枚举
	 * @return FrameResultModel
	 */
	public static FrameResultModel of(FrameResultEnum resultEnum) {
		return new FrameResultModel(resultEnum.getCode(), resultEnum.getName(), null);
	}

	/**
	 * 构造方法
	 *
	 * @param data 数据
	 * @return FrameResultModel
	 */
	public static FrameResultModel ofFail(Object data) {
		return new FrameResultModel(FrameResultEnum.FAIL.getCode(), FrameResultEnum.FAIL.getName(), data);
	}

	/**
	 * 构造方法
	 *
	 * @return FrameResultModel
	 */
	public static FrameResultModel ofFail() {
		return new FrameResultModel(FrameResultEnum.FAIL.getCode(), FrameResultEnum.FAIL.getName(), null);
	}

	/**
	 * 构造方法
	 *
	 * @param data 数据
	 * @return FrameResultModel
	 */
	public static FrameResultModel ofSuccess(Object data) {
		return new FrameResultModel(FrameResultEnum.SUCCESS.getCode(), FrameResultEnum.SUCCESS.getName(),
				data);
	}

	/**
	 * 构造方法
	 *
	 * @return FrameResultModel
	 */
	public static FrameResultModel ofSuccess() {
		return new FrameResultModel(FrameResultEnum.SUCCESS.getCode(), FrameResultEnum.SUCCESS.getName(),
				null);
	}

	/**
	 * 反射方式修改值
	 *
	 * @param fieldStr 字段名
	 * @param object   对象
	 * @return FrameResultModel
	 */
	public FrameResultModel set(String fieldStr, Object object) {
		Field field;
		try {
			field = this.getClass().getDeclaredField(fieldStr);
		} catch (NoSuchFieldException | SecurityException e) {
			log.warn(e,"ResultModel获取字段失败！");
			return this;
		}

		field.setAccessible(true);
		try {
			field.set(this, object);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			log.warn(e,"ResultModel获取字段失败！");
			return this;
		}

		return this;
	}

	/**
	 * toString
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return this.toJSONString();
	}

	/**
	 * toJSONString
	 *
	 * @return
	 */
	@Override
	public String toJSONString() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", this.code);
		jsonObject.put("message", this.message);
		jsonObject.put("data", this.data);
		return jsonObject.toJSONString();
	}
}
