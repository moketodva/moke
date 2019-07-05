package cn.moke.generator.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

import java.time.LocalDateTime;
import java.util.List;


/**
 * @author moke
 */
@ApiModel(description="前端路由")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Router implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private String id;

    @ApiModelProperty("权限主键")
    private String permissionId;

    @ApiModelProperty("父主键")
    private String parentId;

    @ApiModelProperty("路由名")
    private String name;

    @ApiModelProperty("相对路由路径")
    private String relativePath;

    @ApiModelProperty("渲染组件路径")
    private String component;

    @ApiModelProperty("是否精准匹配")
    private Boolean exact;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("操作类型")
    private String operateType;

    @ApiModelProperty("排序号")
    private Double sortNum;

    @ApiModelProperty("类型")
    private String type;
    @ApiModelProperty("是否启用")
    private Boolean isActive;

    @ApiModelProperty("录入时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty("录入用户")
    private String createUser;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifyTime;

    @ApiModelProperty("更新用户")
    private String modifyUser;

    @TableField(exist = false)
    @ApiModelProperty("绝对路由路径")
    private String AbsolutePath;

    @TableField(exist = false)
    @ApiModelProperty("是否选中")
    private boolean checked;

    @TableField(exist = false)
    @ApiModelProperty("权限")
    private Permission permission;

    @TableField(exist = false)
    @ApiModelProperty("路由子节点")
    private List<Router> children;
}