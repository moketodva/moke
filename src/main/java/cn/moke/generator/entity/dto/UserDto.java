package cn.moke.generator.entity.dto;

import cn.moke.generator.constant.Group;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * @author moke
 */
@ApiModel(description = "用户")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户名")
    @NotEmpty(message = "用户名不能为空")
    @Length(min = 6, max = 30, message = "用户名长度控制在{min}-{max}范围内")
    private String username;

    @ApiModelProperty("密码")
    @NotEmpty(message = "密码不能为空", groups = Group.Add.class)
    @Length(min = 6, max = 100, message = "密码长度控制在{min}-{max}范围内", groups = Group.Add.class)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("头像地址")
    private String avatar;

    @ApiModelProperty("性别")
    private String gender;

    @ApiModelProperty("出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @ApiModelProperty("年龄")
    private Integer age;

    @ApiModelProperty("手机区号")
    @NotEmpty(message = "手机区号不能为空")
    private String phonePrefix;

    @ApiModelProperty("手机号")
    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp="^[1]([3-9])[0-9]{9}$", message="手机号格式错误")
    private String phone;

    @ApiModelProperty("邮箱")
    @Email(message = "邮箱格式错误")
    private String email;

    @ApiModelProperty("角色Id集合")
    private List<String> roleIds;

}
