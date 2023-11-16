package com.jwb.upload.service;


import java.io.IOException;

public interface UploadService {
    /**
     * 分片合并
     * @param filename 文件名称
     * @param totalChunks 分片总数
     * @param suffix 后缀
     */
    void mergeChunks(String filename, int totalChunks, String suffix) throws IOException;
}
