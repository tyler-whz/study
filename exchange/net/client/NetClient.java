package com.topinfo.exchange.net.client;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;

import com.topinfo.plane.net.NamedThreadFactory;
import com.topinfo.plane.net.msg.GpsData;
import com.topinfo.plane.net.msg.OtherData;
import com.topinfo.plane.net.serialize.MessageBeanUtil;
import com.topinfo.plane.net.serialize.NettyProtocolDecoder;
import com.topinfo.plane.net.serialize.NettyProtocolEncoder;


/**
 * @Title: NetClient.java
 * @Package 
 * @Description: TODO
 * Company:图讯科技
 * @author Comsys-whz
 * @date 2013-5-20 下午7:00:51
 * @version V1.0
 */

/**
 * @ClassName: NetClient
 * @Description: TODO
 * @author tyler.wu-whz
 * @date 2013-5-20 下午7:00:51
 *
 */

public class NetClient {
	static ChannelFuture future;
	static ClientBootstrap bootstrap;
	static void init() {
	     bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));
		bootstrap.setOption("tcpNoDelay", Boolean.parseBoolean(System
				.getProperty("nfs.rpc.tcp.nodelay", "true")));
		bootstrap.setOption("reuseAddress", Boolean.parseBoolean(System
				.getProperty("nfs.rpc.tcp.reuseaddress", "true")));
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = new DefaultChannelPipeline();
				pipeline.addLast("lengthdecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 2,0,2));
				pipeline.addLast("decoder", new NettyProtocolDecoder());
				pipeline.addLast("lengthencoder", new LengthFieldPrepender(2));
				pipeline.addLast("encoder", new NettyProtocolEncoder());
				pipeline.addLast("handler", new NettyClientHandler());
				return pipeline;
			}
		});
      future = bootstrap.connect(new InetSocketAddress("127.0.0.1", 10001));

	}
	
	public static void main(String[] args) {
		init();
		GpsData data = new GpsData();
	  	data.setCmd("gps");
		data.setCmdLine("2222w");
		data.setLat(30.111);
		data.setLon(120.111);
		data.setStatus(1);
		data.setTime(new Date().getTime());
		future.getChannel().write(data);
		// future.getChannel().getCloseFuture().awaitUninterruptibly();
		// future.addListener(ChannelFutureListener.CLOSE);
		// bootstrap.releaseExternalResources();
		future.addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				// TODO Auto-generated method stub
				System.out.println("write end");
			}
		});
		future.getChannel().getCloseFuture().awaitUninterruptibly();
		bootstrap.releaseExternalResources();
	}
}
