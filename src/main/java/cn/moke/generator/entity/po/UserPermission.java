package cn.moke.generator.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @author moke
 */
@ApiModel(description="用户-权限")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private String id;

    @ApiModelProperty("用户主键")
    private String userId;

    @ApiModelProperty("权限主键")
    private String permissionId;
}