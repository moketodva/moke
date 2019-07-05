package cn.moke.generator.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author moke
 */
@ApiModel("密码修改表单")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PwdAlterDto implements Serializable {

    private static final long serialVersionUID = -76601736840442548L;

    @ApiModelProperty("旧密码")
    @NotEmpty(message = "旧密码不能为空")
    @Length(min = 6, max = 40, message = "旧密码长度控制在6-40范围内")
    private String oldPassword;

    @ApiModelProperty("新密码")
    @NotEmpty(message = "新密码不能为空")
    @Length(min = 6, max = 40, message = "新密码长度控制在6-40范围内")
    private String newPassword;
}
