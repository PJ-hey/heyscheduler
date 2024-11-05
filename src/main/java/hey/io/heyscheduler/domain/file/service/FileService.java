package hey.io.heyscheduler.domain.file.service;

import hey.io.heyscheduler.common.exception.ErrorCode;
import hey.io.heyscheduler.common.exception.notfound.EntityNotFoundException;
import hey.io.heyscheduler.common.exception.servererror.ServerErrorException;
import hey.io.heyscheduler.domain.file.dto.FileResponse;
import hey.io.heyscheduler.domain.file.entity.File;
import hey.io.heyscheduler.domain.file.enums.EntityType;
import hey.io.heyscheduler.domain.file.enums.FileCategory;
import hey.io.heyscheduler.domain.file.enums.FileType;
import hey.io.heyscheduler.domain.file.repository.FileRepository;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileRepository fileRepository;
    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    private final String rootPath = "app/";

    /**
     * <p>파일 등록</p>
     *
     * @param multipartFile  파일 정보
     * @param uploadFilePath 파일 경로
     * @return 등록한 파일 목록
     */
    @Transactional
    public FileResponse createFile(MultipartFile multipartFile, String uploadFilePath) {
        String fileUrl = uploadS3File(multipartFile, uploadFilePath);

        File file = File.builder()
            .entityType(EntityType.PERFORMANCE)
            .entityId(3L)
            .fileType(FileType.IMAGE)
            .fileCategory(FileCategory.DETAIL)
            .fileUrl(fileUrl)
            .build();
        return FileResponse.of(List.of(file));
    }

    /**
     * <p>파일 등록</p>
     *
     * @param files 파일 목록
     * @return 등록한 파일 목록
     */
    public FileResponse createFiles(List<File> files) {
        files.parallelStream().forEach(file -> {
            String uploadUrl = uploadS3FileFromUrl(file);
            file.updateFileUrl(uploadUrl);
        });

        List<File> response = fileRepository.saveAll(files);
        return FileResponse.of(response);
    }

    /**
     * <p>파일 삭제</p>
     *
     * @param fileId 파일 ID
     * @return 삭제한 파일 목록
     */
    @Transactional
    public FileResponse removeFiles(Long fileId) {
        File file = fileRepository.findById(fileId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FILE_NOT_FOUND));
        deleteS3File(file.getFileUrl());

        return FileResponse.of(List.of(file));
    }

    /**
     * <p>파일 업로드</p>
     *
     * @param multipartFile  파일 정보
     * @param uploadFilePath 파일 경로
     * @return 업로드한 파일 URL
     */
    private String uploadS3File(MultipartFile multipartFile, String uploadFilePath) {
        String orgFileName = Objects.requireNonNull(multipartFile.getOriginalFilename());
        String uploadUrl;

        try {
            String key = rootPath + uploadFilePath + "/" + getUuidFileName(orgFileName);

            S3Resource s3Resource = s3Template.upload(bucketName, key, multipartFile.getInputStream());
            uploadUrl = s3Resource.getURL().toString();
        } catch (IOException | URISyntaxException e) {
            log.error("S3 Upload file failed", e);
            throw new ServerErrorException(ErrorCode.FILE_SERVER_ERROR);
        }

        return uploadUrl;
    }

    /**
     * <p>파일 URL 로부터 파일 업로드</p>
     *
     * @param file 파일 정보
     * @return 업로드한 파일 URL
     */
    private String uploadS3FileFromUrl(File file) {
        String fileUrl = file.getFileUrl();
        String entityType = file.getEntityType().getCode().toLowerCase();
        String entityId = String.valueOf(file.getEntityId());
        String uploadUrl;

        try (InputStream inputStream = new URI(fileUrl).toURL().openStream()) {
            String key = rootPath + entityType + "/" + entityId + "/" + getUuidFileName(fileUrl);

            S3Resource s3Resource = s3Template.upload(bucketName, key, inputStream);
            uploadUrl = s3Resource.getURL().toString();
        } catch (IOException | URISyntaxException e) {
            log.error("S3 Upload file from URL failed", e);
            throw new ServerErrorException(ErrorCode.FILE_SERVER_ERROR);
        }

        return uploadUrl;
    }

    /**
     * <p>UUID 파일명 생성</p>
     *
     * @param fileUrl 파일 URL
     * @return UUID 파일명
     */
    private String getUuidFileName(String fileUrl) throws IOException, URISyntaxException {
        int lastIndex = fileUrl.indexOf(".");
        String ext = fileUrl.substring(lastIndex + 1);

        if (lastIndex != -1 && lastIndex < fileUrl.length() - 1 && !fileUrl.substring(lastIndex + 1).contains("/")) {
            ext = fileUrl.substring(lastIndex + 1);
        } else {
            // 확장자가 없는 경우 Content-Type 을 이용해 확장자 추출
            HttpURLConnection connection = (HttpURLConnection) new URI(fileUrl).toURL().openConnection();
            String contentType = connection.getContentType();
            if (contentType != null) {
                ext = getExtensionFromContentType(contentType);
            }
        }

        return UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);
    }

    /**
     * <p>Content-Type 으로부터 파일 확장자 추출</p>
     *
     * @param contentType Content-Type 값
     * @return 파일 확장자
     */
    private String getExtensionFromContentType(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            default -> "";
        };
    }

    /**
     * <p>파일 삭제</p>
     *
     * @param key 파일 업로드 경로 + 파일명 (relative path)
     */
    private void deleteS3File(String key) {
        try {
            s3Template.deleteObject(bucketName, key);
        } catch (Exception e) {
            log.error("S3 Delete file failed", e);
            throw new ServerErrorException(ErrorCode.FILE_SERVER_ERROR);
        }
    }
}
