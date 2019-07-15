package tonatyw.libra.rabbitmq.base;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import tonatyw.libra.util.Constants;

public abstract class BaseRabbitConsumer extends BaseConnection{
    private Consumer consumer;
    private CallBack callback;
    private String queueName;
    private boolean autoAck;
    private long deliveryTag;
    public void basicConsume()throws Exception{
        channel.basicConsume(this.queueName,this.autoAck,this.consumer);
    }
    public void basicAck()throws Exception{
        channel.basicAck(deliveryTag, false);
    }
    public void basicAck(long deliveryTagIn)throws Exception{
        channel.basicAck(deliveryTagIn, false);
    }
    @Override
    public void setParams(Map<String,Object> params)throws Exception{
        consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       com.rabbitmq.client.AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                callback(consumerTag, envelope, properties, body);
            };
        };
        
        if(params.containsKey(Constants.RabbitParams.QUEUE_NAME)){
            this.queueName = (String)params.get(Constants.RabbitParams.QUEUE_NAME);
            channel.queueDeclare(this.queueName, queueDurable, exclusive, false, null);
        }
        
        if(params.containsKey(Constants.RabbitParams.EXCHANGE_NAME)){
            String bindQueue = null;
            channel.exchangeDeclare((String)params.get(Constants.RabbitParams.EXCHANGE_NAME),(String)params.get(Constants.RabbitParams.RABBIT_TYPE),exchangeDurable);  
            if(StringUtils.isEmpty(this.queueName)){
                DeclareOk dos = channel.queueDeclare("", queueDurable, exclusive, false, null);
                bindQueue = dos.getQueue();
            }else{
                bindQueue = this.queueName;
            }
            channel.queueBind(this.queueName, (String)params.get(Constants.RabbitParams.EXCHANGE_NAME), (String)params.get(Constants.RabbitParams.ROUTING_KEY));
        }
        
        if(params.containsKey(Constants.RabbitParams.QOS)){
            channel.basicQos(Integer.parseInt((String)params.get(Constants.RabbitParams.QOS)));
        }
        
        this.autoAck = Boolean.valueOf((String)params.get(Constants.RabbitParams.AUTO_ACK));
    }
    
    public void callback(String consumerTag, Envelope envelope, 
                         com.rabbitmq.client.AMQP.BasicProperties properties,
                         byte[] body){
        if(callback!=null){
            callback.callback(consumerTag, envelope, properties, body);
        }else{
            System.out.println("hello world");
        }
    }
    public void setCallback(CallBack callback){
        this.callback = callback;
    }
    
    public interface CallBack {
        void callback(String consumerTag, Envelope envelope, 
                      com.rabbitmq.client.AMQP.BasicProperties properties,
                      byte[] body);
    }
    public BaseRabbitConsumer(){
        super();
    }
    public long getDeliveryTag() {
        return deliveryTag;
    }
    public void setDeliveryTag(long deliveryTag) {
        this.deliveryTag = deliveryTag;
    }
}
