package cn.moke.generator.controller;

import cn.moke.generator.entity.vo.Wrapper;
import cn.moke.generator.enums.FileType;
import cn.moke.generator.enums.WrapperCode;
import cn.moke.generator.service.UploadService;
import cn.moke.generator.utils.Base64FileUtil;
import cn.moke.generator.utils.FileTypeUtil;
import cn.moke.generator.utils.WrapperUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * @author moke
 */
@Api(tags = "上传接口")
@RestController
@RequestMapping("/upload")
@Validated
public class UploadController {

    @Resource
    private UploadService uploadService;

    @ApiOperation("头像上传")
    @RequestMapping(value = "/avatar", method = RequestMethod.POST)
    public Wrapper uploadAvatar(@ApiParam("图片文件") @NotNull(message = "图片文件不能为空") @RequestParam(required = false) MultipartFile file) throws IOException {
        FileType fileType = FileTypeUtil.getFileType(file.getBytes());
        boolean isPicture = FileTypeUtil.isPicture(fileType);
        if(!isPicture){
            return WrapperUtil.error(WrapperCode.FILETYPE_ERROR);
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String url = uploadService.upload(file.getBytes(), username + fileType.getSuffix());
        return WrapperUtil.ok(url);
    }

    @ApiOperation("头像上传（base64）")
    @RequestMapping(value = "/base64/avatar", method = RequestMethod.POST)
    public Wrapper uploadBase64Avatar(@ApiParam("图片base64格式") @NotEmpty(message = "图片base64格式不能为空") @RequestParam(required = false) String data64) throws IOException {
        Base64 base64 = new Base64();
        byte[] byteData = base64.decode(Base64FileUtil.getData(data64));
        FileType fileType = FileTypeUtil.getFileType(byteData);
        boolean isPicture = FileTypeUtil.isPicture(fileType);
        if(!isPicture){
            return WrapperUtil.error(WrapperCode.FILETYPE_ERROR);
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String url = uploadService.upload(byteData, username + fileType.getSuffix());
        return WrapperUtil.ok(url);
    }
}
