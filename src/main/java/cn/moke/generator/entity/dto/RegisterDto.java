package cn.moke.generator.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author moke
 */
@ApiModel(description = "注册表单")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto implements Serializable {

    private static final long serialVersionUID = 4235942334690853513L;

    @ApiModelProperty("用户名")
    @NotEmpty(message = "用户名不能为空")
    @Length(min = 6, max = 30, message = "用户名长度控制在{min}-{max}范围内")
    private String username;

    @ApiModelProperty("密码")
    @NotEmpty(message = "密码不能为空")
    @Length(min = 6, max = 100, message = "密码长度控制在{min}-{max}范围内")
    private String password;

    @ApiModelProperty("手机区号")
    @NotEmpty(message = "手机区号不能为空")
    private String phonePrefix;

    @ApiModelProperty("手机号码")
    @NotEmpty(message = "手机号码不能为空")
    @Pattern(regexp="^[1]([3-9])[0-9]{9}$", message="手机号码错误")
    private String phone;

    @ApiModelProperty("短信验证码")
    @NotEmpty(message = "短信验证码不能为空")
    @Length(min=6, max = 6, message = "短信验证码长度不正常")
    private String smsCode;

    @ApiModelProperty("验证码")
    @NotEmpty(message = "验证码不能为空")
    @Length(min=4, max = 4, message = "验证码长度不正常")
    private String captcha;

    @ApiModelProperty("验证码id")
    @NotEmpty(message = "验证码id不能为空")
    @Length(max = 100, message = "验证码id长度控制在{max}以内")
    private String captchaId;
}
