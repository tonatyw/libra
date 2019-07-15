package tonatyw.libra.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tonatyw.libra.conf.CenterConfig;
  
/** 
 * 对jdbc的完整封装 
 * 
 */  
public class JDBCUtil {  
  
    private static  String driver = null;  
    private static  String url = null;  
    private static  String username = null;  
    private static  String password = null;  
    static {     
        try {    
            driver = CenterConfig.getConfsMap().get("driver");
            url = CenterConfig.getConfsMap().get("url");  
            username = CenterConfig.getConfsMap().get("user");
            password = CenterConfig.getConfsMap().get("password");
            Class.forName(driver);
            // 初始化队列
            for(int i=0;i<Sets.getCPUNUM();i++){
                Connection conn = DriverManager.getConnection(url, username,      
                    password);
                Sets.getConQueue().put(conn);
            }
            // 加载数据库驱动程序
            Class.forName(driver);     
        } catch (Exception e) {     
            System.out.println("加载驱动错误");     
            System.out.println(e.getMessage());     
        }     
    }
      
    public JDBCUtil() {  
        
    }
      
    /**   
     * insert update delete SQL语句的执行的统一方法   
     * @param sql SQL语句   
     * @param params 参数数组，若没有参数则为null   
     * @return 受影响的行数   
     */      
    public static int executeUpdate(String sql, Object[] params) {      
        // 受影响的行数      
        int affectedLine = 0;      
        Connection conn = null;
        PreparedStatement pst = null;
        try {      
            // 获得连接      
            conn = Sets.getConQueue().take();
            // 调用SQL       
            pst = conn.prepareStatement(sql);      
                  
            // 参数赋值      
            if (params != null) {
                for (int i = 0; i < params.length; i++) {      
                    pst.setObject(i + 1, params[i]);      
                }      
            }      
            /*在此 PreparedStatement 对象中执行 SQL 语句， 
                                          该语句必须是一个 SQL 数据操作语言（Data Manipulation Language，DML）语句，比如 INSERT、UPDATE 或 DELETE  
                                          语句；或者是无返回内容的 SQL 语句，比如 DDL 语句。    */  
            // 执行      
            affectedLine = pst.executeUpdate();  
            
        } catch (Exception e) {      
            System.out.println(e.getMessage());      
        } finally {      
            // 释放资源      
//            closeAll();
            try {
                if(pst!=null){
                    pst.close();
                }
                if(conn!=null){
                    Sets.getConQueue().put(conn);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }      
        return affectedLine;      
    }      
    
    /**   
     * insert update delete SQL语句的执行的统一方法   
     * @param sql SQL语句   
     * @param params 参数数组，若没有参数则为null   
     * @return 受影响的行数   
     */      
    public static int executeUpdateBatch(String sql, List<Object[]> pList) {      
        // 受影响的行数      
        int affectedLine = 0;      
        Connection conn = null;
        PreparedStatement pst = null;     
        try {      
            // 获得连接      
            conn = Sets.getConQueue().take();
            // 调用SQL       
            pst = conn.prepareStatement(sql);      
                  
            // 参数赋值      
            for(Object[] params:pList){
	            if (params != null) {      
	                for (int i = 0; i < params.length; i++) {      
	                    pst.setObject(i + 1, params[i]);      
	                }      
	                pst.addBatch();
	            }      
            }
            /*在此 PreparedStatement 对象中执行 SQL 语句， 
                                          该语句必须是一个 SQL 数据操作语言（Data Manipulation Language，DML）语句，比如 INSERT、UPDATE 或 DELETE  
                                          语句；或者是无返回内容的 SQL 语句，比如 DDL 语句。    */  
            // 执行      
            pst.executeBatch();
      
        } catch (Exception e) {      
            System.out.println(e.getMessage());      
        } finally {      
            // 释放资源      
//            closeAll();
            try {
                if(pst!=null){
                    pst.close();
                }
                if(conn!=null){
                    Sets.getConQueue().put(conn);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }      
        return affectedLine;
    }      
          
    /**   
     * SQL 查询将查询结果：一行一列   
     * @param sql SQL语句   
     * @param params 参数数组，若没有参数则为null   
     * @return 结果集   
     */      
    public static Object executeQuerySingle(String sql, Object[] params) {      
        Object object = null;      
        Connection conn = null;
        PreparedStatement pst = null;     
        ResultSet rst = null;
        try {      
            // 获得连接      
            conn = Sets.getConQueue().take();
                  
            // 调用SQL      
            pst = conn.prepareStatement(sql);      
                  
            // 参数赋值      
            if (params != null) {      
                for (int i = 0; i < params.length; i++) {      
                    pst.setObject(i + 1, params[i]);      
                }      
            }      
                  
            // 执行      
            rst = pst.executeQuery();      
      
            if(rst.next()) {      
                object = rst.getObject(1);      
            }      
                  
        } catch (Exception e) {      
            System.out.println(e.getMessage());      
        } finally {      
//            closeAll();
            try {
                if(pst!=null){
                    pst.close();
                }
                
                if(rst!=null){
                    rst.close();
                }
                
                if(conn!=null){
                    Sets.getConQueue().put(conn);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }      
      
        return object;      
    }      
      
    /**   
     * 获取结果集，并将结果放在List中   
     *    
     * @param sql  SQL语句   
     *         params  参数，没有则为null    
     * @return List   
     *                       结果集   
     */      
    public static Map<String,Map<String,Object>> excuteQuery(String sql, Object[] params) {   
        Connection conn = null;
        PreparedStatement pst = null;     
        ResultSet rs = null;
     // 创建List      
        Map<String,Map<String,Object>> existMap = new HashMap<String,Map<String,Object>>(); 
        try {      
            // 获得连接      
            conn = Sets.getConQueue().take();
                  
            // 调用SQL      
            pst = conn.prepareStatement(sql);      
                  
            // 参数赋值      
            if (params != null) {      
                for (int i = 0; i < params.length; i++) {      
                    pst.setObject(i + 1, params[i]);      
                }      
            }      
                  
            // 执行      
            rs = pst.executeQuery();      
            
            // 创建ResultSetMetaData对象      
            ResultSetMetaData rsmd = null;      
                  
            // 结果集列数      
            int columnCount = 0;      
            try {      
                rsmd = rs.getMetaData();      
                      
                // 获得结果集列数      
                columnCount = rsmd.getColumnCount();      
            } catch (SQLException e1) {      
                System.out.println(e1.getMessage());      
            }      
          
            // 将ResultSet的结果保存到List中      
            while (rs.next()) {      
                Map<String, Object> map = new HashMap<String, Object>();      
                for (int i = 1; i <= columnCount; i++) {    
                    map.put(rsmd.getColumnLabel(i), rs.getObject(i));      
                }      
                existMap.put(String.valueOf(map.get("row_key")),map);//每一个map代表一条记录，把所有记录存在list中      
            }      
        } catch (Exception e) {      
            System.out.println(e.getMessage());      
        } finally{
            //closeAll();
            try {
                if(pst!=null){
                    pst.close();
                }
                
                if(rs!=null){
                    rs.close();
                }
                
                if(conn!=null){
                    Sets.getConQueue().put(conn);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return existMap;      
    }      
    public static Map<String,Object> excuteQuerySingle(String sql, Object[] params) {      
        Connection conn = null;
        PreparedStatement pst = null;     
        ResultSet rs = null;
        // 创建List      
        Map<String, Object> map = new HashMap<String, Object>();
        try {      
            // 获得连接      
            conn = Sets.getConQueue().take();
                  
            // 调用SQL      
            pst = conn.prepareStatement(sql);      
                  
            // 参数赋值      
            if (params != null) {      
                for (int i = 0; i < params.length; i++) {      
                    pst.setObject(i + 1, params[i]);      
                }      
            }      
                  
            // 执行      
            rs = pst.executeQuery();      
            
            // 创建ResultSetMetaData对象      
            ResultSetMetaData rsmd = null;      
                  
            // 结果集列数      
            int columnCount = 0;      
            try {      
                rsmd = rs.getMetaData();      
                      
                // 获得结果集列数      
                columnCount = rsmd.getColumnCount();      
            } catch (SQLException e1) {      
                System.out.println(e1.getMessage());      
            }      
          
            // 将ResultSet的结果保存到List中      
            if (rs.next()) {      
                for (int i = 1; i <= columnCount; i++) {    
                    map.put(rsmd.getColumnLabel(i), rs.getObject(i));      
                }      
            }        
        } catch (Exception e) {      
            System.out.println(e.getMessage());      
        } finally{
            //closeAll();
            try {
                if(pst!=null){
                    pst.close();
                }
                
                if(rs!=null){
                    rs.close();
                }
                
                if(conn!=null){
                    Sets.getConQueue().put(conn);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }      
      
        return map;      
    }     
    
    public static Set<String> excuteQuerySet(String sql, Object[] params) {      
        Connection conn = null;
        PreparedStatement pst = null;     
        ResultSet rs = null;
        // 创建List      
        Set<String> set = new HashSet<String>();
        try {      
            // 获得连接      
            conn = Sets.getConQueue().take();
                  
            // 调用SQL      
            pst = conn.prepareStatement(sql);      
                  
            // 参数赋值      
            if (params != null) {      
                for (int i = 0; i < params.length; i++) {      
                    pst.setObject(i + 1, params[i]);      
                }      
            }      
                  
            // 执行      
            rs = pst.executeQuery();      
            
         // 创建ResultSetMetaData对象      
            ResultSetMetaData rsmd = null;      
                  
            try {      
                rsmd = rs.getMetaData();      
            } catch (SQLException e1) {      
                System.out.println(e1.getMessage());      
            }      
          
            // 将ResultSet的结果保存到List中      
            while (rs.next()) {      
                set.add(rs.getString("site_id")); 
            }         
        } catch (Exception e) {      
            System.out.println(e.getMessage());      
        } finally{
            //closeAll();
            try {
                if(pst!=null){
                    pst.close();
                }
                
                if(rs!=null){
                    rs.close();
                }
                
                if(conn!=null){
                    Sets.getConQueue().put(conn);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }      
      
        return set;      
    }      
          
    /**   
     * 存储过程带有一个输出参数的方法   
     * @param sql 存储过程语句   
     * @param params 参数数组   
     * @param outParamPos 输出参数位置   
     * @param SqlType 输出参数类型   
     * @return 输出参数的值   
     */      
    public static Object excuteQuery(String sql, Object[] params,int outParamPos, int SqlType) {      
        Object object = null;      
        Connection conn = null;
        CallableStatement callableStatement = null;
        try {      
            conn = Sets.getConQueue().take();
            // 调用存储过程      
            // prepareCall:创建一个 CallableStatement 对象来调用数据库存储过程。  
            callableStatement = conn.prepareCall(sql);      
                  
            // 给参数赋值      
            if(params != null) {      
                for(int i = 0; i < params.length; i++) {      
                    callableStatement.setObject(i + 1, params[i]);      
                }      
            }      
                  
            // 注册输出参数      
            callableStatement.registerOutParameter(outParamPos, SqlType);      
                  
            // 执行      
            callableStatement.execute();      
                  
            // 得到输出参数      
            object = callableStatement.getObject(outParamPos);      
                  
        } catch (Exception e) {      
            System.out.println(e.getMessage());      
        } finally {      
            // 释放资源      
//            closeAll();      
            try {
                if(conn!=null){
                    Sets.getConQueue().put(conn);
                }
                
                if(callableStatement!=null){
                    callableStatement.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }      
              
        return object;      
    }      
}  