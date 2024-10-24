package hey.io.heyscheduler.domain.artist.enums;

import hey.io.heyscheduler.common.mapper.EnumMapperType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArtistType implements EnumMapperType {
    GROUP("그룹"),
    SOLO("솔로");

    private final String description; // Enum 설명

    @Override
    public String getCode() {
        return name();
    }
}
