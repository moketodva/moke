package cn.moke.generator.utils;

import cn.moke.generator.entity.vo.Wrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author moke
 */
@Slf4j
@Component
public class ResponseUtil {

    private static ObjectMapper objectMapper;

    public static void writeJson(HttpServletResponse response, HttpStatus httpStatus, Wrapper wrapper){

        // 该response已经响应
        if(response.isCommitted()){
           return;
        }

        // 状态码默认200
        if(httpStatus == null){
            httpStatus = HttpStatus.OK;
        }

        PrintWriter out = null;
        try {
            String jsonStr = objectMapper.writeValueAsString(wrapper);
            response.setStatus(httpStatus.value());
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            out = response.getWriter();
            out.write(jsonStr);
        } catch (JsonProcessingException e) {
            log.error("{}", e);
        } catch (IOException e) {
            log.error("{}", e);
        }finally {
            if(out != null){
                out.close();
            }
        }
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        ResponseUtil.objectMapper = objectMapper;
    }
}
