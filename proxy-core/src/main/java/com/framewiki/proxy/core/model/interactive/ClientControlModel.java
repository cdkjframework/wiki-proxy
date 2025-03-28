package com.framewiki.proxy.core.model.interactive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.model.interactive
 * @ClassName: ClientControlModel
 * @Description: 请求建立控制器模型
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientControlModel {

	/**
	 * 监听端口
	 */
	private Integer listenPort;
}
