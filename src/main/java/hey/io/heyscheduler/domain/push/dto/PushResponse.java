package hey.io.heyscheduler.domain.push.dto;

public record PushResponse(
    int successCount, // PUSH 성공 수
    int failureCount // PUSH 실패 수
) {

    public static PushResponse of(int successCount, int failureCount) {
        return new PushResponse(successCount, failureCount);
    }
}
