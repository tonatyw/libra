package tonatyw.libra.task;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import tonatyw.libra.conf.CenterConfig;
import tonatyw.libra.processor.Processor;
import tonatyw.libra.processor.mulProcessor;
import tonatyw.libra.rabbitmq.base.BaseRabbitConsumer;
import tonatyw.libra.util.Constants;
import tonatyw.libra.util.LogUtil;
import tonatyw.libra.util.SystemUtil;

public class Task implements Callable<String>{
    private List<Map<String,Object>> processorList;
    private String body;
    private String classPre;
    private BaseRabbitConsumer brc;
    private long deliveryTag;
    public Task(List<Map<String,Object>> processorList,String body,String classPre,BaseRabbitConsumer brc,long deliveryTag){
        this.processorList = processorList;
        this.body = body;
        this.classPre = classPre;
        if(StringUtils.isEmpty(classPre)){
            this.classPre = CenterConfig.getConfsMap().get(Constants.ConfigKey.CLASS_PRE);
        }
        this.deliveryTag = deliveryTag;
    }
    @Override
    public String call() throws Exception {
        try{
            for(Map<String,Object> processorTmp:processorList){
                Map<String,Object> processor = SystemUtil.clone(processorTmp);
                if(processor.containsKey(Constants.SystemComponent.PROCESSOR)){//并行执行
                    List<Map<String,Object>> mulProcessorList = (List<Map<String,Object>>)processor.get(Constants.SystemComponent.PROCESSOR);
                    //实例一个临时线程组
                    ExecutorService proService = Executors.newFixedThreadPool(mulProcessorList.size());
                    CompletionService<String> completion = new ExecutorCompletionService<String>(proService);
                    mulProcessorList.forEach(mulProcessor->{
                        List<Map<String,Object>> performanceListTmp = (List<Map<String, Object>>) mulProcessor.get(Constants.SystemComponent.PERFORMANCE);
                        List<Map<String,Object>> performanceList = SystemUtil.clone(performanceListTmp);
                        completion.submit(new mulProcessor(body, performanceList, classPre));
                    });
                    try{
                        JSONObject json = JSON.parseObject(body);
                        for(int i=0;i<mulProcessorList.size();i++){
                            String tmpBody = completion.take().get();
                            JSONObject tmpJson = JSON.parseObject(tmpBody);
                            json.putAll(tmpJson);
                        }
                        //更新body以便后面调用
                        body = JSON.toJSONString(json);
                    }catch(Exception e){
                        LogUtil.getLogger().error(e.getMessage());
                        e.printStackTrace();
                    }
                }else{
                    List<Map<String,Object>> performanceListTmp = (List<Map<String, Object>>) processor.get(Constants.SystemComponent.PERFORMANCE);
                    List<Map<String,Object>> performanceList = SystemUtil.clone(performanceListTmp);
                    Processor p = new Processor(body, performanceList, classPre);
                    body = p.doProcess();
                }
                if(StringUtils.equals("null", body) || StringUtils.isEmpty(body) || body==null){
                    System.out.println(deliveryTag);
                    this.brc.basicAck(deliveryTag);
                    return "";
                }
            }
            System.out.println(deliveryTag);
            this.brc.basicAck(deliveryTag);
//            this.brc.basicConsume();
            return body;
        }catch(Exception e){
            this.brc.basicAck(deliveryTag);
            LogUtil.getLogger().error(e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
}
