package tonatyw.libra.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import tonatyw.libra.performance.base.Performance;
import tonatyw.libra.util.LogUtil;
import tonatyw.libra.util.SystemUtil;

public class Processor {
    private String body;
    private List<Map<String,Object>> performanceList;
    private String classPre;
    public Processor(String body,List<Map<String,Object>> performanceList,String classPre){
        this.body = body;
        this.performanceList = performanceList;
        this.classPre = classPre;
    }
    public String doProcess(){
        for(Map<String,Object> performanceTmp:performanceList){
            try{
                Map<String,Object> performance = SystemUtil.clone(performanceTmp);
                Class performanceClass = Class.forName(classPre.concat((String)performance.get("class")));
                Performance p = (Performance) performanceClass.newInstance();
                Map<String,Object> bodyMap = JSON.parseObject(body,HashMap.class);
                performance.putAll(bodyMap);
                body = JSON.toJSONString(p.deal(performance));
                if(body==null){
                    return "";
                }
                //new Object[]{performance,body}
            }catch (Exception e){
                LogUtil.getLogger().error(e.getMessage());
                e.printStackTrace();
                return "";
            }
        }
        return body;
    }
}
