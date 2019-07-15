package tonatyw.libra.performance.impl;

import java.util.Map;

import com.alibaba.fastjson.JSON;

import tonatyw.libra.performance.base.Performance;
import tonatyw.libra.util.Constants;
import tonatyw.libra.util.HbaseUtil;
import tonatyw.libra.util.LogUtil;

public class GetHBase implements Performance{
    @Override
    public Map<String,Object> deal(Map<String, Object> map) throws Exception {
        String json = HbaseUtil.get(((String)map.get(Constants.Param.TABLE_NAME)).toLowerCase(), (String)map.get(Constants.Param.ROW_KEY));
        Map<String,Object> resultMap = JSON.parseObject(json, Map.class);
        map.putAll((Map)resultMap.get(Constants.HBase.BASE_INFO));
        LogUtil.getLogger().info("params:"+JSON.toJSONString(map));
        return map;
    }
}
