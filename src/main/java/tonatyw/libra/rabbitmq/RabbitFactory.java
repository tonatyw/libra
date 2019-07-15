package tonatyw.libra.rabbitmq;

import tonatyw.libra.conf.CenterConfig;
import tonatyw.libra.rabbitmq.base.BaseRabbitConsumer;
import tonatyw.libra.rabbitmq.base.BaseRabbitProducer;
import tonatyw.libra.util.Constants;

public class RabbitFactory {
    public static BaseRabbitConsumer getRabbitConsumer(String rabbitType)throws Exception{
        //根据rabbitType获取子类
        String baseRabbitClass = CenterConfig.getConfsMap().get(Constants.ConfigKey.BASE_RABBIT_CLASS);
        String rabbitClass = CenterConfig.getConfsMap().get(rabbitType.concat(Constants.SystemComponent.CONSUMER));
        Class c = Class.forName(baseRabbitClass.concat(rabbitClass));
        BaseRabbitConsumer br = (BaseRabbitConsumer) c.newInstance();
        return br;
    }
    public static BaseRabbitProducer getRabbitProducer(String rabbitType)throws Exception{
        //根据rabbitType获取子类
        String baseRabbitClass = CenterConfig.getConfsMap().get(Constants.ConfigKey.BASE_RABBIT_CLASS);
        String rabbitClass = CenterConfig.getConfsMap().get(rabbitType.concat(Constants.SystemComponent.PRODUCER));
        Class c = Class.forName(baseRabbitClass.concat(rabbitClass));
        BaseRabbitProducer br = (BaseRabbitProducer) c.newInstance();
        return br;
    }
}
