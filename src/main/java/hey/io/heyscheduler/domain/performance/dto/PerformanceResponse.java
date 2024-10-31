package hey.io.heyscheduler.domain.performance.dto;

import hey.io.heyscheduler.domain.performance.entity.Performance;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "등록된 공연 목록")
public record PerformanceResponse(
    @Schema(description = "등록 공연 수", example = "3")
    int count,

    @Schema(description = "공연 목록", nullable = true)
    List<PerformanceResult> performanceList
) {

    public static PerformanceResponse of(List<Performance> performances) {
        List<PerformanceResult> performanceResults = performances.stream()
            .map(PerformanceResult::of)
            .toList();

        return new PerformanceResponse(
            performanceResults.size(),
            performanceResults
        );
    }

    public record PerformanceResult(
        @Schema(description = "공연 ID", example = "168")
        Long performanceId,

        @Schema(description = "공연명", example = "경기인디뮤직페스티벌")
        String name
    ) {

        public static PerformanceResult of(Performance performance) {
            return new PerformanceResult(
                performance.getPerformanceId(),
                performance.getName()
            );
        }
    }
}
