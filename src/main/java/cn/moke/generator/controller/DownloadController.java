package cn.moke.generator.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.moke.generator.entity.dto.ExcelDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author moke
 */
@Api(tags = "下载接口")
@RestController
@RequestMapping("/download")
@Validated
public class DownloadController {

    @ApiOperation("导出excel")
    @RequestMapping(value = "/excel", method = RequestMethod.POST)
    public void downloadExcel(@ApiParam("表头信息") @NotEmpty(message = "表头信息不能为空") @RequestParam(required = false) List<String> headers,
                              @ApiParam("表体信息") @NotEmpty(message = "表体信息不能为空") @RequestBody(required = false) List<Map<String, String>> data,
                              HttpServletResponse response) throws IOException {
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.writeHeadRow(headers);
        writer.write(data);
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition","attachment;filename=moke.xls");
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        writer.close();
        IoUtil.close(out);
    }

    @ApiOperation("导出excel（表头与数据映射关联）")
    @RequestMapping(value = "/excel/map", method = RequestMethod.POST)
    public void downloadExcelMap(@ApiParam("excel") @Valid @RequestBody ExcelDto excelDto,
                                 HttpServletResponse response) throws IOException {
        ExcelWriter writer = ExcelUtil.getWriter();
        List<Map<String, String>> headers = excelDto.getHeaders();
        List<Map<String, String>> data = excelDto.getData();
        headers.stream().peek((item) -> writer.addHeaderAlias(item.get("dataIndex"), item.get("title"))).count();
        writer.write(data);
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition","attachment;filename=moke.xls");
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        writer.close();
        IoUtil.close(out);
    }
}
