package hey.io.heyscheduler.domain.file.enums;

import hey.io.heyscheduler.common.mapper.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EntityType implements EnumMapperType {
    PERFORMANCE("공연"),
    ARTIST("아티스트"),
    ALBUM("앨범");

    private final String description; // Enum 설명

    @Override
    public String getCode() {
        return name();
    }
}
