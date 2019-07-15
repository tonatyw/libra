package tonatyw.libra.performance.impl;

import java.util.Map;

import com.alibaba.fastjson.JSON;

import tonatyw.libra.performance.base.Performance;
import tonatyw.libra.rabbitmq.RabbitFactory;
import tonatyw.libra.rabbitmq.base.BaseRabbitProducer;
import tonatyw.libra.util.Constants;
import tonatyw.libra.util.LogUtil;

public class SendMQ implements Performance{

    @Override
    public Map<String,Object> deal(Map<String, Object> map) throws Exception{
        map.put("table_name", ((String)map.get("table_name")).toLowerCase());
        //将信息发送到rabbitMq
        BaseRabbitProducer brp = null;
        try {
            brp = RabbitFactory.getRabbitProducer((String)map.get(Constants.RabbitParams.RABBIT_TYPE));
            LogUtil.getLogger().info("send:"+JSON.toJSONString(map));
            brp.init(map);
            brp.sendMessage(JSON.toJSONString(map));
        } catch (Exception e) {
            LogUtil.getLogger().error(e.getMessage());
            e.printStackTrace();
            return null;
        }finally{
            if(brp!=null){
                brp.close();
            }
        }
        return map;
    }

}
