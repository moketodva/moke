package cn.moke.generator.service;


import cn.moke.generator.entity.vo.WeatherVo;

import java.io.IOException;

/**
 * @author moke
 */
public interface IpService {

    /**
     * 根据ip获取天气信息
     * @param ip ip
     * @return 天气
     * @throws IOException io异常
     */
    WeatherVo getWeatherByIp(String ip) throws IOException;

}
