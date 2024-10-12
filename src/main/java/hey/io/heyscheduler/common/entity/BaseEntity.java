

package hey.io.heyscheduler.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public abstract class BaseEntity {
    @CreatedDate
    @Column(
            nullable = false,
            updatable = false
    )
    private @NotNull LocalDateTime createdAt;

    public BaseEntity() {
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
}