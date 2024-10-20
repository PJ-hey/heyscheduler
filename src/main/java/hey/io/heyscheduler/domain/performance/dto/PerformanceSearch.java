package hey.io.heyscheduler.domain.performance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Builder
@Schema(description = "공연 검색 조건")
public class PerformanceSearch {

    @Schema(description = "공연 시작일", format = "yyyy-MM-dd", example = "2024-10-01")
    private LocalDate startDate;

    @Schema(description = "공연 종료일", format = "yyyy-MM-dd", example = "2024-10-06")
    private LocalDate endDate;

    @Schema(description = "공연명", nullable = true, example = "서울숲 재즈 페스티벌")
    private String name;

    public static PerformanceSearch of(LocalDate startDate, LocalDate endDate, String name) {
        return PerformanceSearch.builder()
            .startDate(startDate)
            .endDate(endDate)
            .name(name)
            .build();
    }
}