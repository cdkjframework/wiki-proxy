package com.framewiki.network.proxy.model.interactive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.model.interactive
 * @ClassName: ServerWaitModel
 * @Description: 服务端等待建立隧道模型
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerWaitModel {

	/**
	 * 隧道标识
	 */
	private String socketPartKey;
}
