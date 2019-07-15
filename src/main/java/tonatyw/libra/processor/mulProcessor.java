package tonatyw.libra.processor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class mulProcessor implements Callable<String>{
    private String body;
    private List<Map<String,Object>> performanceList;
    private String classPre;
    public mulProcessor(String body,List<Map<String,Object>> performanceList,String classPre){
        this.body = body;
        this.performanceList = performanceList;
        this.classPre = classPre;
    }
    @Override
    public String call() throws Exception {
        Processor p = new Processor(body, performanceList, classPre);
        return p.doProcess();
    }
}
