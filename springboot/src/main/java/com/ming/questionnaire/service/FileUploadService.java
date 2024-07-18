package com.ming.questionnaire.service;

import com.ming.questionnaire.pojo.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

// 文件上传服务接口
public interface FileUploadService {

    ResponseResult uploadHeader(MultipartFile multipartFile);

}
