package com.framewiki.proxy.server.core.side.server.client.handler.impl;

import com.cdkjframework.util.log.LogUtils;
import com.framewiki.network.proxy.channel.impl.BaseSocketChannel;
import com.framewiki.network.proxy.common.Optional;
import com.framewiki.network.proxy.model.InteractiveModel;
import com.framewiki.network.proxy.model.enums.FrameResultEnum;
import com.framewiki.network.proxy.model.enums.InteractiveTypeEnum;
import com.framewiki.proxy.server.core.side.server.client.adapter.impl.PassValueNextEnum;
import com.framewiki.proxy.server.core.side.server.client.handler.PassValueHandler;
import com.framewiki.proxy.server.core.side.server.client.process.IProcess;
import com.framewiki.proxy.server.core.side.server.listen.ServerListenThread;


import java.util.LinkedList;
import java.util.List;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.side.server.client.handler
 * @ClassName: InteractiveIProcessHandler
 * @Description: 常规接收处理handler
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */

public class InteractiveProcessHandler implements PassValueHandler<InteractiveModel, InteractiveModel> {
	/**
	 * 日志
	 */
	private final LogUtils log = LogUtils.getLogger(InteractiveProcessHandler.class);

	/**
	 * 处理链表
	 */
	private List<IProcess> processList = new LinkedList<>();

	/**
	 * 处理消息
	 *
	 * @param baseSocketChannel 交互通道
	 * @param optional      可以重设值
	 * @return 处理结果
	 */
	@Override
	public PassValueNextEnum proc(BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> baseSocketChannel,
																Optional<? extends InteractiveModel> optional) {
		InteractiveModel value = optional.getValue();
		log.info("接收到新消息：[ {} ]", value);

		for (IProcess iProcess : this.processList) {
			boolean wouldProc = iProcess.wouldProc(value);
			if (wouldProc) {
				boolean iProcessMethod;
				try {
					iProcessMethod = iProcess.processMethod(baseSocketChannel, value);
				} catch (Exception e) {
					log.error("处理任务时发生异常", e);
					return PassValueNextEnum.STOP_CLOSE;
				}
				if (iProcessMethod) {
					return PassValueNextEnum.STOP_KEEP;
				} else {
					return PassValueNextEnum.STOP_CLOSE;
				}
			}
		}

		try {
			baseSocketChannel.writeAndFlush(InteractiveModel.of(value.getInteractiveSeq(), InteractiveTypeEnum.COMMON_REPLY,
					FrameResultEnum.UNKNOWN_INTERACTIVE_TYPE.toResultModel()));
		} catch (Exception e) {
			log.error("发送消息时异常", e);
		}

		return PassValueNextEnum.STOP_CLOSE;
	}

	/**
	 * 将处理方法加入后面
	 *
	 * @param iProcess
	 * @return
	 */
	public InteractiveProcessHandler addLast(IProcess iProcess) {
		this.processList.add(iProcess);
		return this;
	}

	/**
	 * 获取处理链表，可以代理维护
	 *
	 * @return
	 */
	public List<IProcess> getProcessList() {
		return this.processList;
	}

	/**
	 * 设置处理链表
	 *
	 * @param processList
	 * @return
	 */
	public InteractiveProcessHandler setProcessList(List<IProcess> processList) {
		this.processList = processList;
		return this;
	}
}
