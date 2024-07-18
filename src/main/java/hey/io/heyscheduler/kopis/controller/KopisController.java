//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package hey.io.heyscheduler.kopis.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import hey.io.heyscheduler.common.response.SuccessResponse;
import hey.io.heyscheduler.kopis.dto.KopisBatchUpdateRequest;
import hey.io.heyscheduler.kopis.service.KopisBatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/batch"})
public class KopisController {
    private final KopisBatchService kopisBatchService;

    @GetMapping({"/performances"})
    public ResponseEntity<SuccessResponse<Integer>> updatePerformancesBatch(@RequestBody @Valid KopisBatchUpdateRequest request) {
        return SuccessResponse.of(this.kopisBatchService.updatePerformancesBatch(request.getFrom(), request.getTo(), request.getRows())).asHttp(HttpStatus.OK);
    }

    @GetMapping({"/performances/status"})
    public ResponseEntity<SuccessResponse<Integer>> updatePerformanceStatusBatch() {
        return SuccessResponse.of(this.kopisBatchService.updatePerformanceStatusBatch()).asHttp(HttpStatus.OK);
    }

    @GetMapping({"/performances/rank"})
    public ResponseEntity<SuccessResponse<Integer>> updateBoxOfficeRankBatch() {
        return SuccessResponse.of(this.kopisBatchService.updateBoxOfficeRankBatch()).asHttp(HttpStatus.OK);
    }

    @GetMapping({"/performances/notification"})
    public ResponseEntity<SuccessResponse<Integer>> sendPerformancesNotification() throws FirebaseMessagingException {
        return SuccessResponse.of(this.kopisBatchService.sendPerformancesNotification()).asHttp(HttpStatus.OK);
    }

}
