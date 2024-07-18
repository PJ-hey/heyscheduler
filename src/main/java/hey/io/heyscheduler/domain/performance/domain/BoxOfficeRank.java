package hey.io.heyscheduler.domain.performance.domain;

import hey.io.heyscheduler.common.entity.BaseEntityWithUpdate;
import hey.io.heyscheduler.domain.performance.domain.enums.TimePeriod;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoxOfficeRank extends BaseEntityWithUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private TimePeriod timePeriod;

    @Column(length = 1000)
    private String performanceIds;

    @Builder
    public BoxOfficeRank(TimePeriod timePeriod, String performanceIds) {
        this.timePeriod = timePeriod;
        this.performanceIds = performanceIds;
    }
}