package cn.moke.generator.entity.po;

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


/**
 * @author moke
 */
@ApiModel(description="字典")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dict implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键", readOnly = true)
    private String id;

    @ApiModelProperty("字典类型")
    @NotEmpty(message = "字典类型不能为空")
    @Length(max = 30, message = "字典类型长度不能超过{max}")
    private String type;

    @ApiModelProperty("字典类型描述")
    @NotEmpty(message = "字典类型描述不能为空")
    @Length(max = 30, message = "字典类型描述长度不能超过{max}")
    private String description;

    @ApiModelProperty("字典名")
    @NotEmpty(message = "字典名不能为空")
    @Length(max = 30, message = "字典名长度不能超过{max}")
    private String name;

    @ApiModelProperty("字典值")
    @NotEmpty(message = "字典值不能为空")
    @Length(max = 30, message = "字典值长度不能超过{max}")
    private String value;

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

}