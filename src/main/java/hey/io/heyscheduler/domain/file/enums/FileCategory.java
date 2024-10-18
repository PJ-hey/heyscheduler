package hey.io.heyscheduler.domain.file.enums;

import hey.io.heyscheduler.common.mapper.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileCategory implements EnumMapperType {
    THUMBNAIL("썸네일"),
    DETAIL("상세");

    private final String description; // Enum 설명

    @Override
    public String getCode() {
        return name();
    }
}
