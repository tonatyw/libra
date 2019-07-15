package tonatyw.libra.conf;

import java.util.Map;

public class CenterConfig {
    /** conf.properties */
    private static Map<String,String> confsMap;
    private static Map<String,Map<String,Object>> xmlsMap;
    /** performance */
    /** processor */
    /** task */
    static{
        PropertiesConf pc = new PropertiesConf();
        confsMap = pc.getConfsMap();
        
        XmlConf xc = new XmlConf();
        xmlsMap = xc.getXmlsMap();
    }
    public static Map<String, String> getConfsMap() {
        return confsMap;
    }
    public static void setConfsMap(Map<String, String> confsMap) {
        CenterConfig.confsMap = confsMap;
    }
    public static Map<String, Map<String, Object>> getXmlsMap() {
        return xmlsMap;
    }
    public static void setXmlsMap(Map<String, Map<String, Object>> xmlsMap) {
        CenterConfig.xmlsMap = xmlsMap;
    }
}
