
package tonatyw.libra.conf;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultAttribute;
import org.xml.sax.SAXException;

public class XmlConf extends BaseConf{
    private static SAXReader reader;
    private Map<String,Map<String,Object>> xmlsMap;
    static{
        reader = new SAXReader();
        try {
            reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
    public XmlConf(){
        try {
//            LogUtil.getLogger().info(resourcePath);
//            List<String> xmlList = SystemUtil.getFiles(resourcePath, this.getClass().getSimpleName().split("Conf")[0].toLowerCase());
            this.xmlsMap = readXmls(readPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Map<String,Map<String,Object>> readXmls(List<String> confList)throws Exception{
        Map<String,Map<String,Object>> map = new HashMap<String,Map<String,Object>>();
        for(String resourcePath:confList){
            map.put(getKeyword(resourcePath), readXml(resourcePath));
        }
        return map;
    }
    public String getKeyword(String resourcePath){
        Pattern p = Pattern.compile("(\\w+).xml");
        Matcher m = p.matcher(resourcePath);
        if(m.find()){
            String fileName = m.group(1);
            return format(m.group(1));
        }
        return null;
    }
    public String format(String source){
        StringBuffer sb = new StringBuffer();
        Matcher m = Pattern.compile("-([a-z])", Pattern.CASE_INSENSITIVE).matcher(source);
        while(m.find()){
            m.appendReplacement(sb, m.group().toUpperCase());
        }
        m.appendTail(sb);
        return sb.toString().replaceAll("-", "");
    }
    public Map<String,Object> readXml(String resourcePath)throws Exception{
        InputStream is = this.getClass().getResourceAsStream(resourcePath);
        Document document = reader.read(is);
        Element root = document.getRootElement();
        Map<String,Object> map = new HashMap<String,Object>();
        childs(root, map);
        return map;
    }
    public Map<String,Object> childs(Element element,Map<String,Object> map){
        List<Map<String,Object>> list = new LinkedList<Map<String,Object>>();
        List<Element> elementList = element.elements();
        String key = null;
        if(element.elements().size()>0){
            for(Element e:elementList){
                key = e.getName();
                Map<String,Object> nodeMap = new HashMap<String,Object>();
                List<DefaultAttribute> attributeList = e.attributes();
                //添加属性
                for(DefaultAttribute attr:attributeList){
                    nodeMap.put(attr.getName(), attr.getStringValue());
                }
                if(e.elements().size()>0){
                    childs(e,nodeMap);
                }
                list.add(nodeMap);
            }
        }
        if(!StringUtils.isEmpty(key)){
            map.put(key, list);
        }
        return map;
    }
    public static void main(String[] args) {
        XmlConf xc = new XmlConf();
        try {
            System.out.println(xc.getXmlsMap());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<String> getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(List<String> resourcePath) {
        this.resourcePath = resourcePath;
    }
    public Map<String, Map<String, Object>> getXmlsMap() {
        return xmlsMap;
    }

    public void setXmlsMap(Map<String, Map<String, Object>> xmlsMap) {
        this.xmlsMap = xmlsMap;
    }
}
