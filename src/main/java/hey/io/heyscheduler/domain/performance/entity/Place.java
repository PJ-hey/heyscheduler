package hey.io.heyscheduler.domain.performance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "performance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId; // 장소 ID

    @Column(nullable = false)
    private String name; // 장소명

    private String placeUid; // KOPIS 장소 ID

    private String address; // 주소

    private double latitude; // 위도

    private double longitude; // 경도

    @Builder
    public Place(String name, String placeUid, String address, double latitude, double longitude) {
        this.name = name;
        this.placeUid = placeUid;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}