package com.jwb.upload.service.impl;


import com.jwb.upload.constant.Constant;
import com.jwb.upload.exception.ApiException;
import com.jwb.upload.service.UploadService;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UploadServiceImpl implements UploadService {
    @Override
    public void mergeChunks(String filename, int totalChunks, String suffix) throws IOException{
        Path uploadPath = Paths.get(Constant.UPLOAD_DIR, filename);
        Path finalFilePath = Paths.get(uploadPath.toString(), filename + "." + suffix);

        // Merge chunks into final file
        try (OutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(finalFilePath.toFile().toPath()))) {
            //校验大小
            long fileSize = 0L;
            for (int i = 1; i <= totalChunks; i++) {
                fileSize += calculateFileSize(Constant.UPLOAD_DIR + filename + ".part" + i);
            }
            if(fileSize > Constant.MAX_FILE_SIZE){
                throw new ApiException("文件大小超出限制");
            }
            for (int i = 1; i <= totalChunks; i++) {
                Path chunkPath = Paths.get(uploadPath.toString(), filename + ".part" + i);
                Files.copy(chunkPath, outputStream);
                Files.delete(chunkPath);
            }
        }
    }

    // 计算文件大小的方法
    public static long calculateFileSize(String filePath) {
        Path path = Paths.get(filePath);
        try {
            return Files.size(path);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // 将文件大小格式化为人类可读的形式
    public static String formatFileSize(long fileSize) {
        if (fileSize <= 0) {
            return "0 B";
        }

        // 定义文件大小单位
        String[] units = {"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};

        int digitGroups = (int) (Math.log10(fileSize) / Math.log10(1024));

        return String.format("%.2f %s", fileSize / Math.pow(1024, digitGroups), units[digitGroups]);
    }

}
