package com.jwb.upload.controller;

import com.jwb.upload.constant.Constant;
import com.jwb.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private UploadService uploadService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("chunkNumber") int chunkNumber,
                                             @RequestParam("totalChunks") int totalChunks,
                                             @RequestParam("suffix") String suffix,
                                             @RequestParam("filename") String filename) {
        try {
            // Create directory if not exists
            Path uploadPath = Paths.get(Constant.UPLOAD_DIR, filename);
            Files.createDirectories(uploadPath);

            // Save the chunk
            Path chunkPath = Paths.get(uploadPath.toString(), filename + ".part" + chunkNumber);
            Files.copy(file.getInputStream(), chunkPath, StandardCopyOption.REPLACE_EXISTING);

            // Check if all chunks are uploaded
            if (chunkNumber == totalChunks) {
                // All chunks are uploaded, now merge them
                uploadService.mergeChunks(filename, totalChunks, suffix);
            }
            return ResponseEntity.ok("Chunk uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading chunk");
        }
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(Constant.UPLOAD_DIR, filename);
            byte[] fileContent = FileCopyUtils.copyToByteArray(filePath.toFile());

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + filename)
                    .body(fileContent);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
