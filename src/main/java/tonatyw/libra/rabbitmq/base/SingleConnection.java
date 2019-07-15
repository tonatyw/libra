package tonatyw.libra.rabbitmq.base;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import tonatyw.libra.conf.CenterConfig;
import tonatyw.libra.util.Constants;
import tonatyw.libra.util.LogUtil;

public class SingleConnection {
    private static class ConnectionBuilder{
        private static Connection connection;
        static{
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
            try {
                connection = factory.newConnection();
            } catch (IOException | TimeoutException e) {
                LogUtil.getLogger().error(e);
            }
        }
    }
    
    public static Connection getInstance(){
        return ConnectionBuilder.connection;
        
    }
}
