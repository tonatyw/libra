package tonatyw.libra.util;
/**
 * 常量类
 * @ClassName Constants
 * @Description: 常量类
 * @date 2019年7月15日 上午10:47:27
 */
public class Constants {
    public interface ConfigKey{
        public final static String CLASS_PRE = "classPre";
        public final static String BASE_RABBIT_CLASS = "baseRabbit";
        public final static String RABBIT_HOST = "rabbitHost";
        public final static String RABBIT_PORT = "rabbitPort";
        public final static String RABBIT_USER_NAME = "rabbitUserName";
        public final static String RABBIT_PASSWORD = "rabbitPassword";
        public final static String RABBIT_NETWORK_RECOVERY_INTERVAL = "rabbitNetworkRecoveryInterval";
    }
    public interface RabbitParams{
        public final static String EXCLUSIVE = "exclusive";
        public final static String QUEUE_NAME = "queueName";
        public final static String EXCHANGE_NAME = "exchangeName";
        public final static String QOS = "qos";
        public final static String RABBIT_TYPE = "rabbitType";
        public final static String ROUTING_KEY = "routingKey";
        public final static String AUTO_ACK = "autoAck";
        public final static String QUEUE_DURABLE = "queueDurable";
        public final static String EXCHANGE_DURABLE = "exchangeDurable";
        public final static String DELIVERY_MODE = "deliveryMode";
        public final static String PRIORITY = "priority";
        public final static String CONTENT_TYPE = "contentType";
        public final static String ENCODING = "utf-8";
    }
    public interface SystemComponent{
        public final static String TASK = "task";
        public final static String PROCESSOR = "processor";
        public final static String PERFORMANCE = "performance";
        public final static String CONSUMER = "Consumer";
        public final static String PRODUCER = "Producer";
        public final static String TABLE_NAME = "tableName";
        public final static String FILE_REPLACE_SYM = "-";
        public final static String NAME = "name";
        public final static String REG_PARAM = "${\\w*?}";
        public final static String DEFAULT_LANGUAGE = "zh-cn";
        public final static String DETECT_FILE_NAME = "all";
    }
    
    public interface TaskParams{
        public final static String NAME = "name";
        public final static String MULTIPLE = "multiple";
    }
    public interface ProcessorParams{
        public final static String NAME = "name";
    }
    public interface PerformanceParams{
        public final static String NAME = "name";
        public final static String CLASS = "class";
        public final static String FIELD_KEY = "fieldKey";
        public final static String FIELD_KEY_SPILIT = ",";
        public final static String NUMS_KEY = "numsKey";
        public final static String NUMS_KEY_SPILIT = ",";
        public final static String DATE_KEY = "dateKey";
        public final static String DATE_KEY_SPILIT = ",";
    }
    
    public interface FileName{
        public final static String FDFS_CLIENT = "client.conf";
        public final static String COLUMN_GENERATE = "columnGenerate";
        public final static String ESMAPPING = "esmapping";
    }
    
    public interface EsMapping{
        public final static String TABLE_NAME = "name";
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN = "column";
        public final static String IMPORT_TABLE_NAME = "importTableName";
        public final static String IMPORT_TYPE = "importType";
    }
    
    public interface ColumnGenerate{
        public final static String TABLE = "table";
        public final static String COLUMN = "column";
        public final static String TABLE_NAME = "name";
        public final static String COLUMN_NAME = "name";
        public final static String RULE = "rule";
    }
    
    public interface HBase{
        public final static String FAMILY = "family";
        public final static String BASE_INFO = "base_info";
        public final static String QUALIFIER = "qualifier";
        public final static String VALUE = "value";
    }
    
    public interface Param{
        public final static String TABLE_NAME = "table_name";
        public final static String ROW_KEY = "row_key";
        public final static String IMG_URL = "img_url";
        public final static String IMG_URL_NO_FMT = "(?<!fmt_)[a-zA-Z]+_img_url";
        public final static String IMG_URL_FMT = "fmt(?:[\\s\\S]*?)img_url";
        public final static String PRE = "fmt_";
        /** contentType */
        public final static String CONTENT_TYPE = "content_type";
        /** 0图片 1视频 */
        public final static String TYPE = "TYPE";
    }
}
