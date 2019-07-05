package cn.moke.generator.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

import java.time.LocalDateTime;
import java.util.List;


/**
 * @author moke
 */
@ApiModel(description="后端资源")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Path implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键", readOnly = true)
    private String id;

    @ApiModelProperty(value = "权限主键", readOnly = true)
    private String permissionId;

    @ApiModelProperty("后端资源名")
    @NotEmpty(message = "后端资源名不能为空")
    @Length(max = 30, message = "后端资源名长度不能超过{max}")
    private String name;

    @ApiModelProperty("后端资源uri（支持ant风格）")
    @NotEmpty(message = "后端资源uri（支持ant风格）不能为空")
    @Length(max = 120, message = "后端资源uri（支持ant风格）长度不能超过{max}")
    private String antUri;

    @ApiModelProperty("请求方法类型")
    private String method;

    @ApiModelProperty("是否激活")
    private Boolean isActive;

    @ApiModelProperty(value = "录入时间", readOnly = true)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "录入用户", readOnly = true)
    private String createUser;

    @ApiModelProperty(value = "更新时间", readOnly = true)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifyTime;

    @ApiModelProperty(value = "更新用户", readOnly = true)
    private String modifyUser;

    @TableField(exist = false)
    @ApiModelProperty(value = "权限", readOnly = true)
    private Permission permission;

    @TableField(exist = false)
    @ApiModelProperty(value = "角色集合", readOnly = true)
    private List<Role> roles;

}