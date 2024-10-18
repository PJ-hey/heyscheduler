package hey.io.heyscheduler.domain.performance.enums;

import hey.io.heyscheduler.common.mapper.EnumMapperType;
import java.util.Arrays;
import java.util.NoSuchElementException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PerformanceStatus implements EnumMapperType {
    INIT("공연 등록", ""),
    READY("공연 예정", "공연예정"),
    ONGOING("공연 중", "공연중"),
    CLOSED("공연 종료", "공연완료"),
    CANCEL("공연 취소", "");

    private final String description; // Enum 설명

    private final String kopisName; // KOPIS 공연명

    @Override
    public String getCode() {
        return name();
    }

    public static PerformanceStatus getByKopisName(String kopisName) {
        return Arrays.stream(
                PerformanceStatus.values())
                .filter(performanceStatus -> performanceStatus.getKopisName().equals(kopisName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No such status."));
    }
}
