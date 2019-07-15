package tonatyw.libra.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.alibaba.fastjson.JSON;

import tonatyw.libra.conf.CenterConfig;

/***
 *
 * 
 * @author wish30_1
 * @version $Id: HbaseUtil.java, v 0.1 2018年4月19日 下午3:21:43 wish30_1 Exp $
 */
public class HbaseUtil {
    public static Configuration configuration;
    public static final Integer maxVersion = 10000;
    private static ByteBuffer buffer = ByteBuffer.allocate(8);
    private static Connection connection;
    //初始化Hbase核心对象
    static {
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.property.clientPort", CenterConfig.getConfsMap().get("port"));
        configuration.set("hbase.zookeeper.quorum",
          "master");
//      configuration.set("hbase.master", "master:60000");
        configuration.set("hbase.master", CenterConfig.getConfsMap().get("hbaseIp"));
        configuration.set("hbase.client.keyvalue.maxsize", "20971520");
        try {
            connection = ConnectionFactory.createConnection(configuration, Sets.getHbasePool());
//            connection = getConnection();
          //初始化队列
//            for(int i=0;i<1;i++){
//                Sets.getHbaseConQueue().put(ConnectionFactory.createConnection());
//                System.out.println("初始化hbase链接:"+(i+1));
//            }
//            hTablePool = new HTablePool(configuration,1000);
            
            System.out.println("初始化完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /***
     * 获取Configuration对象
     * @return
     */
    public static Configuration getHbaseConfiguration() {
        return configuration;
    }
    
    public static Connection getConnection(){
        try{
            if(connection==null || connection.isClosed()){
                connection = ConnectionFactory.createConnection(configuration, Sets.getHbasePool());
            }
        }catch(Exception e){
            e.printStackTrace();
            LogUtil.getLogger().error(e);
        }
        return connection;
    }
    /***
     * 保存多行数据
     * 
     * @param tableName
     * @param rowKey
     * @param dataList
     */
    public static boolean add(String tableName,String rowKey,List<Map<String,String>> dataList){
        long ts = System.currentTimeMillis();
        Connection connection = null;
        Table table = null;
        try {
            connection = getConnection();
            table = connection.getTable(TableName.valueOf(tableName));
            Put put = new Put(rowKey.getBytes());
            for(Map<String,String> map:dataList){
                put.add(map.get("family").getBytes(),map.get("qualifier").getBytes(),ts,map.get("value").getBytes());
            }
            table.put(put);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally{
            try {
                if(table!=null){
                    table.close();
                }
                
//                if(connection!=null){
//                    connection.close();
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void add(String tableName,Map<String,Object> map){
        long ts = System.currentTimeMillis();
        List<Put> list = new LinkedList<Put>();
        Connection connection = null;
        Table table = null;
        try {
            connection = getConnection();
            table = connection.getTable(TableName.valueOf(tableName));
            for(String key:map.keySet()){
                Put put = new Put(key.getBytes());
                Map<String,Object> map2 = (Map<String,Object>)map.get(key);
                for(String key2:map2.keySet()){
                    if(map2.get(key2) != null){
                        put.add("base_info".getBytes(), key2.getBytes(), String.valueOf(map2.get(key2)).getBytes());
                    }
                }
                list.add(put);
            }
            table.put(list);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(table!=null){
                    table.close();
                }
                
//                if(connection!=null){
//                    connection.close();
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void addBatch(String tableName,List<Map<String,Object>> listMap){
        String column = "base_info";
        long ts = System.currentTimeMillis();
        Connection connection = null;
        Table table = null;
        try {
            connection = getConnection();
            table = connection.getTable(TableName.valueOf(tableName));
            List<Put> list = new LinkedList<Put>();
            Map<String,Object> map = new HashMap<String,Object>();
            for(Map<String,Object> lMap:listMap){
                map.put((String)lMap.get("rowKey"), lMap);
            }
            int i=0;
            for(String key:map.keySet()){
                Put put = new Put(key.getBytes());
                Map<String,Object> map3 = (Map<String,Object>)map.get(key);
                Map<String,Object> map2 = (Map<String,Object>)map3.get(column);
                for(String key2:map2.keySet()){
                    if(map2.get(key2) != null){
                        put.add(column.getBytes(), key2.getBytes(), String.valueOf(map2.get(key2)).getBytes());
                    }
                }
                list.add(put);
                i++;
                if(i%1000==0){
                    table.put(list);
                    list = new LinkedList<Put>();
                    System.out.println(i);
                }
            }
            System.out.println(i);
            table.put(list);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(table!=null){
                    table.close();
                }
                
//                if(connection!=null){
//                    connection.close();
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
     /***
      * 创建列簇
      * 
      * @param tableName
      * @param columnFamily
      * @return
      * @throws Exception
      */
    public static boolean create(String tableName, String columnFamily){  
        Connection connection = null;
        Admin admin = null;
        try{
            connection = getConnection();
            admin =connection.getAdmin();
            if (admin.tableExists(TableName.valueOf(tableName))) {  
                System.out.println(tableName + " exists!");  
                return false;  
            } else {  
                String[] columnFamilyArray = columnFamily.split(",");  
                HColumnDescriptor[] hColumnDescriptor = new HColumnDescriptor[columnFamilyArray.length];  
                for (int i = 0; i < hColumnDescriptor.length; i++) {  
                    hColumnDescriptor[i] = new HColumnDescriptor(columnFamilyArray[i]);  
                }  
                HTableDescriptor familyDesc = new HTableDescriptor(TableName.valueOf(tableName));  
                for (HColumnDescriptor columnDescriptor : hColumnDescriptor) {  
                    familyDesc.addFamily(columnDescriptor);  
                }  
                HTableDescriptor tableDesc = new HTableDescriptor(TableName.valueOf(tableName), familyDesc);  
                admin.createTable(tableDesc);  
                System.out.println(tableName + " create successfully!");  
                return true;  
            }  
        }catch(Exception e){
            e.printStackTrace();
            LogUtil.getLogger().error(e);
        }finally{
            try {
                if(admin!=null){
                    admin.close();
                }
                
//                if(connection!=null){
//                    connection.close();
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }  
  
    /***
     * 保存一条数据
     * 
     * @param tablename
     * @param row
     * @param columnFamily
     * @param qualifier
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean put(String tableName, String row, String columnFamily,  
                              String qualifier, String data){  
        Connection connection = null;
        Table table = null;
        try {
            connection = getConnection();
            table = connection.getTable(TableName.valueOf(tableName));
            Put put = new Put(Bytes.toBytes(row));  
            put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(qualifier),  
                    Bytes.toBytes(data));  
            table.put(put);  
            System.out.println("put '" + row + "', '" + columnFamily + ":" + qualifier  
                    + "', '" + data + "'");  
            return true;  
        }catch(Exception e){
            e.printStackTrace();
            LogUtil.getLogger().error(e);
        } finally{
            try {
                if(table!=null){
                    table.close();
                }
                
//                if(connection!=null){
//                    connection.close();
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }  
  
    /***
     * 将返回结果封装成Map
     * 
     * @param result
     * @return
     */
    public static Map<String, Object> resultToMap(Result result) {  
        if(result.isEmpty()) return null;
        
        Map<String, Object> resMap = new HashMap<String, Object>();  
        List<Cell> listCell = result.listCells();  
        Map<String, Object> tempMap = new HashMap<String, Object>();  
        String rowKey = "";  
        List<String> familynamelist = new ArrayList<String>();  
        for (Cell cell : listCell) {  
            byte[] rowArray = cell.getRowArray();  
            byte[] familyArray = cell.getFamilyArray();  
            byte[] qualifierArray = cell.getQualifierArray();  
            byte[] valueArray = cell.getValueArray();  
            int rowoffset = cell.getRowOffset();  
            int familyoffset = cell.getFamilyOffset();  
            int qualifieroffset = cell.getQualifierOffset();  
            int valueoffset = cell.getValueOffset();  
            int rowlength = cell.getRowLength();  
            int familylength = cell.getFamilyLength();  
            int qualifierlength = cell.getQualifierLength();  
            int valuelength = cell.getValueLength();  
  
            byte[] temprowarray = new byte[rowlength];  
            System.arraycopy(rowArray, rowoffset, temprowarray, 0, rowlength);  
            String temprow= Bytes.toString(temprowarray);  
//            System.out.println(Bytes.toString(temprowarray));  
  
            byte[] tempqulifierarray = new byte[qualifierlength];  
            System.arraycopy(qualifierArray, qualifieroffset, tempqulifierarray, 0, qualifierlength);  
            String tempqulifier= Bytes.toString(tempqulifierarray);  
//            System.out.println(Bytes.toString(tempqulifierarray));  
  
            byte[] tempfamilyarray = new byte[familylength];  
            System.arraycopy(familyArray, familyoffset, tempfamilyarray, 0, familylength);  
            String tempfamily= Bytes.toString(tempfamilyarray);  
//            System.out.println(Bytes.toString(tempfamilyarray));  
  
            byte[] tempvaluearray = new byte[valuelength];  
            System.arraycopy(valueArray, valueoffset, tempvaluearray, 0, valuelength);  
            String tempvalue= Bytes.toString(tempvaluearray);  
//            System.out.println(Bytes.toString(tempvaluearray));  
  
  
            tempMap.put(tempfamily + ":" + tempqulifier, tempvalue);  
//            long t= cell.getTimestamp();  
//            tempMap.put("timestamp",t);  
            rowKey = temprow;  
            String familyname = tempfamily;  
            if (familynamelist.indexOf(familyname) < 0) {  
                familynamelist.add(familyname);  
            }  
        }  
        resMap.put("rowKey", rowKey);  
        for (String familyname : familynamelist) {  
            HashMap<String,Object> tempFilterMap = new HashMap<String,Object>();  
            for (String key : tempMap.keySet()) {  
                String[] keyArray = key.split(":");  
                if(keyArray[0].equals(familyname)){  
                    tempFilterMap.put(keyArray[1],tempMap.get(key));  
                }  
            }  
            resMap.put(familyname, tempFilterMap);  
        }  
  
        return resMap;
    }  
  
    /***
     * 获取单行数据
     * 
     * @param tablename
     * @param row
     * @return
     * @throws Exception
     */
    public static String get(String tableName, String row){
        Table table = null;
        Connection connection = null;
        try{
            connection = getConnection();
            table = connection.getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(row));  
            Result result = table.get(get);  
            System.out.println("Get: " + result);  
            if(!result.isEmpty()){
                return JSON.toJSONString(resultToMap(result));  
            }else{
                return null;
            }
        }catch(Exception e){
            e.printStackTrace();
            LogUtil.getLogger().error(e);
        }finally{
            try {
                if(table!=null){
                    table.close();
                }
                
//                if(connection!=null){
//                    connection.close();
//                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }  
  
    /***
     * 描述全表  
     * 
     * @param tablename
     * @return
     * @throws Exception
     */
    public static List<Map<String,Object>> scan(String tableName){
        Table table = null;
        Connection connection = null;
        try{
            connection = getConnection();
            table = connection.getTable(TableName.valueOf(tableName));
            Scan s = new Scan();  
            ResultScanner rs = table.getScanner(s);  
      
            List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();  
            for (Result r : rs) {  
                for(Cell c:r.listCells()){
                    //�ر��ж�spider_post
    //              if(new String(c.getQualifier(),"UTF-8").equals("replyTime") ||
    //                      new String(c.getQualifier(),"UTF-8").equals("clickTime")){
    //                  if(new String(c.getValue(),"UTF-8")==null || new String(c.getValue(),"UTF-8").length()<1){
    //                      cellMap.put(new String(c.getQualifier(),"UTF-8"), "0");
    //                  }
    //              }else{
    //                  cellMap.put(new String(c.getQualifier(),"UTF-8"), new String(c.getValue(),"UTF-8"));
    //              }
                    String value = new String(c.getValue(),"UTF-8");
                }
                Map<String, Object> tempmap = resultToMap(r);  
                resList.add(tempmap);  
            }  
            return resList;
        }catch(Exception e){
            e.printStackTrace();
            LogUtil.getLogger().error(e);
        }finally{
            try {
                if(table!=null){
                    table.close();
                }
                
//                if(connection!=null){
//                    connection.close();
//                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }  
  
    /**
     * 删除表
     * 
     * @param tableName
     * @return
     * @throws IOException
     */
    public static boolean delete(String tableName){
        Connection connection = null;
        HBaseAdmin admin = null;
        try{
            connection = getConnection();
            admin = (HBaseAdmin) connection.getAdmin();  
            if (admin.tableExists(tableName)) {  
                try {  
                    admin.disableTable(tableName);  
                    admin.deleteTable(tableName);  
                } catch (Exception e) {  
                    e.printStackTrace();  
                    return false;  
                }  
            }  
            return true;
        }catch(Exception e){
            e.printStackTrace();
            LogUtil.getLogger().error(e);
        }finally{
            try {
                if(admin!=null){
                    admin.close();
                }
                
//                if(connection!=null){
//                    connection.close();
//                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }  
  
    /***
     * 删除列簇
     * 
     * @param tableName
     * @param columnFamilyName
     * @return
     * @throws IOException
     */
    public static boolean deleteColumnFamily(String tableName,String columnFamilyName){  
        Connection connection = null;
        HBaseAdmin admin = null;
        try{
            connection = getConnection();
            admin = (HBaseAdmin) connection.getAdmin();  
            if (admin.tableExists(tableName)) {  
                admin.deleteColumn(tableName,columnFamilyName);  
            }  
            return true;
        }catch(Exception e){
            e.printStackTrace();
            LogUtil.getLogger().error(e);
        }finally{
            try {
                if(admin!=null){
                    admin.close();
                }
                
//                if(connection!=null){
//                    connection.close();
//                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }  
    /**
     * 根据主键删除一行
     * 
     * @param tableName
     * @param rowKey
     * @return
     * @throws IOException
     */
    public static boolean deleteRow(String tableName,String rowKey) throws IOException {  
        Connection connection = null;
        HBaseAdmin admin = null;
        Table table = null;
        try{
            connection = getConnection();
            admin = (HBaseAdmin) connection.getAdmin();  
            table = connection.getTable(TableName.valueOf(tableName));
            if (admin.tableExists(tableName)) {  
                Delete delete = new Delete(rowKey.getBytes());  
                table.delete(delete);  
            }  
            return true;
        }catch(Exception e){
            e.printStackTrace();
            LogUtil.getLogger().error(e);
        }finally{
            try {
                if(table!=null){
                    table.close();
                }
                
                if(admin!=null){
                    admin.close();
                }
                
//                if(connection!=null){
//                    connection.close();
//                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }  
  /***
   * 删除一列
   * 
   * @param tableName
   * @param rowKey
   * @param columnFamilyName
   * @param qualifierName
   * @return
   * @throws IOException
   */
    public static boolean deleteQualifier(String tableName,String rowKey,String columnFamilyName,String qualifierName) throws IOException {  
        Connection connection = null;
        HBaseAdmin admin = null;
        Table table = null;
        try{
            connection = getConnection();
            admin = (HBaseAdmin) connection.getAdmin();  
            table = connection.getTable(TableName.valueOf(tableName));
            if (admin.tableExists(tableName)) {  
                Delete delete = new Delete(rowKey.getBytes());  
                delete.addColumns(columnFamilyName.getBytes(),qualifierName.getBytes());  
                table.delete(delete);  
            }  
            return true;
        }catch(Exception e){
            e.printStackTrace();
            LogUtil.getLogger().error(e);
        }finally{
            try {
                if(table!=null){
                    table.close();
                }
                
                if(admin!=null){
                    admin.close();
                }
                
//                if(connection!=null){
//                    connection.close();
//                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }  
    
    public static byte[] longToBytes(long x) {  
       buffer.putLong(0, x);  
       return buffer.array();  
    } 
    
    /**
     * 域名正则匹配
     * @param url
     * @return
     */
    public static String getDomain(String url){
        Pattern pa = Pattern.compile("(http[s]{0,1}://\\w+.[\\w:]+)([/]{0,1}(?:\\s|\\S)*)", Pattern.DOTALL);
        Matcher ma = pa.matcher(url);
        if(ma.find()){
            return ma.group(1);
        }else{
            return null;
        }
    }
    
    /**
     * 域名正则匹配
     * @param url
     * @return
     */
    public static String getAfterDomain(String url){
        Pattern pa = Pattern.compile("(http[s]{0,1}://\\w+.[\\w:]+)([/]{0,1}(?:\\s|\\S)*)", Pattern.DOTALL);
        Matcher ma = pa.matcher(url);
        if(ma.find()){
            return ma.group(2);
        }else{
            return null;
        }
    }
    public static void main(String[] args) {
        try {
//            HbaseUtil.put("testtable", "123123", "base_info", "aaa", "123");
//            HbaseUtil.put("testtable", "123123", "base_info", "bbb", "321");
//            HbaseUtil.put("testtable", "123123", "base_info", "aaa", "121215");
//            HbaseUtil.create("testtable", "base_info");
            //数据修改
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
