package hey.io.heyscheduler.batch;

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

//    @Scheduled(cron = "0 45 23 * * *")
    public void createPerformances() {
        long start = System.currentTimeMillis();
        log.info("\n==============================");
        log.info("Create Performance - Start Time : {}", LocalDateTime.now());

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now();
        performanceService.createPerformances(PerformanceSearch.of(startDate, endDate, null));

        log.info("Create Performance - Elapsed Time : {}ms", System.currentTimeMillis() - start);
        log.info("Create Performance - End Time : {}", LocalDateTime.now());
        log.info("==============================\n");
    }
}