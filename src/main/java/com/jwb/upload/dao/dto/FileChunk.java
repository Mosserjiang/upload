package com.jwb.upload.dao.dto;

import lombok.Data;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileChunk {
    /**
     * 文件
     */
    @NonNull
    private MultipartFile file;
    /**
     * 分片编号
     */
    @NonNull
    private Integer chunkNumber;
    /**
     * 总分片数
     */
    @NonNull
    private Integer totalChunk;
    /**
     * 文件名称
     */
    @NonNull
    private String fileName;
}
