package tonatyw.libra.conf;

import java.util.List;

import tonatyw.libra.util.Sets;
import tonatyw.libra.util.SystemUtil;

public class BaseConf {
    protected List<String> resourcePath = Sets.getResourcePath();
    public List<String> readPath(){
        List<String> fileList = SystemUtil.getFiles(resourcePath, this.getClass().getSimpleName().split("Conf")[0].toLowerCase());
        return fileList;
    }
}
