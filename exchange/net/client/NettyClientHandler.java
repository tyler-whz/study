package com.topinfo.exchange.net.client;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * @Title: NettyClientHandler.java
 * @Package 
 * @Description: TODO
 * Company:图讯科技
 * @author Comsys-whz
 * @date 2013-5-23 上午9:23:01
 * @version V1.0
 */

/**
 * @ClassName: NettyClientHandler
 * @Description: TODO
 * @author tyler.wu-whz
 * @date 2013-5-23 上午9:23:01
 *
 */

public class NettyClientHandler extends SimpleChannelUpstreamHandler {
	
	/*
	  * <p>Title: messageReceived</p>
	  * <p>Description: </p>
	  * @param ctx
	  * @param e
	  * @throws Exception
	  * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
	  */
	
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		// TODO Auto-generated method stub
		super.messageReceived(ctx, e);
		System.out.println("client" + e.getMessage());
	}

}
