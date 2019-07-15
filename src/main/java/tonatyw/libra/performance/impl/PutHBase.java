package tonatyw.libra.performance.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tonatyw.libra.conf.CenterConfig;
import tonatyw.libra.performance.base.Performance;
import tonatyw.libra.util.Constants;
import tonatyw.libra.util.HbaseUtil;
import tonatyw.libra.util.SystemUtil;

public class PutHBase implements Performance{
    
    @Override
    public Map<String,Object> deal(Map<String, Object> map) throws Exception{
        // 将数据map转化为family qualifier data的listmap形式
        List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
        for(String key:map.keySet()){
            Map<String,Object> hbaseMapping = CenterConfig.getXmlsMap().get("hbasemapping");
            Map<String,Map<String,Object>> tables = SystemUtil.listToMap((List<Map<String,Object>>)hbaseMapping.get("table"), "name");
            
            Map<String,Object> tableMap = tables.get(((String)map.get("table_name")).toLowerCase());
            List<Map<String,Object>> columnList = (List<Map<String,Object>>)tableMap.get("column");
            for(Map<String,Object> columnMap: columnList){
                if(columnMap.containsValue(key)){
                    Map<String,String> tmpMap = new HashMap<String,String>();
                    tmpMap.put(Constants.HBase.FAMILY, Constants.HBase.BASE_INFO);
                    tmpMap.put(Constants.HBase.QUALIFIER, key);
                    tmpMap.put(Constants.HBase.VALUE, String.valueOf(map.get(key)));
                    dataList.add(tmpMap);
                }
            }
        }
        if(HbaseUtil.add((String)map.get(Constants.Param.TABLE_NAME), (String)map.get(Constants.Param.ROW_KEY), dataList)){
            return map;
        }
        return null;
    }
}
