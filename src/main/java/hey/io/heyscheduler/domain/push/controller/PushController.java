package hey.io.heyscheduler.domain.push.controller;

import hey.io.heyscheduler.common.response.ApiResponse;
import hey.io.heyscheduler.domain.push.dto.PushRequest;
import hey.io.heyscheduler.domain.push.dto.PushResponse;
import hey.io.heyscheduler.domain.push.service.PushService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "3. Push", description = "PUSH 알림 API")
@Hidden
public class PushController {

    private final PushService pushService;

    @PostMapping("/push/topic")
    public ApiResponse<PushResponse> sendMessageTopic(@RequestBody PushRequest pushRequest) {
        return ApiResponse.created(pushService.sendMessageByTopic(pushRequest));
    }

    @PostMapping("/push/token")
    public ApiResponse<String> sendMessageToken(@RequestBody PushRequest pushRequest) {
        String messageId = pushService.sendMessageByToken(pushRequest);
        return ApiResponse.created(messageId);
    }
}