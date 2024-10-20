package hey.io.heyscheduler.fcm.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import hey.io.heyscheduler.fcm.dto.MessageRequestDTO;
import hey.io.heyscheduler.fcm.service.FcmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Push", description = "PUSH 알림 API")
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/message/fcm/topic")
    @Operation(hidden = true)
    public ResponseEntity sendMessageTopic(@RequestBody MessageRequestDTO requestDTO) throws IOException, FirebaseMessagingException{
        fcmService.sendMessageByTopic(requestDTO.getTitle(), requestDTO.getBody(), requestDTO.getTopic());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/message/fcm/token")
    @Operation(hidden = true)
    public ResponseEntity sendMessageToken(@RequestBody MessageRequestDTO requestDTO) throws IOException, FirebaseMessagingException{
        fcmService.sendMessageByToken(requestDTO.getTitle(), requestDTO.getBody(), requestDTO.getTargetToken());
        return ResponseEntity.ok().build();
    }
}