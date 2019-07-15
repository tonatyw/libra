package tonatyw.libra.scheduler;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Envelope;

import tonatyw.libra.conf.CenterConfig;
import tonatyw.libra.rabbitmq.RabbitFactory;
import tonatyw.libra.rabbitmq.base.BaseRabbitConsumer;
import tonatyw.libra.rabbitmq.base.BaseRabbitConsumer.CallBack;
import tonatyw.libra.task.Task;
import tonatyw.libra.util.Constants;
import tonatyw.libra.util.Sets;
import tonatyw.libra.util.SystemUtil;

@Service(value="scheduler")
public class Scheduler {
    public void init() {
        //取出task文件内容
        List<Map<String,Object>> processorList = (List<Map<String, Object>>) CenterConfig.getXmlsMap().get(Constants.SystemComponent.PROCESSOR).get(Constants.SystemComponent.PROCESSOR);
        //取出task文件内容
        List<Map<String,Object>> performanceList = (List<Map<String, Object>>) CenterConfig.getXmlsMap().get(Constants.SystemComponent.PERFORMANCE).get(Constants.SystemComponent.PERFORMANCE);
        
        SystemUtil.initProPerMap(processorList, performanceList);
        SystemUtil.initProcessList();
    }
    public void start(){
        //取出task文件内容
        Map<String,Object> taskMap = CenterConfig.getXmlsMap().get(Constants.SystemComponent.TASK);
        //取出task集合
        List<Map<String,Object>> taskList = (List<Map<String, Object>>) taskMap.get(Constants.SystemComponent.TASK);
        //获取重装好的tasksMap
        Map<String,Object> tasksMap = Sets.getTasksMap();
        final String classPre = CenterConfig.getConfsMap().get(Constants.ConfigKey.CLASS_PRE);
        //循环开启rabbitmq监听
        for(Map<String,Object> task:taskList){
            //处理数据
            List<Map<String,Object>> processorListM = (List<Map<String, Object>>) ((Map<String,Object>)tasksMap.get(task.get(Constants.TaskParams.NAME))).get(Constants.SystemComponent.PROCESSOR);
            //复制一个新的list
            List<Map<String,Object>> processorListC = SystemUtil.clone(processorListM);
            try{
                BaseRabbitConsumer brc =RabbitFactory.getRabbitConsumer((String)task.get(Constants.RabbitParams.RABBIT_TYPE));
                brc.init(task);
                CallBack cb = new CallBack() {
                    @Override
                    public void callback(String consumerTag, Envelope envelope, BasicProperties properties,
                                         byte[] body) {
                        try{
                            String revBody = new String(body,Constants.RabbitParams.ENCODING);
                            System.out.println(revBody);
                            Sets.getCompletion().submit(new Task(processorListC, revBody, classPre,brc,envelope.getDeliveryTag()));
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                brc.setCallback(cb);
                brc.basicConsume();
                while(true){
                    String str = Sets.getCompletion().take().get();
                    System.out.println("!!! "+str);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        System.out.println("初始化完成");
    }
    @PostConstruct
    public void test() throws Exception {
        init();
        start();
    }
    public static void main(String[] args) {
        Scheduler s = new Scheduler();
        s.init();
        s.start();
    }
}
