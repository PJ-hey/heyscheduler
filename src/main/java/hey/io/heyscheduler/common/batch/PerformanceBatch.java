package hey.io.heyscheduler.common.batch;

import hey.io.heyscheduler.domain.performance.dto.PerformanceSearch;
import hey.io.heyscheduler.domain.performance.service.PerformanceService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
@RequiredArgsConstructor
@Slf4j
public class PerformanceBatch {

    private final PerformanceService performanceService;

    /**
     * <p>공연 자동 등록</p>
     */
//    @Scheduled(cron = "0 45 23 * * *")
    public void createPerformances() {
        executeBatch("Create Performance", () -> {
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = LocalDate.now();
            performanceService.createPerformances(PerformanceSearch.of(startDate, endDate, null));
        });
    }

    /**
     * <p>티켓팅 임박 공연 알림 발송</p>
     */
//    @Scheduled(cron = "0 0,30 * * * *")
    public void ticketingPerformances() {
        executeBatch("Ticketing Performances", () -> performanceService.modifyPerformanceTicketings(LocalDateTime.now()));
    }

    /**
     * <p>공통 배치 실행 메서드</p>
     *
     * @param batchName  배치 이름
     * @param batchLogic 배치 로직
     */
    private void executeBatch(String batchName, Runnable batchLogic) {
        long start = System.currentTimeMillis();
        log.info("\n==============================");
        log.info("{} - Start Time : {}", batchName, LocalDateTime.now());

        try {
            batchLogic.run();
        } catch (Exception e) {
            log.error("{} - Error occurred: {}", batchName, e.getMessage(), e);
        }

        log.info("{} - Elapsed Time : {}ms", batchName, System.currentTimeMillis() - start);
        log.info("{} - End Time : {}", batchName, LocalDateTime.now());
        log.info("==============================\n");
    }
}