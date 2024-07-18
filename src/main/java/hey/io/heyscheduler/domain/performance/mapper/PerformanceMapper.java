//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package hey.io.heyscheduler.domain.performance.mapper;

import hey.io.heyscheduler.domain.performance.domain.Performance;
import hey.io.heyscheduler.domain.performance.dto.PerformanceDetailResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PerformanceMapper {
    PerformanceMapper INSTANCE = (PerformanceMapper)Mappers.getMapper(PerformanceMapper.class);

    @Mappings({@Mapping(
            source = "performance.storyUrls",
            target = "storyUrls",
            ignore = true
    ), @Mapping(
            target = "latitude",
            ignore = true
    ), @Mapping(
            target = "longitude",
            ignore = true
    ), @Mapping(
            target = "address",
            ignore = true
    ), @Mapping(
            source = "performance.place",
            target = "placeId",
            ignore = true
    )})
    PerformanceDetailResponse toPerformanceDto(Performance performance);
}
