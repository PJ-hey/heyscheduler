package hey.io.heyscheduler.domain.push.service;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import com.google.firebase.messaging.TopicManagementResponse;
import hey.io.heyscheduler.common.exception.ErrorCode;
import hey.io.heyscheduler.common.exception.servererror.ServerErrorException;
import hey.io.heyscheduler.domain.push.dto.PushRequest;
import hey.io.heyscheduler.domain.push.dto.PushResponse;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PushService {

    @Value("${firebase.project-id}")
    private String projectId;

    @Value("${firebase.key-path}")
    private String keyPath;

    @PostConstruct
    public void initialize() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(keyPath).getInputStream()))
                .setProjectId(projectId)
                .build();

            FirebaseApp.initializeApp(options);
        }
    }

    /**
     * <p>토픽 기준 알림 전송</p>
     *
     * @param pushRequest 알림 전송 정보
     * @return 전송 성공/실패 수
     */
    public PushResponse sendMessageByTopic(PushRequest pushRequest) {
        int successCount, failureCount;
        if (pushRequest.getPushUnits().isEmpty()) {
            return PushResponse.of(0, 0);
        }

        List<Message> messages = pushRequest.getPushUnits().stream()
            .map(pushUnit -> Message.builder()
                .setNotification(Notification.builder()
                    .setTitle(pushRequest.getTitle())
                    .setBody(pushUnit.getBody())
                    .build())
                .setTopic(pushUnit.getTopic())
                .build()
            ).toList();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachAsync(messages).get();
            List<SendResponse> responses = response.getResponses();
            responses.forEach(sendResponse -> log.info(sendResponse.getMessageId()));
            successCount = response.getSuccessCount();
            failureCount = response.getFailureCount();
            log.info("Messages sent successfully. Success Count: {}, Failure Count: {}", successCount, failureCount);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to send message by topic", e);
            throw new ServerErrorException(ErrorCode.FCM_SERVER_ERROR);
        }

        return PushResponse.of(successCount, failureCount);
    }

    /**
     * <p>토큰 기준 알림 전송</p>
     *
     * @param pushRequest 알림 전송 정보
     * @return 전송된 메시지 ID
     */
    public String sendMessageByToken(PushRequest pushRequest) {
//        getAccessToken();
        String messageId;

        Message message = Message.builder()
            .setNotification(Notification.builder()
                .setTitle(pushRequest.getTitle())
                .setBody(null)
                .build())
            .setToken(null)
            .build();

        try {
            messageId = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send message by token", e);
            throw new ServerErrorException(ErrorCode.FCM_SERVER_ERROR);
        }

        return messageId;
    }

    /**
     * <p>특정 토픽에 구독</p>
     *
     * @param topic  토픽 이름
     * @param tokens 구독할 토큰 리스트
     * @return 구독 수
     */
    public int subscribeToTopic(String topic, List<String> tokens) {
        int count;

        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopic(tokens, topic);
            count = response.getSuccessCount();

            log.info("Successfully subscribed to topic: {}, Response: {}", topic, response);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to subscribe topic", e);
            throw new ServerErrorException(ErrorCode.FCM_SERVER_ERROR);
        }

        return count;
    }

    /**
     * <p>특정 토픽에서 구독 해제</p>
     *
     * @param topic  토픽 이름
     * @param tokens 구독 해제할 토큰 리스트
     * @return 구독 해제 수
     */
    public int unsubscribeFromTopic(String topic, List<String> tokens) {
        int count;

        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance().unsubscribeFromTopic(tokens, topic);
            count = response.getSuccessCount();

            log.info("Successfully unsubscribed from topic: {}, Response: {}", topic, response);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to unsubscribe topic", e);
            throw new ServerErrorException(ErrorCode.FCM_SERVER_ERROR);
        }

        return count;
    }

    private void getAccessToken() {
        try {
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(
                    new ClassPathResource(keyPath).getInputStream())
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/firebase.messaging"));
            googleCredentials.refreshIfExpired();
            AccessToken accessToken = googleCredentials.getAccessToken();
            accessToken.getTokenValue();
        } catch (IOException e) {
            log.error("Failed to get Firebase access token", e);
            throw new ServerErrorException(ErrorCode.FCM_SERVER_ERROR);
        }
    }
}