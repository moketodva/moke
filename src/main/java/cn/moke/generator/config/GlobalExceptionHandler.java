package cn.moke.generator.config;

import cn.moke.generator.entity.vo.Wrapper;
import cn.moke.generator.enums.WrapperCode;
import cn.moke.generator.exception.BusinessException;
import cn.moke.generator.utils.WrapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author moke
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Wrapper businessExceptionHandler(BusinessException ex){
        return WrapperUtil.error(ex.getWrapperCode());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpMessageNotReadableException.class,MethodArgumentTypeMismatchException.class, MethodArgumentNotValidException.class,ConstraintViolationException.class})
    public Wrapper http400Handler(Exception e, HandlerMethod handlerMethod){
        if(e instanceof HttpMessageNotReadableException){
            return WrapperUtil.error(WrapperCode.EXCEPTION_JSON_PARSER);
        }else if(e instanceof MethodArgumentTypeMismatchException){
            return WrapperUtil.error(WrapperCode.EXCEPTION_TYPE);
        }else if(e instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException)e;
            List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
            List<String> message = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
            return WrapperUtil.error(WrapperCode.EXCEPTION_VALIDATE, message);
        }else{
            ConstraintViolationException exception = (ConstraintViolationException)e;
            List<String> message = exception.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
            return WrapperUtil.error(WrapperCode.EXCEPTION_VALIDATE, message);
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public Wrapper http403Handler(){
        return WrapperUtil.error(WrapperCode.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public Wrapper http404Handler(){
        return WrapperUtil.error(WrapperCode.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Wrapper http405Handler(){
        return WrapperUtil.error(WrapperCode.METHOD_NOT_ALLOWED);
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Wrapper http415Handler(){
        return WrapperUtil.error(WrapperCode.UNSUPPORTED_MEDIA_TYPE);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Wrapper http500Handler(Exception ex, HandlerMethod handlerMethod){
        log.error("系统异常信息: {}", ex.getMessage());
        log.error("-> 位置: {}", handlerMethod);
//        log.error("-> 系统异常信息栈: {}", ex.getStackTrace());
        ex.printStackTrace();
        return WrapperUtil.error();
    }
}
