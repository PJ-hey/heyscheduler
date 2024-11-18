package hey.io.heyscheduler.domain.performance.enums;

import hey.io.heyscheduler.common.mapper.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TicketStatus implements EnumMapperType {
    READY("판매 예정"),
    ONGOING("판매 중"),
    CLOSED("판매 종료");

    private final String description; // Enum 설명

    @Override
    public String getCode() {
        return name();
    }
}
