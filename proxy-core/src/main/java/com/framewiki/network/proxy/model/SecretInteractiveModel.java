package com.framewiki.network.proxy.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.framewiki.network.proxy.util.AesUtils;
import com.framewiki.network.proxy.util.Md5Signature;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.model
 * @ClassName: SecretInteractiveModel
 * @Description: 基于InteractiveModel模型的加密交互模型
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SecretInteractiveModel extends InteractiveModel {

	/**
	 * 构造函数
	 *
	 * @param model 待加密的InteractiveModel模型
	 */
	public SecretInteractiveModel(InteractiveModel model) {
		super(model);
	}

	/**
	 * 时间戳
	 */
	private Long timestamp;

	/**
	 * 签名
	 */
	private String autograph;

	/**
	 * InteractiveModel模型jsonString加密值
	 */
	private String encrypt;

	/**
	 * 字符编码
	 */
	private String charset = StandardCharsets.UTF_8.name();

	/**
	 * 加密消息
	 *
	 * @param key
	 * @throws Exception
	 */
	public void encryptMsg(Key key) throws Exception {
		this.encrypt = AesUtils.encryptBase64(key, super.toJSONString().getBytes(this.charset));
	}

	/**
	 * 解密消息
	 *
	 * @param key
	 * @throws Exception
	 */
	public void decryptMsg(Key key) throws Exception {
		byte[] decryptBase64 = AesUtils.decryptBase64(key, this.encrypt);
		String interactiveJsonString = new String(decryptBase64, this.charset);
		InteractiveModel model = JSON.parseObject(interactiveJsonString, InteractiveModel.class);
		super.fullValue(model);
	}

	/**
	 * 签名模型
	 *
	 * @param tokenKey
	 */
	public void autographMsg(String tokenKey) {
		this.autograph = Md5Signature.getSignature(Charset.forName(this.charset), tokenKey, this.timestamp.toString(),
				this.encrypt, this.charset);
	}

	/**
	 * 检查签名
	 *
	 * @param tokenKey
	 * @return
	 */
	public boolean checkAutograph(String tokenKey) {
		String signature = Md5Signature.getSignature(Charset.forName(this.charset), tokenKey, this.timestamp.toString(),
				this.encrypt, this.charset);
		return StringUtils.equals(this.autograph, signature);
	}

	/**
	 * 填充消息
	 *
	 * @param key
	 * @param tokenKey
	 * @throws Exception
	 */
	public void fullMessage(Key key, String tokenKey) throws Exception {
		this.timestamp = System.currentTimeMillis();
		this.encryptMsg(key);
		this.autographMsg(tokenKey);
	}

	/**
	 * 转换为JSONString
	 *
	 * @return 返回JSON字符
	 */
	@Override
	public String toJSONString() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("charset", this.charset);
		jsonObject.put("timestamp", this.timestamp);
		jsonObject.put("encrypt", this.encrypt);
		jsonObject.put("autograph", this.autograph);
		return jsonObject.toJSONString();
	}

}
