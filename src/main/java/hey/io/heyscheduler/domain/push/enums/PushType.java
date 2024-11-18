package hey.io.heyscheduler.domain.push.enums;

import hey.io.heyscheduler.common.mapper.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PushType implements EnumMapperType {
    BATCH_TICKETING_OPEN("티켓팅 일정 오픈"),
    BATCH_TICKETING_D1("티켓팅 전날"),
    BATCH_PERFORMANCE_OPEN("새 공연 오픈");

    private final String description; // Enum 설명

    @Override
    public String getCode() {
        return name();
    }
}
