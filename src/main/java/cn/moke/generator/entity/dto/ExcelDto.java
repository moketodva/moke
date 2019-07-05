package cn.moke.generator.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

/**
 * @author moke
 */
@ApiModel(description = "excel")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelDto {

    @ApiModelProperty("表头信息")
    @NotEmpty(message = "表头信息不能为空")
    @Size(max = 100, message = "表头信息列数不能超过{max}")
    private List<Map<String, String>> headers;

    @ApiModelProperty("表体信息")
    @NotEmpty(message = "表体信息不能为空")
    @Size(max = 100, message = "表体信息行数不能超过{max}")
    private List<Map<String, String>> data;
}
