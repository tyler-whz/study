package com.topinfo.exchange.net;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import com.topinfo.plane.common.DateUtil;
import com.topinfo.plane.net.msg.GpsData;
import com.topinfo.plane.net.msg.Message;
import com.topinfo.plane.net.msg.MessageBean;
import com.topinfo.plane.net.request.IRequest;
import com.topinfo.plane.net.request.RequestEnum;
import com.topinfo.plane.redis.RedisClient;


/**
  * @ClassName: NettyServerHandler
  * @Description: TODO
  * @author tyler.wu-whz
  * @date 2013-5-8 上午9:46:28
  *
  */
public class NettyServerHandler extends SimpleChannelUpstreamHandler {

	private static final Log LOGGER = LogFactory.getLog(NettyServerHandler.class);
	
	/**
	  * @Fields threadpool : 业务处理主线城池
	  */
	private ExecutorService threadpool;
	
	private RedisClient redisClient;
	
	public NettyServerHandler(ExecutorService threadpool,RedisClient redisClient){
		this.threadpool = threadpool;
		this.redisClient = redisClient;
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
		throws Exception {
		if(!(e.getCause() instanceof IOException)){
			// only log
			LOGGER.error("catch some exception not IOException",e.getCause());
		}
	}
	
	/*
	  * <p>Title: channelConnected</p>
	  * <p>Description: </p>
	  * @param ctx
	  * @param e
	  * @throws Exception
	  * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelConnected(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
	  */
	
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		// TODO Auto-generated method stub
		super.channelConnected(ctx, e);	
		System.out.println(ctx.getChannel().getLocalAddress());
	}
	
	public void messageReceived(final ChannelHandlerContext ctx, MessageEvent e)
		throws Exception {
		Object message = e.getMessage();
		LOGGER.warn("接收数据"+message.toString());
		System.out.println("接收数据"+message.getClass());
		//接收消息处理
		handleRequest(ctx, message);
	}
	
	@SuppressWarnings("unchecked")
	private void handleRequest(final ChannelHandlerContext ctx,final Object message) {
		try {
			threadpool.execute(new HandlerRunnable(ctx,(Message)message,redisClient));
		} catch (RejectedExecutionException exception) {
			LOGGER.error("server threadpool full,threadpool maxsize is:"
					+ ((ThreadPoolExecutor) threadpool).getMaximumPoolSize());
		}
	}

	
	class HandlerRunnable implements Runnable{

		private ChannelHandlerContext ctx;
		
		private Object message;
				
		private RedisClient redisClient;
		
		public HandlerRunnable(ChannelHandlerContext ctx,Object message,RedisClient redisClient){
			this.ctx = ctx;
			this.message = message;
			this.redisClient = redisClient ;
		}
		
		@SuppressWarnings("rawtypes")
		public void run() {
			IRequest request = RequestEnum.getRequestCmd(message.toString());
			try {
				request.request(ctx,(MessageBean)message,redisClient);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
}
