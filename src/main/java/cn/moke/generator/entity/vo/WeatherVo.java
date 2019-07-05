package cn.moke.generator.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author moke
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherVo implements Serializable {

    private static final long serialVersionUID = 1635247468016605795L;

    private String city;

    private String weather;

    private String weatherPic;

    private String temperature;

    private String quality;
}
