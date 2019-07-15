package tonatyw.libra.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
public class Sets {
    public static List<String> profiles = new LinkedList<String>();
    private static List<String> resourcePath = new ArrayList<String>();//Sets.class.getResource("/").getPath();
    private static int CPUNUM = Runtime.getRuntime().availableProcessors();
    private static ExecutorService taskService = Executors.newFixedThreadPool(CPUNUM);
    private static CompletionService<String> completion = new ExecutorCompletionService<String>(taskService);
    private static List<Map<String,Object>> processorList = new LinkedList<Map<String,Object>>();
    private static Map<String,Map<String,Object>> processMap = new HashMap<String,Map<String,Object>>();
    private static Map<String,Map<String,Object>> performanceMap = new HashMap<String,Map<String,Object>>();
    private static Map<String,Object> tasksMap = new HashMap<String,Object>();
    private static ArrayBlockingQueue<Connection> conQueue = new ArrayBlockingQueue<Connection>(CPUNUM);
    private static ExecutorService hbasePool = Executors.newFixedThreadPool(Sets.getCPUNUM());
    static{
        String files = "client.conf, conf.properties, hbasemapping.xml, log4j.properties, logback-spring.xml, performance.xml, processor.xml, task.xml";
        String[] array = files.split(",");
        for(String str:array){
            resourcePath.add(str.trim());
        }
        InputStream is = null;
        BufferedReader br = null;
        try{
            is = Sets.class.getClassLoader().getResourceAsStream(Constants.SystemComponent.DETECT_FILE_NAME);
            br = new BufferedReader(new InputStreamReader(is,"utf-8"));
            String json = br.readLine();
            JSONArray ja = JSON.parseArray(json);
            ja.forEach(obj->{
                profiles.add(((JSONObject)obj).toJSONString());
            });
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(is != null){
                    is.close();
                }
                
                if(br != null){
                    br.close();
                }
            }catch(Exception e1){
                LogUtil.getLogger().error(e1);
            }
        }
    }
    public static int getCPUNUM() {
        return CPUNUM;
    }
    public static void setCPUNUM(int cPUNUM) {
        CPUNUM = cPUNUM;
    }
    public static ExecutorService getTaskService() {
        return taskService;
    }
    public static void setTaskService(ExecutorService taskService) {
        Sets.taskService = taskService;
    }
    public static CompletionService<String> getCompletion() {
        return completion;
    }
    public static void setCompletion(CompletionService<String> completion) {
        Sets.completion = completion;
    }
    public static List<Map<String, Object>> getProcessorList() {
        return processorList;
    }
    public static void setProcessorList(List<Map<String, Object>> processorList) {
        Sets.processorList = processorList;
    }
    public static Map<String, Map<String, Object>> getProcessMap() {
        return processMap;
    }
    public static void setProcessMap(Map<String, Map<String, Object>> processMap) {
        Sets.processMap = processMap;
    }
    public static Map<String, Map<String, Object>> getPerformanceMap() {
        return performanceMap;
    }
    public static void setPerformanceMap(Map<String, Map<String, Object>> performanceMap) {
        Sets.performanceMap = performanceMap;
    }
    public static Map<String, Object> getTasksMap() {
        return tasksMap;
    }
    public static void setTasksMap(Map<String, Object> tasksMap) {
        Sets.tasksMap = tasksMap;
    }
    public static List<String> getResourcePath() {
        return resourcePath;
    }
    public static void setResourcePath(List<String> resourcePath) {
        Sets.resourcePath = resourcePath;
    }
    public static ArrayBlockingQueue<Connection> getConQueue() {
        return conQueue;
    }
    public static void setConQueue(ArrayBlockingQueue<Connection> conQueue) {
        Sets.conQueue = conQueue;
    }
    public static ExecutorService getHbasePool() {
        return hbasePool;
    }
    public static void setHbasePool(ExecutorService hbasePool) {
        Sets.hbasePool = hbasePool;
    }
}
