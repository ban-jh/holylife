package org.shaloman.pj.config;

import lombok.extern.slf4j.Slf4j;
import org.shaloman.pj.content.qt.domain.QtDaily;
import org.shaloman.pj.content.qt.mapper.QtDailyMapper;
import org.shaloman.pj.content.qt.service.QtCrawlService;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;

/**
 * Spring Batch 설정 (Spring Batch 5.x/6.x API).
 * 큐티 크롤링 Job과 Step을 정의한다.
 */
@Slf4j
@Configuration
public class BatchConfig {

    /**
     * JDBC 기반 JobRepository (배치 메타데이터를 DB에 영속화).
     * Spring Batch 6 기본값은 Resourceless(인메모리) 저장소라
     * 실행 이력이 남지 않고 JobInstance 판별도 부정확해진다.
     */
    @Bean
    @Primary
    public JobRepository jdbcJobRepository(DataSource dataSource,
                                           PlatformTransactionManager transactionManager) throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.setDatabaseType("MARIADB");
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean
    public Job qtCrawlJob(JobRepository jobRepository, Step qtCrawlStep) {
        return new JobBuilder("qtCrawlJob", jobRepository)
                .start(qtCrawlStep)
                .build();
    }

    @Bean
    public Step qtCrawlStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            Tasklet qtCrawlTasklet) {
        return new StepBuilder("qtCrawlStep", jobRepository)
                .tasklet(qtCrawlTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet qtCrawlTasklet(QtCrawlService qtCrawlService,
                                  QtDailyMapper qtDailyMapper) {
        return (contribution, chunkContext) -> {
            LocalDate today = LocalDate.now();
            log.info("Batch tasklet 실행: 큐티 크롤링 날짜={}", today);

            QtDaily qt = qtCrawlService.crawlQt(today);
            if (qt != null) {
                int rows = qtDailyMapper.upsert(qt);
                log.info("Batch tasklet 완료: upsert {}행, date={}", rows, today);
            } else {
                log.warn("Batch tasklet: 큐티 크롤링 결과가 null입니다. date={}", today);
            }
            return RepeatStatus.FINISHED;
        };
    }

    // ==================== 생명의삶 (SUM) 큐티 크롤링 ====================

    @Bean
    public Job qtSumCrawlJob(JobRepository jobRepository, Step qtSumCrawlStep) {
        return new JobBuilder("qtSumCrawlJob", jobRepository)
                .start(qtSumCrawlStep)
                .build();
    }

    @Bean
    public Step qtSumCrawlStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               Tasklet qtSumCrawlTasklet) {
        return new StepBuilder("qtSumCrawlStep", jobRepository)
                .tasklet(qtSumCrawlTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet qtSumCrawlTasklet(QtCrawlService qtCrawlService,
                                     QtDailyMapper qtDailyMapper) {
        return (contribution, chunkContext) -> {
            LocalDate today = LocalDate.now();
            log.info("Batch tasklet 실행: SUM 큐티 크롤링 날짜={}", today);

            QtDaily qt = qtCrawlService.crawlSum(today);
            if (qt != null) {
                int rows = qtDailyMapper.upsert(qt);
                log.info("Batch tasklet 완료: SUM upsert {}행, date={}", rows, today);
            } else {
                log.warn("Batch tasklet: SUM 큐티 크롤링 결과가 null입니다. date={}", today);
            }
            return RepeatStatus.FINISHED;
        };
    }
}