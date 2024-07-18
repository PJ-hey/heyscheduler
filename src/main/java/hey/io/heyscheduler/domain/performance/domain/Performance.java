//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package hey.io.heyscheduler.domain.performance.domain;

import hey.io.heyscheduler.common.entity.BaseEntityWithUpdate;
import hey.io.heyscheduler.domain.performance.domain.enums.PerformanceStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "performance", indexes = {
        @Index(name = "idx_performance_id", columnList = "id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Performance extends BaseEntityWithUpdate {

    @Id
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String theater;
    @Column(name = "`cast`")
    private String cast;
    private String runtime;
    private String age;
    @Column(length = 500)
    private String price;
    private String poster;
    private Boolean visit;
    @Enumerated(EnumType.STRING)
    private PerformanceStatus status;
    @Column(name = "story_urls", columnDefinition = "TEXT")
    private String storyUrls;
    private String schedule;

    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerformanceArtist> performanceArtists = new ArrayList<>();

    @Builder(toBuilder = true)
    private Performance(String id, Place place, String title, LocalDate startDate, LocalDate endDate,
                        String theater, String cast, String runtime, String age, String price,
                        String poster, Boolean visit, PerformanceStatus status, String storyUrls, String schedule) {
        this.id = id;
        this.place = place;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.theater = theater;
        this.cast = cast;
        this.runtime = runtime;
        this.age = age;
        this.price = price;
        this.poster = poster;
        this.visit = visit;
        this.status = status;
        this.storyUrls = storyUrls;
        this.schedule = schedule;
    }

    public void updateStatus(PerformanceStatus status) {
        this.status = status;
    }

    public void updatePlace(Place place) {
        this.place = place;
    }

}