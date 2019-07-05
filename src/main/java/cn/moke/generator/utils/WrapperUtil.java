package cn.moke.generator.utils;

import cn.moke.generator.entity.vo.Wrapper;
import cn.moke.generator.enums.WrapperCode;

/**
 * @author moke
 */
public class WrapperUtil {

    /**
     * 操作成功
     * @return
     */
    public static <T> Wrapper<T> ok(){
        return new Wrapper(WrapperCode.SUCCESS.getCode(), WrapperCode.SUCCESS.getMsg(), null);
    }

    /**
     * 操作成功
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Wrapper<T> ok(T data){
        return new Wrapper(WrapperCode.SUCCESS.getCode(), WrapperCode.SUCCESS.getMsg(), data);
    }

    /**
     * 系统异常
     * @return
     */
    public static Wrapper error(){
        return new Wrapper(WrapperCode.ERROR.getCode(), WrapperCode.ERROR.getMsg(), null);
    }

    /**
     * 基础异常 + 业务异常
     * @param wrapperCode
     * @return
     */
    public static Wrapper error(WrapperCode wrapperCode){
        return error(wrapperCode,null);
    }

    /**
     * 基础异常 + 业务异常
     * @param wrapperCode
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Wrapper<T> error(WrapperCode wrapperCode, T data){
        if(wrapperCode == null){
            throw new Error("wrapperCode不能为null");
        }
        Integer code = wrapperCode.getCode();
        String msg = wrapperCode.getMsg();
        if(code == null){
            throw new Error("wrapperCode的code不能为null");
        }
        if(msg == null){
            throw new Error("wrapperCode的msg不能为null");
        }
        return new Wrapper(code, msg, data);
    }
}
