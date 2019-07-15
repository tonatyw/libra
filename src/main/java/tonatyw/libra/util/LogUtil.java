package tonatyw.libra.util;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LogUtil {
    private static class SingleBuilder{
        private static Logger logger = null;
        private static InputStream loggerPath = LogUtil.class.getResourceAsStream("/log4j.properties");
        static{
            PropertyConfigurator.configure(loggerPath);
            logger  =  Logger.getLogger(LogUtil.class);
        }
    } 
    public static Logger getLogger() {
        return SingleBuilder.logger;
    }
}
