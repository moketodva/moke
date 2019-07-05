package cn.moke.generator.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author moke
 */
@ApiModel(description = "用户登录")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto implements Serializable {
    private static final long serialVersionUID = -76601736840442548L;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;
}
