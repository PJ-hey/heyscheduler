package hey.io.heyscheduler.domain.user.entity;

import hey.io.heyscheduler.common.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Entity
@Table(schema = "system")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"userId"}, callSuper = false)
public class User extends BaseEntity implements Persistable<String> {

    @Id
    private String userId; // 사용자 ID

    @Column(nullable = false)
    private String name; // 성명

    @Column(nullable = false)
    private String password; // 비밀번호

    private String email; // 이메일

    @Column(nullable = false)
    private boolean enabled; // 사용 여부

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAuth> userAuth = new ArrayList<>(); // 사용자 권한 매핑 엔티티

    @Override
    public String getId() {
        return userId;
    }

    @Override
    public boolean isNew() {
        return getCreatedAt() == null;
    }
}