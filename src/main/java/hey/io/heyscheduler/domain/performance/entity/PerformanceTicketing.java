package hey.io.heyscheduler.domain.performance.entity;

import hey.io.heyscheduler.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "performance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceTicketing extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketingId; // 예매 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance; // 공연 엔티티

    @Column(nullable = false)
    private String ticketingBooth; // 예매처

    private String ticketingPremium; // 예매 프리미엄

    private LocalDateTime openDatetime; // 티켓 오픈 시간

    private String ticketingUrl; // 예매 링크

    @Builder
    public PerformanceTicketing(Performance performance, String ticketingBooth, String ticketingPremium,
        LocalDateTime openDatetime, String ticketingUrl) {
        this.performance = performance;
        this.ticketingBooth = ticketingBooth;
        this.ticketingPremium = ticketingPremium;
        this.openDatetime = openDatetime;
        this.ticketingUrl = ticketingUrl;
    }

    void updatePerformance(Performance performance) {
        this.performance = performance;
    }
}