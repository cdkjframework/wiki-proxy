package com.framewiki.proxy.core.model.interactive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.model.interactive
 * @ClassName: ClientConnectModel
 * @Description: 客户端连接请求
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientConnectModel {

	/**
	 * 隧道标识
	 */
	private String socketPartKey;
}
