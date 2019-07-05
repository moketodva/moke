package cn.moke.generator.config;

import cn.moke.generator.entity.po.User;
import cn.moke.generator.entity.vo.Wrapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * 作用：
 *      1、在访问Controller记录相关日志
 *      2、参数校验处理
 * @author moke
 */
@Aspect
@Component
@Order(0)
@Slf4j
public class GlobalLog {

    private ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final ExecutableValidator validator = factory.getValidator().forExecutables();


    private <T> Set<ConstraintViolation<T>> validMethodParams(T obj, Method method, Object[] params) {
        return validator.validateParameters(obj, method, params);
    }

    /**
     * 切点
     */
    @Pointcut("execution(public * cn.moke.generator.controller.*.*(..))")
    public void pointCut(){};

    /**
     * 【环绕通知】 打印请求的方式、url、协议,ip地址,请求的方法名、参数,最后处理参数校验结果
     * @param pjp 连接点（切点的具体化）,包含了连接点的信息
     * @return
     * @throws Throwable
     */
    @Around("pointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        log.info("------------------ start ----------------");
        // 通过springmvc提供的RequestContextHolder拿到HttpServletRequest对象
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // ip:port
        String remoteAddr = request.getRemoteAddr();
        int remotePort = request.getRemotePort();
        log.info("IP: {}:{}", remoteAddr, remotePort);

        // 用户
        log.info("User: {}", username);

        // http协议的请求行
        // 请求方法
        String method = request.getMethod();
        // 请求的URI
        String requestURI = request.getRequestURI();
        // 请求协议
        String protocol = request.getProtocol();
        log.info("Request Line: {} {} {}", method, requestURI, protocol);

        // 方法名
        String name = pjp.getSignature().getName();
        // 参数
        Object[] args = pjp.getArgs();
        // 拼接参数
        String paramsStr = joinArgs(args);
        log.info("Method: {}({})", name, paramsStr);

        return pjp.proceed();
    }

    /**
     * 【返回通知】 打印响应结果
     * @param response
     * @return
     */
    @AfterReturning(pointcut = "pointCut()", returning = "response")
    public Object afterReturning(Object response){
        if(response instanceof Wrapper){
            log.info("Response: {}", ((Wrapper)response).toString());
        }
        log.info("------------------- end -----------------");
        return response;
    }

    /**
     * 拼接请求参数成字符串,以逗号+空格拼接
     * @param args
     * @return String
     */
    private String joinArgs(Object[] args){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < args.length;i++){
            if(args[i] instanceof BeanPropertyBindingResult){
                continue;
            }
            if(i != 0){
                sb.append(", ");
            }
            sb.append(args[i]);
        }
        return sb.toString();
    }

//    /**
//     * 参数校验（Aop）
//     * @param pjp
//     * @return Object
//     * @throws Throwable
//     */
//    private Object vaildateParams(ProceedingJoinPoint pjp) throws Throwable {
//        //取参数，如果没参数，那肯定不校验了
//        Object[] objects = pjp.getArgs();
//        if (objects.length == 0) {
//            return pjp.proceed();
//        }
//        // 校验封装好的javabean
//        // 寻找带BindingResult参数的方法，然后判断是否有error，如果有则是校验不通过
//        for (Object object : objects) {
//            if (object instanceof BeanPropertyBindingResult) {
//                // 有校验
//                BeanPropertyBindingResult result = (BeanPropertyBindingResult) object;
//                StringBuilder sb = new StringBuilder();
//                if (result.hasErrors()) {
//                    List<ObjectError> list = result.getAllErrors();
//                    for (int i = 0; i < list.size();i++){
//                        ObjectError error = list.get(i);
////                        // code
////                        String code = error.getCode();
////                        // 校验参数
////                        Object[] arguments = error.getArguments();
////                        // 校验信息
////                        String defaultMessage = error.getDefaultMessage();
//                        // 返回第一条校验失败信息。也可以拼接起来返回所有的
//                        if(i != 0){
//                            sb.append("->");
//                        }
//                        sb.append(error.getDefaultMessage());
//                    }
//                    return WrapperUtil.error(WrapperCode.EXCEPTION_VALIDATE, sb.toString());
//                }
//            }
//        }
//
//        // 校验普通参数
//        // 获得切入目标对象
//        Object target = pjp.getThis();
//        // 获得切入的方法
//        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
//        // 执行校验，获得校验结果
//        Set<ConstraintViolation<Object>> validResult = validMethodParams(target, method, objects);
//        //如果有校验不通过的
//        if (!validResult.isEmpty()) {
//            // 获得方法的参数名称
////            String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
//            StringBuilder sb = new StringBuilder();
//            int i = 0;
//            for(ConstraintViolation<Object> constraintViolation : validResult) {
////
////                // 获得校验的参数路径信息
////                PathImpl pathImpl = (PathImpl) constraintViolation.getPropertyPath();
////                // 获得校验的参数位置
////                int paramIndex = pathImpl.getLeafNode().getParameterIndex();
////                // 获得校验的参数名称
////                String paramName = parameterNames[paramIndex];
//
//                if(i != 0){
//                    sb.append("->");
//                }
//                sb.append(constraintViolation.getMessage());
//
//                i++;
//            }
//            log.info("校验信息: {}", sb.toString());
//            //返回
//            return WrapperUtil.error(WrapperCode.EXCEPTION_VALIDATE, sb.toString());
//        }
//
//        return pjp.proceed();
//    }
}