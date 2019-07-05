package cn.moke.generator.service.impl;

import cn.moke.generator.entity.vo.WeatherVo;
import cn.moke.generator.enums.WrapperCode;
import cn.moke.generator.exception.BusinessException;
import cn.moke.generator.service.IpService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author moke
 */
@Service
public class IpServiceImpl implements IpService {

    @Resource
    private RestTemplate restTemplate;
    @Value("${moke.api.weather.url}")
    private String weatherUrl;
    @Value("${moke.api.weather.app-code}")
    private String weatherAppCode;
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public WeatherVo getWeatherByIp(String ip) throws IOException {
        Map<String, String> querys = new HashMap<>(16);
        querys.put("ip", "112.14.123.16");
        querys.put("need3HourForcast", "0");
        querys.put("needAlarm", "0");
        querys.put("needHourData", "0");
        querys.put("needIndex", "0");
        querys.put("needMoreDay", "0");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "APPCODE " + weatherAppCode);
        HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(weatherUrl, HttpMethod.GET, httpEntity, String.class, querys);
        if(!responseEntity.getStatusCode().is2xxSuccessful()){
            throw new BusinessException(WrapperCode.IP_WEATHER_FAIL);
        }
        JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
        String city = jsonNode.get("showapi_res_body").get("cityInfo").get("c5").toString().replace("\"", "");
        String weather = jsonNode.get("showapi_res_body").get("now").get("weather").toString().replace("\"", "");
        String weatherPic = jsonNode.get("showapi_res_body").get("now").get("weather_pic").toString().replace("\"", "");
        String temperature = jsonNode.get("showapi_res_body").get("now").get("temperature").toString().replace("\"", "");
        String quality = jsonNode.get("showapi_res_body").get("now").get("aqiDetail").get("quality").toString().replace("\"", "");

        return new WeatherVo(city, weather, weatherPic, temperature, quality);
    }
}
