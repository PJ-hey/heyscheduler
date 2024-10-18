package hey.io.heyscheduler.domain.performance.entity;

import hey.io.heyscheduler.domain.performance.enums.PerformanceGenre;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceGenres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 일련번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance; // 공연 엔티티

    @Column(nullable = false)
    private PerformanceGenre performanceGenre; // 공연 장르
}