package com.framewiki.proxy.server.core;

import com.cdkjframework.constant.IntegerConsts;
import com.cdkjframework.util.log.LogUtils;
import com.framewiki.proxy.server.core.config.ProxyConfig;
import com.framewiki.proxy.server.core.side.server.client.ClientServiceThread;
import com.framewiki.proxy.server.core.side.server.client.config.SecretSimpleClientServiceConfig;
import com.framewiki.proxy.server.core.side.server.client.config.SimpleClientServiceConfig;
import com.framewiki.proxy.server.core.side.server.listen.ListenServerControl;
import com.framewiki.proxy.server.core.side.server.listen.config.impl.AllSecretSimpleListenServerConfig;
import com.framewiki.proxy.server.core.side.server.listen.config.impl.MultControlListenServerConfig;
import com.framewiki.proxy.server.core.side.server.listen.config.impl.SecretSimpleListenServerConfig;
import com.framewiki.proxy.server.core.side.server.listen.config.impl.SimpleListenServerConfig;
import com.framewiki.proxy.server.core.side.server.listen.serversocket.ICreateServerSocket;
import com.framewiki.network.proxy.common.CommonConstants;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;

import static com.framewiki.network.proxy.common.CommonConstants.ListenDest;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.server.core
 * @ClassName: NetworkAgentServer
 * @Description: Network proxy server
 * @Author: frank tiger
 * @Date: 2025/1/3 16:17
 * @Version: 1.0
 */
public class WikiProxyServer {

	/**
	 * log
	 */
	private LogUtils logUtils = LogUtils.getLogger(WikiProxyServer.class);

	/**
	 * configuration
	 */
	private final ProxyConfig proxyConfig;
	/**
	 * Create a server socket
	 */
	public ICreateServerSocket createServerSocket;

	/**
	 * constructor
	 */
	public WikiProxyServer(ProxyConfig proxyConfig) {
		this.proxyConfig = proxyConfig;
	}

	/**
	 * Thread startup
	 */
	public void start() throws Exception {
		// Your p12 format certificate path
		final String sslKeyStorePath = proxyConfig.getSslKeyStorePath();
		// Your certificate password
		final String sslKeyStorePassword = proxyConfig.getSslKeyStorePassword();
		// If HTTPS protocol support is required, fill in sslKeyStorePath, sslKeyStorePassword, or define them in the environment variable
		if (StringUtils.isNoneBlank(sslKeyStorePath, sslKeyStorePassword)) {
			logUtils.info("loading certificate！");
			createServerSocket = listenPort -> {
				KeyStore keystore = KeyStore.getInstance(proxyConfig.getSslKeyStoreType());
				keystore.load(Files.newInputStream(Paths.get(sslKeyStorePath)), sslKeyStorePassword.toCharArray());
				KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(proxyConfig.getAlgorithm());
				keyFactory.init(keystore, sslKeyStorePassword.toCharArray());

				SSLContext ctx = SSLContext.getInstance(proxyConfig.getProtocol());
				ctx.init(keyFactory.getKeyManagers(), null, null);
				SSLServerSocketFactory serverSocketFactory = ctx.getServerSocketFactory();
				return serverSocketFactory.createServerSocket(listenPort);
			};
		}

		int size = proxyConfig.getPort().size();
		// Set constant information
		for (int i = IntegerConsts.ZERO; i < size; i++) {
			int listenPort = proxyConfig.getPort().get(i);
			int destPort = proxyConfig.getDestPort().get(i);
			String destIp = proxyConfig.getIp().get(i);
			CommonConstants.listenDestArray.add(ListenDest.of(listenPort, destIp, destPort));
		}

		// Interactive encryption, also known as interactive verification
		secret();
	}


	/**
	 * Multi client, control channel encryption
	 */
	public void multiControlSecret() throws Exception {
		// Set up and start the client service thread
		SecretSimpleClientServiceConfig config = new SecretSimpleClientServiceConfig(CommonConstants.servicePort);
		// Set interactive AES key and signature key
		config.setBaseAesKey(CommonConstants.aesKey);
		config.setTokenKey(CommonConstants.tokenKey);
		new ClientServiceThread(config).start();

		for (ListenDest model : CommonConstants.listenDestArray) {
			// Set up and start a penetration port
			SecretSimpleListenServerConfig baseListenConfig = new SecretSimpleListenServerConfig(model.listenPort);
			// Set the interactive AES key and signature key, using the same key as the client service. Different keys can be set as needed
			baseListenConfig.setBaseAesKey(CommonConstants.aesKey);
			baseListenConfig.setTokenKey(CommonConstants.tokenKey);
			baseListenConfig.setCreateServerSocket(createServerSocket);

			MultControlListenServerConfig listenConfig = new MultControlListenServerConfig(baseListenConfig);

			ListenServerControl.createNewListenServer(listenConfig);
		}
	}

	/**
	 * Encrypt interactions and tunnels
	 */
	public void secretAll() throws Exception {
		// Set up and start the client service thread
		SecretSimpleClientServiceConfig config = new SecretSimpleClientServiceConfig(CommonConstants.servicePort);
		// Set interactive AES key and signature key
		config.setBaseAesKey(CommonConstants.aesKey);
		config.setTokenKey(CommonConstants.tokenKey);
		new ClientServiceThread(config).start();

		for (ListenDest model : CommonConstants.listenDestArray) {
			AllSecretSimpleListenServerConfig listenConfig = new AllSecretSimpleListenServerConfig(model.listenPort);
			// Set the interactive AES key and signature key, using the same key as the client service. Different keys can be set as needed
			listenConfig.setBaseAesKey(CommonConstants.aesKey);
			listenConfig.setTokenKey(CommonConstants.tokenKey);
			// Set tunnel key
			listenConfig.setBasePasswayKey(CommonConstants.aesKey);
			listenConfig.setCreateServerSocket(createServerSocket);
			ListenServerControl.createNewListenServer(listenConfig);
		}
	}

	/**
	 * Interactive encryption, also known as interactive verification
	 */
	public void secret() throws Exception {
		// Set up and start the client service thread
		SecretSimpleClientServiceConfig config = new SecretSimpleClientServiceConfig(CommonConstants.servicePort);
		// Set interactive AES key and signature key
		config.setBaseAesKey(CommonConstants.aesKey);
		config.setTokenKey(CommonConstants.tokenKey);
		new ClientServiceThread(config).start();

		for (ListenDest model : CommonConstants.listenDestArray) {
			logUtils.info("Start service penetration port：" + model.listenPort);
			// Set up and start a penetration port
			SecretSimpleListenServerConfig listenConfig = new SecretSimpleListenServerConfig(model.listenPort);
			// Set the interactive AES key and signature key, using the same key as the client service. Different keys can be set as needed
			listenConfig.setBaseAesKey(CommonConstants.aesKey);
			listenConfig.setTokenKey(CommonConstants.tokenKey);
			listenConfig.setCreateServerSocket(createServerSocket);
			ListenServerControl.createNewListenServer(listenConfig);
		}
	}

	/**
	 * No encryption, no verification
	 */
	public void simple() throws Exception {
		// Set up and start the client service thread
		SimpleClientServiceConfig config = new SimpleClientServiceConfig(CommonConstants.servicePort);
		new ClientServiceThread(config).start();

		for (ListenDest model : CommonConstants.listenDestArray) {
			// Set up and start a penetration port
			SimpleListenServerConfig listenConfig = new SimpleListenServerConfig(model.listenPort);
			listenConfig.setCreateServerSocket(createServerSocket);
			ListenServerControl.createNewListenServer(listenConfig);
		}
	}
}
