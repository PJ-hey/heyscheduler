package hey.io.heyscheduler.domain.push.dto;

import hey.io.heyscheduler.domain.push.enums.PushType;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PushRequest {

    private String title; // PUSH 제목
    private List<PushUnit> pushUnits; // 개별 PUSH 정보

    @Getter
    @Builder
    public static class PushUnit {

        private String topic; // PUSH 토픽 (performance-1, artist-2, ...)
        private String body; // PUSH 본문

        private static String getTopic(PushType pushType, Long id) {
            return switch (pushType) {
                case PushType.BATCH_TICKETING_OPEN, PushType.BATCH_TICKETING_D1 -> "performance-" + id;
                case PushType.BATCH_PERFORMANCE_OPEN -> "artist-" + id;
            };
        }

        private static String getBodyMessage(PushType pushType, String name) {
            return switch (pushType) {
                case PushType.BATCH_TICKETING_OPEN -> "드디어 " + name + "의 티켓 예매 일정이 오픈됐어요!";
                case PushType.BATCH_TICKETING_D1 -> "두근두근! 내일 " + name + "의 티켓 예매 잊지 않으셨죠?";
                case PushType.BATCH_PERFORMANCE_OPEN -> name + "의 공연이 오픈되었어요! 바로 확인하러 가볼까요?";
            };
        }

        private static PushUnit of(PushType pushType, Long id, String name) {
            return PushUnit.builder()
                .body(getBodyMessage(pushType, name))
                .topic(getTopic(pushType, id))
                .build();
        }
    }

    private static String getTitle(PushType pushType) {
        return switch (pushType) {
            case PushType.BATCH_TICKETING_OPEN -> "티켓 오픈 일정";
            case PushType.BATCH_TICKETING_D1 -> "티켓 오픈 D-1";
            case PushType.BATCH_PERFORMANCE_OPEN -> "공연 오픈";
        };
    }

    /**
     * <p>PUSH Request 구성</p>
     *
     * @param pushType PUSH 알림 유형
     * @param entities 공연 또는 아티스트 개체 (key + name 쌍으로 이루어진 정보)
     * @return PushRequest
     */
    public static PushRequest of(PushType pushType, Map<Long, String> entities) {
        List<PushUnit> pushUnits = entities.entrySet().stream()
            .map(entry -> PushUnit.of(pushType, entry.getKey(), entry.getValue()))
            .toList();

        return PushRequest.builder()
            .title(getTitle(pushType))
            .pushUnits(pushUnits)
            .build();
    }
}