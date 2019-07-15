package tonatyw.libra.rabbitmq.base;

import java.util.Map;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import tonatyw.libra.conf.CenterConfig;
import tonatyw.libra.util.Constants;
import tonatyw.libra.util.LogUtil;

public abstract class BaseConnection {
    protected Channel channel;
    protected Connection connection;
    protected boolean exclusive = false;
    protected boolean queueDurable = false;
    protected boolean exchangeDurable = false;
    protected int deliveryMode = 1;
    protected int priority = 9;
    protected String contentType = "text/plain";
    public BaseConnection() {
        createConnection();
    }

    public void createConnection(){
        try{
            connection = SingleConnection.getInstance();
            this.channel = connection.createChannel();
        }catch(Exception e){
            LogUtil.getLogger().error(e);
        }
    }
    public void close(){
        try {
            channel.close();
//            connection.close();
        } catch (Exception e) {
            LogUtil.getLogger().error(e);
        }
    }
    public void init(Map<String,Object> params)throws Exception{
        if(params.containsKey(Constants.RabbitParams.EXCLUSIVE)){
            this.exclusive =  Boolean.valueOf((String)params.get(Constants.RabbitParams.EXCLUSIVE));
        }
        
        if(params.containsKey(Constants.RabbitParams.QUEUE_DURABLE)){
            this.queueDurable =  Boolean.valueOf((String)params.get(Constants.RabbitParams.QUEUE_DURABLE));
        }
        
        if(params.containsKey(Constants.RabbitParams.EXCHANGE_DURABLE)){
            this.exchangeDurable =  Boolean.valueOf((String)params.get(Constants.RabbitParams.EXCHANGE_DURABLE));
        }
        
        if(params.containsKey(Constants.RabbitParams.DELIVERY_MODE)){
            this.deliveryMode =  Integer.parseInt((String)params.get(Constants.RabbitParams.DELIVERY_MODE));
        }
        
        if(params.containsKey(Constants.RabbitParams.PRIORITY)){
            this.priority =  Integer.parseInt((String)params.get(Constants.RabbitParams.PRIORITY));
        }
        
        if(params.containsKey(Constants.RabbitParams.CONTENT_TYPE)){
            this.contentType =  (String)params.get(Constants.RabbitParams.CONTENT_TYPE);
        }
        
        setParams(params);
    }
    public abstract void setParams(Map<String,Object> map)throws Exception;
    
    public static void main(String[] args) {
        try{
            //定义连接工厂
            ConnectionFactory factory = new ConnectionFactory();
            //设置10s尝试恢复一次
            factory.setNetworkRecoveryInterval(Long.valueOf(CenterConfig.getConfsMap().get(Constants.ConfigKey.RABBIT_NETWORK_RECOVERY_INTERVAL)));
            //设置服务地址
            factory.setHost(CenterConfig.getConfsMap().get(Constants.ConfigKey.RABBIT_HOST));
            //端口
            factory.setPort(Integer.parseInt(CenterConfig.getConfsMap().get(Constants.ConfigKey.RABBIT_PORT)));
            //设置账号信息，用户名、密码、vhost
            factory.setUsername(CenterConfig.getConfsMap().get(Constants.ConfigKey.RABBIT_USER_NAME));
            factory.setPassword(CenterConfig.getConfsMap().get(Constants.ConfigKey.RABBIT_PASSWORD));
            // 通过工程获取连接
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
