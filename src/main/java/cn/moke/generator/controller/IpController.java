package cn.moke.generator.controller;

import cn.moke.generator.entity.vo.WeatherVo;
import cn.moke.generator.entity.vo.Wrapper;
import cn.moke.generator.service.IpService;
import cn.moke.generator.utils.IpUtil;
import cn.moke.generator.utils.WrapperUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * @author moke
 */
@Api(tags = "IP接口")
@RestController
@RequestMapping("/ip")
public class IpController {

    @Resource
    private IpService ipService;

    @ApiOperation("获取天气信息")
    @RequestMapping(value = "/weather", method = RequestMethod.GET)
    public Wrapper<WeatherVo> getWeatherByIp(HttpServletRequest request) throws IOException {
        String ip = IpUtil.getIp(request);
        WeatherVo weatherVo = ipService.getWeatherByIp(ip);
        return WrapperUtil.ok(weatherVo);
    }
}



