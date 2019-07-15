package tonatyw.libra.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

import tonatyw.libra.conf.CenterConfig;

public class SystemUtil {
    static{
        try {
            DetectorFactory.loadProfile(Sets.profiles);
        } catch (LangDetectException e) {
            LogUtil.getLogger().error(e);
        }
    }
    public static List<String> getFiles(List<String> resourcePathList,String postfix){
        List<String> fileList = new LinkedList<String>();
//        File resource = new File(resourcePath);
//        File[] files = resource.listFiles();
//        for(File file:files){
//            if(file.getName().endsWith(postfix)){
//                fileList.add(file.getAbsolutePath());
//            }
//        }
        //采用流形式读取方式
        resourcePathList.forEach(resource->{
            if(resource.indexOf(".".concat(postfix)) != -1){
                fileList.add("/".concat(resource));
            }
        });
        return fileList;
    }
    
    public static void initProPerMap(List<Map<String,Object>> processorList, List<Map<String,Object>> performanceList){
        Map<String,Map<String,Object>> proMap = new HashMap<String,Map<String,Object>>();
        for(Map<String,Object> map:processorList){
            proMap.put((String)map.get(Constants.ProcessorParams.NAME), map);
        }
        Map<String,Map<String,Object>> perMap = new HashMap<String,Map<String,Object>>();
        for(Map<String,Object> map:performanceList){
            perMap.put((String)map.get(Constants.PerformanceParams.NAME), map);
        }
        Sets.setProcessMap(proMap);
        Sets.setPerformanceMap(perMap);
    }
    public static void initProcessList(){
        List<Map<String,Object>> taskList = (List<Map<String, Object>>) CenterConfig.getXmlsMap().get(Constants.SystemComponent.TASK).get(Constants.SystemComponent.TASK);
        List<Map<String,Object>> processorList = (List<Map<String, Object>>) CenterConfig.getXmlsMap().get(Constants.SystemComponent.PROCESSOR).get(Constants.SystemComponent.PROCESSOR);
        List<Map<String,Object>> performanceList = (List<Map<String, Object>>) CenterConfig.getXmlsMap().get(Constants.SystemComponent.PERFORMANCE).get(Constants.SystemComponent.PERFORMANCE);
        Map<String,Object> tasksMap = new HashMap<String,Object>();
        for(Map<String,Object> task:taskList){
            Map<String,Object> taskMap = new HashMap<String,Object>();
            List<Map<String,Object>> proList = new LinkedList<Map<String,Object>>();
            //判断有没有multiple
            if(task.containsKey("multiple")){//需要并行
                List<Map<String,Object>> tpList = (List<Map<String,Object>>)task.get(Constants.TaskParams.MULTIPLE);
                for(Map<String,Object> p:tpList){
                    if(p.containsKey(Constants.SystemComponent.PROCESSOR)){
                        Map<String,Object> multiMap = new HashMap<String,Object>();
                        List<Map<String,Object>> tmpProList = new LinkedList<Map<String,Object>>();
                        for(Map<String,Object> pp:(List<Map<String,Object>>)p.get(Constants.SystemComponent.PROCESSOR)){
                            putList(tmpProList,pp);
                        }
                        multiMap.put(Constants.SystemComponent.PROCESSOR, tmpProList);
                        proList.add(multiMap);
                    }else{
                        putList(proList,p);
                    }
                }
            }else{
                List<Map<String,Object>> tpList = (List<Map<String,Object>>)task.get(Constants.SystemComponent.PROCESSOR);
                for(Map<String,Object> p:tpList){
                	System.out.println(p);
                    putList(proList,p);
                }
            }
            taskMap.put(Constants.SystemComponent.PROCESSOR, proList);
            tasksMap.put((String)task.get(Constants.TaskParams.NAME), taskMap);
        }
        Sets.setTasksMap(tasksMap);
    }
    
    public static void putList(List<Map<String,Object>> tmpList,Map<String,Object> p){
        Map<String,Object> proMap = Sets.getProcessMap().get(p.get(Constants.ProcessorParams.NAME));
        Map<String,Object> proMapTmp = new HashMap<String,Object>();
        List<Map<String,Object>> perList = (List<Map<String,Object>>)proMap.get(Constants.SystemComponent.PERFORMANCE);
        List<Map<String,Object>> perListTmp = new LinkedList<Map<String,Object>>();
        for(Map<String,Object> pe:perList){
            Map<String,Object> perMap = Sets.getPerformanceMap().get(pe.get(Constants.PerformanceParams.NAME));
            perListTmp.add(perMap);
        }
        proMapTmp.put(Constants.SystemComponent.PERFORMANCE, perListTmp);
        tmpList.add(proMapTmp);
    }
    
    public static <T> T clone(Object source){
        String tmpStr = JSON.toJSONString(source);
        return (T) JSON.parseObject(tmpStr,source.getClass());
    }
    
    public static Map<String,Object> findReg(Map<String,Object> map,String reg){
        Map<String,Object> urlMap = new HashMap<String,Object>();
        Pattern p = Pattern.compile(reg);
        for(String key:map.keySet()){
            Matcher m = p.matcher(key);
            if(m.find()){
                if(map.get(key)!=null && StringUtils.isNotEmpty((String)map.get(key))){
                    urlMap.put(key,map.get(key));
                }
            }
        }
        return urlMap;
    }
    
    public static boolean canParse(String dateStr,String dateFormat,String lang){
        SimpleDateFormat sDateFormat=new SimpleDateFormat(dateFormat,LocaleFactory.getLocale(lang, ""));
        try {
            sDateFormat.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    
    public static boolean canFormat(long unix,String dateFormat,String lang){
        SimpleDateFormat sDateFormat=new SimpleDateFormat(dateFormat,LocaleFactory.getLocale(lang, ""));
        try {
            System.out.println(sDateFormat.format(unix));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public static Map<String,Map<String,Object>> listToMap(List<Map<String,Object>> list, String keyName){
        Map<String,Map<String,Object>> returnMap = new HashMap<String, Map<String,Object>>();
        for(Map<String,Object> map:list){
            if(map.containsKey(keyName)){
                String tableNames = (String)map.get(keyName);
                String[] tableArray = tableNames.split(",");
                for(String tableStr:tableArray){
                    String atype = "";
                    if(map.containsKey("atype")){
                        atype.concat((String)map.get("atype"));
                    }
                    returnMap.put(atype.concat((String)map.get(keyName)), map);
                }
            }else{
                try {
                    throw new Exception("无此key:".concat(keyName));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return returnMap;
        
    }
    
    public static Set<String> getKey(String keyword,Map<String,Object> map){
        Set<String> keys = new HashSet<String>();
        for(String key:map.keySet()){
            if(key.indexOf(keyword) != -1){
                keys.add(key);
            }
        }
        return keys;
    }
    /**
     * byte数组转换成16进制字符串
     * 
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    public static String getTypeByStream(byte[] b) {
        String type = bytesToHexString(b).toUpperCase();
        if (type.contains("89504E47")) {
            return "png";
        } else if (type.contains("FFD8FF")) {
            return "jpg";
        } else if (type.contains("47494638")) {
            return "gif";
        } else if (type.contains("49492A00")) {
            return "tif";
        } else if (type.contains("424D")) {
            return "bmp";
        }
        return type;
    }
    public static String detect(String title,String content){
        Detector detect;
        String language = Constants.SystemComponent.DEFAULT_LANGUAGE;
        String contentDetect = "";
        String titleDetect = "";
        boolean contentBoolean = true;
        boolean titleBoolean = true;
        boolean emptyContent = true;
        boolean emptyTitle = true;
        if(StringUtils.isNotEmpty(content)){
            emptyContent = false;
            try{
                if(!CharUtil.isChinese(content)){
                    detect = DetectorFactory.create();
                    detect.append(content);
                    contentDetect = detect.detect();
                }else{
                    contentDetect = Constants.SystemComponent.DEFAULT_LANGUAGE;
                    contentBoolean = false;
                }
            }catch(Exception e){
                contentBoolean = false;
                LogUtil.getLogger().error(e);
            }
        }
        if(StringUtils.isNotEmpty(title)){
            emptyTitle = false;
            try{
                if(!CharUtil.isChinese(title)){
                    detect = DetectorFactory.create();
                    detect.append(title);
                    titleDetect = detect.detect();
                }else{
                    titleDetect = Constants.SystemComponent.DEFAULT_LANGUAGE;
                    contentBoolean = false;
                }
            }catch(Exception e){
                titleBoolean = false;
                LogUtil.getLogger().error(e);
            }
        }
        if(!emptyContent && !emptyTitle){
            if(contentBoolean && titleBoolean){
                language = titleDetect;
            }else if(titleBoolean &&!contentBoolean){
                language = titleDetect;
            }else if(!titleBoolean && contentBoolean){
                language = contentDetect;
            }else{
                language = Constants.SystemComponent.DEFAULT_LANGUAGE;
            }
        }else if(emptyTitle){
            if(contentBoolean){
                language = contentDetect;
            }else{
                language = Constants.SystemComponent.DEFAULT_LANGUAGE;
            }
        }else{
            if(titleBoolean){
                language = titleDetect;
            }else{
                language = Constants.SystemComponent.DEFAULT_LANGUAGE;
            }
        }
        return language;
    }
}
