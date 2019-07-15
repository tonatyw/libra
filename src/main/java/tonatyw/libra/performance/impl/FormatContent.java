package tonatyw.libra.performance.impl;

import java.util.Map;

import tonatyw.libra.performance.base.Performance;
import tonatyw.libra.util.Constants;

public class FormatContent implements Performance{
    public Map<String,Object> deal(Map<String, Object> map) throws Exception{
        String formatKey = (String)map.get("formatKey");
        String[] keys = formatKey.split(",");
        //去除content去掉html标签
        for(String key:keys){
            if(map.containsKey(key)){
                String value = (String)map.get(key);
                value = value.replaceAll("<[^>]*>", "");
                map.put(Constants.Param.PRE.concat(key), value);
            }
        }
        return map;
    }
}
