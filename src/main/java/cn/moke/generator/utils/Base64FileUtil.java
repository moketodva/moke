package cn.moke.generator.utils;


/**
 * @author moke
 */
public class Base64FileUtil {

    public static String getData(String data64) {
        String[] split = data64.split("base64,");
        if(split.length == 2){
            return split[split.length-1];
        }
        return null;
    }

    public static String getType(String data64) {
        String[] split1 = null;
        if(data64 != null){
            String[] split = data64.split(";base64,");
            if(split != null){
                split1 = split[0].split(":");
            }
        }
        if(split1 != null){
            return split1[1];
        }else{
            return null;
        }
    }
}
