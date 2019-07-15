package tonatyw.libra.conf;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertiesConf extends BaseConf{
    private static Properties props;
    private Map<String,String> confsMap;
    static{
        props = new Properties();
    }
    public PropertiesConf(){
        try {
//            LogUtil.getLogger().info(resourcePath);
//            List<String> confList = SystemUtil.getFiles(resourcePath, this.getClass().getSimpleName().split("Conf")[0].toLowerCase());
            this.confsMap = readConfs(readPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Map<String,String> extractConfs(){
        return null;
    }
//    public List<String> getConfs(){
//        List<String> confList = new LinkedList<String>();
//        File resource = new File(resourcePath);
//        File[] files = resource.listFiles();
//        for(File file:files){
//            if(file.getName().endsWith("properties")){
//                confList.add(file.getAbsolutePath());
//            }
//        }
//        return confList;
//    }
    public Map<String,String> readConfs(List<String> confList)throws Exception{
        Map<String, String> confsMap = new HashMap<String, String>();
        for(String confPath:confList){
            confsMap.putAll(readConf(confPath));
        }
        return confsMap;
    }
    public Map<String,String> readConf(String resourcePath)throws Exception{
        Map<String,String> confMap = new HashMap<String,String>();
        InputStream reader = this.getClass().getResourceAsStream(resourcePath);
//        InputStreamReader reader = new InputStreamReader(new FileInputStream(resourcePath),"utf-8");

        props.load(reader); //load个人建议还是用Reader来读，因为reader体系中有个InputStreamReader可以指定编码
//        LogUtil.getLogger().error(props);
        for(Object key:props.keySet()){
            confMap.put(key.toString(), props.getProperty(key.toString()));
        }
        
        return confMap;
    }
    public static void main(String[] args) throws Exception {
        PropertiesConf c = new PropertiesConf();
        System.out.println(c.readPath());
    }
    public List<String> getResourcePath() {
        return resourcePath;
    }
    public void setResourcePath(List<String> resourcePath) {
        this.resourcePath = resourcePath;
    }
    public static Properties getProps() {
        return props;
    }
    public static void setProps(Properties props) {
        PropertiesConf.props = props;
    }
    public Map<String, String> getConfsMap() {
        return confsMap;
    }
    public void setConfsMap(Map<String, String> confsMap) {
        this.confsMap = confsMap;
    }
    
}
