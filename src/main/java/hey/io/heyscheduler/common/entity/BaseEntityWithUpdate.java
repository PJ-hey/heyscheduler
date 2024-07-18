package hey.io.heyscheduler.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public class BaseEntityWithUpdate extends hey.io.heyscheduler.common.entity.BaseEntity {
    @LastModifiedDate
    @Column(
            nullable = false
    )
    private @NotNull LocalDateTime updatedAt;

    public BaseEntityWithUpdate() {
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }
}
