
package hey.io.heyscheduler.domain.performance.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hey.io.heyscheduler.domain.performance.domain.Performance;
import hey.io.heyscheduler.domain.performance.domain.QPerformance;
import hey.io.heyscheduler.domain.performance.domain.enums.PerformanceStatus;
import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
public class PerformanceQueryRepositoryImpl implements PerformanceQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<String> findAllIds() {
        return ((JPAQuery)this.queryFactory.select(QPerformance.performance.id).from(QPerformance.performance)).fetch();
    }

    public List<Performance> getPerformancesByStartDate() {
        return ((JPAQuery)this.queryFactory.selectFrom(QPerformance.performance).where(QPerformance.performance.startDate.eq(LocalDate.now().plusDays(1L)))).fetch();
    }

    private BooleanExpression inStatus(List<PerformanceStatus> statuses) {
        return ObjectUtils.isEmpty(statuses) ? null : QPerformance.performance.status.in(statuses);
    }


}
