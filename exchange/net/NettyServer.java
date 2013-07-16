/**
 * @Title: NettyServer.java
 * @Package com.topinfo.plane.net.netty
 * @Description: TODO
 * Company:图讯科技
 * @author Comsys-whz
 * @date 2013-5-8 上午9:06:58
 * @version V1.0
 */

package com.topinfo.exchange.net;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;

import com.topinfo.plane.net.NamedThreadFactory;
import com.topinfo.plane.net.Server;
import com.topinfo.plane.net.serialize.NettyProtocolDecoder;
import com.topinfo.plane.net.serialize.NettyProtocolEncoder;
import com.topinfo.plane.redis.RedisClient;


/**
 * 网络服务核心类
 * @ClassName: NettyServer
 * @Description: TODO
 * @author tyler.wu-whz
 * @date 2013-5-8 上午9:06:58
 *
 */

public class NettyServer implements Server {
	
	private static final Log LOGGER = LogFactory.getLog(NettyServer.class);
	
	private ServerBootstrap bootstrap = null;

	private AtomicBoolean startFlag = new AtomicBoolean(false);
	
	/**
	  * @Fields redisClient : 缓存包装器
	  */
	private RedisClient redisClient;
	
	/**
	 * setter method
	 * @param redisClient the redisClient to set
	 */
	
	public void setRedisClient(RedisClient redisClient) {
		this.redisClient = redisClient;
	}

	public NettyServer() {
		
		ThreadFactory serverBossTF = new NamedThreadFactory("NETTYSERVER-BOSS-");
		ThreadFactory serverWorkerTF = new NamedThreadFactory("NETTYSERVER-WORKER-");
		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(serverBossTF),
				Executors.newCachedThreadPool(serverWorkerTF)));
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("reuseAddress", true);
	}

	public void start(int listenPort, final ExecutorService threadPool) throws Exception {
		if(!startFlag.compareAndSet(false, true)){
			return;
		}

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = new DefaultChannelPipeline();
				pipeline.addLast("lengthdecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 2,0,2));
				pipeline.addLast("decoder", new NettyProtocolDecoder());
				pipeline.addLast("lengthencoder", new LengthFieldPrepender(2));
				pipeline.addLast("encoder", new NettyProtocolEncoder());
				pipeline.addLast("handler", new NettyServerHandler(threadPool,redisClient));
				return pipeline;
			}
		});
		bootstrap.bind(new InetSocketAddress(listenPort));
		LOGGER.warn("Server started,listen at: "+listenPort);
	}

	public void stop() throws Exception {
		LOGGER.warn("Server stop!");
		bootstrap.releaseExternalResources();
		startFlag.set(false);
	}

}
