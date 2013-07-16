package com.topinfo.exchange.net;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
/**
  * @ClassName: RegisterClientMap
  * @Description: TODO  客户端连接注册容器，便于推送消息
  * @author tyler.wu-whz
  * @date 2013-5-23 下午1:06:43
  *
  */
public class RegisterClientMap {
	
	 /**
	 * 
	 */
	private static Logger m_logger = Logger.getLogger(RegisterClientMap.class);
	
    private static  RegisterClientMap  instance = new RegisterClientMap();
    
    private ConcurrentMap<String, Channel> userClientMap = new ConcurrentHashMap<String, Channel>();
    
    public static RegisterClientMap getInstance() {
        if (instance == null) {
            instance = new RegisterClientMap();
        }
        return instance;
    }
    
    public Channel getUserClientChannel(String  terminalId){
    	return userClientMap.get(terminalId);
    }
	
    
    
    /**
      * registerUserClientChannel(注册终端)
      * @Title: registerUserClientChannel
      * @Description: TODO
      * @param @param terminalId
      * @param @param ctx
      * @param @return    设定文件
      * @return Channel    返回类型
      * @throws
      */
    
    
    public Channel registerUserClientChannel(String  terminalId,Channel ctx){
    	Channel old = getUserClientChannel(terminalId);
    	if(old ==null||(old !=null&&ctx.isConnected()==false)){
           userClientMap.putIfAbsent(terminalId,ctx);
    	}
    	return  ctx;
    }
    
    public Channel moveClientChannel(String terminalId){
    	return userClientMap.remove(terminalId);
    }
	 
    
}
