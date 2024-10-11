package hey.io.heyscheduler;

import com.google.firebase.messaging.FirebaseMessagingException;
import hey.io.heyscheduler.kopis.service.KopisBatchService;
import java.time.LocalDate;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@RequiredArgsConstructor
@Component
public class BatchScheduler {
    private final KopisBatchService kopisBatchService;
    @Value("${kopis.performance.batch-count}")
    private int performanceBatchCount;

//    @Scheduled(
//            cron = "0 0 0/3 * * *"
//    )
//    public void updatePerformances() {
//        this.kopisBatchService.updatePerformancesBatch(LocalDate.now().minusMonths(1L), LocalDate.now().plusMonths(5L), this.performanceBatchCount);
//    }
//
//    @Scheduled(
//            cron = "10 0 0 * * *"
//    )
//    public void updatePerformanceState() {
//        this.kopisBatchService.updatePerformanceStatusBatch();
//    }
//
//    @Scheduled(
//            cron = "0 0 0 * * *"
//    )
//    public void sendPerformancesNotification() throws FirebaseMessagingException {
//        this.kopisBatchService.sendPerformancesNotification();
//    }

}
