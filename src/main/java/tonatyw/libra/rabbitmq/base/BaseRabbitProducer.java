package tonatyw.libra.rabbitmq.base;

import java.util.Map;

import com.rabbitmq.client.AMQP;

import tonatyw.libra.util.Constants;

public abstract class BaseRabbitProducer extends BaseConnection{
    private String exchangeName;
    private String routingKey;
    public BaseRabbitProducer() {
        super();
    }
    public void sendMessage(String bodyStr){
        byte[] body = bodyStr.getBytes();
        try{
        channel.basicPublish(this.exchangeName, this.routingKey, 
            new AMQP.BasicProperties.Builder()
            .contentType(this.contentType)
            .deliveryMode(this.deliveryMode)
            .priority(this.priority)
//            .userId("bob")
            .build(),body);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void setParams(Map<String,Object> params)throws Exception{
        if(params.containsKey(Constants.RabbitParams.QUEUE_NAME)){
            channel.queueDeclare((String)params.get(Constants.RabbitParams.QUEUE_NAME), queueDurable, exclusive, false, null);
        }
        
        if(params.containsKey(Constants.RabbitParams.EXCHANGE_NAME)){
            this.exchangeName = (String)params.get(Constants.RabbitParams.EXCHANGE_NAME);
            channel.exchangeDeclare(this.exchangeName, (String)params.get(Constants.RabbitParams.RABBIT_TYPE),exchangeDurable);
        }
        
        if(params.containsKey(Constants.RabbitParams.ROUTING_KEY)){
            this.routingKey = ((String)params.get(Constants.RabbitParams.ROUTING_KEY)).concat((String)(params.get(Constants.Param.TABLE_NAME)));
            this.routingKey = this.routingKey.toLowerCase();
        }
    }
}
