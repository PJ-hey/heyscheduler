package hey.io.heyscheduler.domain.artist.entity;

import hey.io.heyscheduler.common.entity.BaseTimeEntity;
import hey.io.heyscheduler.domain.artist.dto.ArtistRequest;
import hey.io.heyscheduler.domain.artist.enums.ArtistStatus;
import hey.io.heyscheduler.domain.file.entity.File;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "artist")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Artist extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long artistId; // 아티스트 ID

    @Column(nullable = false)
    private String name; // 아티스트명

    private String engName; // 아티스트 영문명

    private String orgName; // 아티스트 본명

    private String artistUid; // Spotify 아티스트 ID

    private String artistUrl; // 아티스트 URL

    private Integer popularity; // 아티스트 인기도

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ArtistStatus artistStatus; // 아티스트 상태

    @Transient
    private List<File> files = new ArrayList<>();

    @Builder
    public Artist(String name, String engName, String orgName, String artistUid, String artistUrl, Integer popularity,
        ArtistStatus artistStatus, List<File> artistFiles) {
        this.name = name;
        this.engName = engName;
        this.orgName = orgName;
        this.artistUid = artistUid;
        this.artistUrl = artistUrl;
        this.popularity = popularity;
        this.artistStatus = artistStatus;
        setArtistFiles(artistFiles);
    }

    // 이미지 정보 매핑
    private void setArtistFiles(List<File> files) {
        this.files = files;
    }

    // 아티스트 수정
    public Artist updateArtist(ArtistRequest request) {
        this.name = request.getName();
        this.engName = request.getEngName();
        this.artistUid = request.getArtistUid();
        this.artistUrl = request.getArtistUrl();
        this.popularity = request.getPopularity();
        this.artistStatus = request.getArtistStatus();
        return this;
    }
}