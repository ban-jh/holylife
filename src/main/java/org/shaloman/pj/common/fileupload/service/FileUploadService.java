package org.shaloman.pj.common.fileupload.service;

import org.shaloman.pj.config.FileUploadConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 파일 업로드 공통 서비스.
 * 파일 저장/삭제 기능을 제공한다.
 * 보안: 확장자 화이트리스트, 경로 순회(Path Traversal) 방지
 */
@Service
public class FileUploadService {

    private final FileUploadConfig config;

    // 취약점 #3: 허용 확장자 화이트리스트
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        "jpg", "jpeg", "png", "gif", "svg", "webp", "ico", "pdf"
    );

    public FileUploadService(FileUploadConfig config) {
        this.config = config;
    }

    /**
     * 단일 파일 업로드.
     * @param file 업로드할 파일
     * @param category 카테고리 (하위 디렉토리명)
     * @return 업로드 결과 (url, fileName, savedFileName, fileSize, fileType)
     */
    public Map<String, String> upload(MultipartFile file, String category) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있거나 존재하지 않습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("파일명이 유효하지 않습니다.");
        }

        // 취약점 #3: 확장자 화이트리스트 검증
        validateFileExtension(originalFilename);

        // 카테고리 기본값
        if (category == null || category.isBlank()) {
            category = "general";
        }

        // 취약점 #4: 경로 순회(Path Traversal) 방지 — category 검증
        validateCategory(category);

        // 업로드 디렉토리 생성
        Path uploadDir = Paths.get(config.getPath(), category);
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("업로드 디렉토리 생성 실패: " + uploadDir, e);
        }

        // 저장 파일명: UUID-원본파일명
        String savedFileName = UUID.randomUUID().toString() + "-" + originalFilename;
        Path targetPath = uploadDir.resolve(savedFileName).normalize();

        // 취약점 #4: 최종 파일 경로가 업로드 기준 디렉토리 내부인지 확인
        Path basePath = Paths.get(config.getPath()).normalize().toAbsolutePath();
        if (!targetPath.toAbsolutePath().startsWith(basePath)) {
            throw new IllegalArgumentException("잘못된 파일 경로입니다.");
        }

        try {
            file.transferTo(targetPath.toFile());
        } catch (IOException e) {
            throw new IllegalStateException("파일 저장 실패: " + targetPath, e);
        }

        // 결과 반환
        String url = config.getUrlPrefix() + "/" + category + "/" + savedFileName;
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        result.put("fileName", originalFilename);
        result.put("savedFileName", savedFileName);
        result.put("fileSize", String.valueOf(file.getSize()));
        result.put("fileType", file.getContentType());

        return result;
    }

    /**
     * 파일 삭제.
     * @param filePath URL 경로 (예: /uploads/category/file.png)
     */
    public void delete(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("파일 경로가 유효하지 않습니다.");
        }

        // 취약점 #4: 경로 순회 문자 차단
        if (filePath.contains("..") || filePath.contains("\\")) {
            throw new IllegalArgumentException("잘못된 파일 경로입니다.");
        }

        Path diskPath = Paths.get(getDiskPath(filePath)).normalize();

        // 취약점 #4: 삭제 대상이 업로드 기준 디렉토리 내부인지 확인
        Path basePath = Paths.get(config.getPath()).normalize().toAbsolutePath();
        if (!diskPath.toAbsolutePath().startsWith(basePath)) {
            throw new IllegalArgumentException("잘못된 파일 경로입니다.");
        }

        try {
            Files.deleteIfExists(diskPath);
        } catch (IOException e) {
            throw new IllegalStateException("파일 삭제 실패: " + diskPath, e);
        }
    }

    /**
     * URL 경로를 디스크 경로로 변환.
     * @param urlPath URL 경로 (예: /uploads/category/file.png)
     * @return 디스크 경로 Path
     */
    public String getDiskPath(String urlPath) {
        // URL 접두사(/uploads)를 디스크 경로(/opt/holylife/uploads)로 치환
        String prefix = config.getUrlPrefix();
        if (urlPath.startsWith(prefix)) {
            return config.getPath() + urlPath.substring(prefix.length());
        }
        // 접두사가 없는 경우 그대로 붙임
        return config.getPath() + "/" + urlPath;
    }

    /**
     * URL 경로를 디스크 Path 객체로 변환.
     */
    public Path getDiskPathAsPath(String urlPath) {
        return Paths.get(getDiskPath(urlPath));
    }

    /**
     * 취약점 #3: 파일 확장자 화이트리스트 검증.
     */
    private void validateFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot <= 0) {
            throw new IllegalArgumentException("허용되지 않는 파일 확장자입니다.");
        }
        String extension = filename.substring(lastDot + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("허용되지 않는 파일 확장자입니다: " + extension);
        }
    }

    /**
     * 취약점 #4: 카테고리 파라미터 경로 순회 문자 차단.
     */
    private void validateCategory(String category) {
        if (category.contains("..") || category.contains("/") || category.contains("\\") || category.contains(":")) {
            throw new IllegalArgumentException("잘못된 카테고리 경로입니다.");
        }
    }
}