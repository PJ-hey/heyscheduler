package hey.io.heyscheduler.domain.performance.domain;

import hey.io.heyscheduler.common.entity.BaseEntityWithUpdate;
import hey.io.heyscheduler.domain.artist.domain.ArtistEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceArtist extends BaseEntityWithUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private ArtistEntity artist;

    @Builder
    private PerformanceArtist(Performance performance, ArtistEntity artist) {
        this.performance = performance;
        this.artist = artist;
    }

    public static PerformanceArtist of(Performance performance, ArtistEntity artist) {
        return PerformanceArtist.builder()
                .performance(performance)
                .artist(artist)
                .build();
    }

}