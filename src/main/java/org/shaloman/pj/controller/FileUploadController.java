package org.shaloman.pj.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.shaloman.pj.service.FileUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 파일 업로드 RESTful API 컨트롤러.
 */
@RestController
@RequestMapping("/api/files")
@Tag(name = "파일 업로드", description = "파일 업로드/삭제 API")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/upload")
    @Operation(summary = "단일 파일 업로드", description = "파일을 업로드하고 저장된 파일 정보를 반환한다.")
    public ResponseEntity<Map<String, String>> upload(
            @Parameter(description = "업로드할 파일") @RequestParam("file") MultipartFile file,
            @Parameter(description = "카테고리 (하위 디렉토리명)") @RequestParam(value = "category", required = false, defaultValue = "general") String category) {
        Map<String, String> result = fileUploadService.upload(file, category);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/uploadMultiple")
    @Operation(summary = "다중 파일 업로드", description = "여러 파일을 한 번에 업로드하고 저장된 파일 정보 목록을 반환한다.")
    public ResponseEntity<List<Map<String, String>>> uploadMultiple(
            @Parameter(description = "업로드할 파일 목록") @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "카테고리 (하위 디렉토리명)") @RequestParam(value = "category", required = false, defaultValue = "general") String category) {
        List<Map<String, String>> results = new ArrayList<>();
        for (MultipartFile file : files) {
            results.add(fileUploadService.upload(file, category));
        }
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "파일 삭제", description = "업로드된 파일을 삭제한다.")
    public ResponseEntity<Void> delete(
            @Parameter(description = "파일 URL 경로 (예: /uploads/category/file.png)") @RequestParam("filePath") String filePath) {
        fileUploadService.delete(filePath);
        return ResponseEntity.noContent().build();
    }
}