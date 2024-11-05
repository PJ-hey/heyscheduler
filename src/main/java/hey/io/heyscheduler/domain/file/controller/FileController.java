package hey.io.heyscheduler.domain.file.controller;

import hey.io.heyscheduler.common.config.swagger.ApiErrorCode;
import hey.io.heyscheduler.common.config.swagger.ApiErrorCodes;
import hey.io.heyscheduler.common.exception.ErrorCode;
import hey.io.heyscheduler.common.response.ApiResponse;
import hey.io.heyscheduler.domain.file.dto.FileResponse;
import hey.io.heyscheduler.domain.file.service.FileService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "9. File", description = "파일 관련 API")
@Hidden
public class FileController {

    private final FileService fileService;

    /**
     * <p>파일 등록</p>
     *
     * @param multipartFile 파일 정보
     * @return 등록한 파일 목록
     */
    @Operation(summary = "파일 등록", description = "새로운 파일을 등록합니다.")
    @ApiErrorCode(ErrorCode.FILE_SERVER_ERROR)
    @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileResponse> createFiles(
        @RequestPart MultipartFile multipartFile) {
        return ApiResponse.created(fileService.createFile(multipartFile, "performance/3"));
    }

    /**
     * <p>파일 삭제</p>
     *
     * @param fileId 파일 ID
     * @return 삭제한 파일 목록
     */
    @Operation(summary = "파일 삭제", description = "기존 파일을 삭제합니다.")
    @ApiErrorCodes({ErrorCode.FILE_SERVER_ERROR, ErrorCode.FILE_NOT_FOUND})
    @DeleteMapping("/files/{id}")
    public ApiResponse<FileResponse> removeFiles(@PathVariable("id") Long fileId) {
        return ApiResponse.success(fileService.removeFiles(fileId));
    }
}