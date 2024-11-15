package hey.io.heyscheduler.domain.performance.controller;

import hey.io.heyscheduler.common.config.swagger.ApiErrorCode;
import hey.io.heyscheduler.common.config.swagger.ApiErrorCodes;
import hey.io.heyscheduler.common.exception.ErrorCode;
import hey.io.heyscheduler.common.response.ApiResponse;
import hey.io.heyscheduler.domain.performance.dto.PerformanceResponse;
import hey.io.heyscheduler.domain.performance.dto.PerformanceSearch;
import hey.io.heyscheduler.domain.performance.service.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "1. Performance", description = "공연 관련 API")
public class PerformanceController {

    private final PerformanceService performanceService;

    /**
     * <p>공연 등록</p>
     *
     * @param searchDto 공연 조회 조건
     * @return 등록한 공연 목록
     */
    @Operation(summary = "공연 등록", description = "KOPIS 공연 데이터를 조회 후 등록합니다.")
    @ApiErrorCodes({ErrorCode.PERFORMANCE_NOT_FOUND, ErrorCode.PLACE_NOT_FOUND})
    @PostMapping("/performances")
    public ApiResponse<PerformanceResponse> createPerformances(@RequestBody PerformanceSearch searchDto) {
        return ApiResponse.created(performanceService.createPerformances(searchDto));
    }

    /**
     * <p>공연 일괄 수정</p>
     *
     * @param performanceIds 공연 ID 목록
     * @return 수정한 공연 정보 목록
     */
    @Operation(summary = "공연 일괄 수정", description = "해당 공연의 상태를 일괄적으로 수정합니다.")
    @ApiErrorCode(ErrorCode.INVALID_PERFORMANCE_ID)
    @PutMapping("/performances")
    public ApiResponse<PerformanceResponse> modifyPerformances(@RequestBody List<Long> performanceIds) {
        return ApiResponse.success(performanceService.modifyPerformances(performanceIds));
    }
}