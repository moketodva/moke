package cn.moke.generator.exception;

import cn.moke.generator.enums.WrapperCode;

/**
 * @author moke
 */
public class BusinessException extends RuntimeException {

    private WrapperCode wrapperCode;

    public BusinessException() {}

    public BusinessException(WrapperCode wrapperCode) {
        super(wrapperCode == null ? null : wrapperCode.getMsg());
        this.wrapperCode = wrapperCode;
    }

    public BusinessException(WrapperCode wrapperCode, String appendMsg) {
        super(wrapperCode == null ? null : wrapperCode.getMsg() + appendMsg);
        this.wrapperCode = wrapperCode;
    }

    public WrapperCode getWrapperCode() {
        return wrapperCode;
    }
}
