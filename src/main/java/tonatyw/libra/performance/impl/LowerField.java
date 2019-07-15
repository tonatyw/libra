package tonatyw.libra.performance.impl;

import java.util.Map;

import tonatyw.libra.performance.base.Performance;
import tonatyw.libra.util.Constants;

public class LowerField implements Performance{

    @Override
    public Map<String,Object> deal(Map<String, Object> map) throws Exception {
        String fieldKey = (String)map.get(Constants.PerformanceParams.FIELD_KEY);
        for(String key:fieldKey.split(Constants.PerformanceParams.FIELD_KEY_SPILIT)){
            map.put(key, ((String)map.get(key)).toLowerCase());
        }
        return map;
    }
}
