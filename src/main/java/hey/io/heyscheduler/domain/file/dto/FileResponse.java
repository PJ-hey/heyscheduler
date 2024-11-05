package hey.io.heyscheduler.domain.file.dto;

import hey.io.heyscheduler.domain.file.entity.File;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "변경된 파일 목록")
public record FileResponse(
    @Schema(description = "등록한 파일 수", example = "1")
    int count,

    @Schema(description = "파일 목록", nullable = true)
    List<FileResult> fileList
) {

    public static FileResponse of(List<File> files) {
        List<FileResult> fileResults = files.stream()
            .map(FileResult::of)
            .toList();

        return new FileResponse(
            fileResults.size(),
            fileResults
        );
    }

    public record FileResult(
        @Schema(description = "파일 ID", example = "35")
        Long fileId,

        @Schema(description = "관련 엔티티 유형", example = "PERFORMANCE")
        String entityType,

        @Schema(description = "관련 엔티티 ID", example = "15")
        Long entityId,

        @Schema(description = "파일 유형", example = "IMAGE")
        String fileType,

        @Schema(description = "파일 분류", example = "DETAIL")
        String fileCategory,

        @Schema(description = "파일명", example = "PF_PF249580_240923_164057.jpg")
        String fileName,

        @Schema(description = "파일 URL", example = "https://hey-bucket.s3.amazonaws.com/app/performance/15/66e0485b-ddd2-4b78-ae4d-55be8c2c7288.jpg")
        String fileUrl
    ) {

        public static FileResult of(File file) {
            return new FileResult(
                file.getFileId(),
                file.getEntityType().getCode(),
                file.getEntityId(),
                file.getFileType().getCode(),
                file.getFileCategory().getCode(),
                file.getFileName(),
                file.getFileUrl()
            );
        }
    }
}

