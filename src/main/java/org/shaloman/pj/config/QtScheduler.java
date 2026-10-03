package org.shaloman.pj.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 큐티 크롤링 배치 잡 스케줄러.
 * 매일 새벽 4시에 qtCrawlJob을 실행한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QtScheduler {

    private final JobLauncher jobLauncher;
    private final Job qtCrawlJob;
    private final Job qtSumCrawlJob;

    /**
     * 매일 04:00에 Duranno 큐티 크롤링 배치 실행.
     * cron: 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void runQtCrawlJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis())
                    .toJobParameters();
            log.info("QT 크롤링 배치 잡 시작 (Duranno)");
            jobLauncher.run(qtCrawlJob, params);
            log.info("QT 크롤링 배치 잡 완료 (Duranno)");
        } catch (Exception e) {
            log.error("QT 크롤링 배치 잡 실패 (Duranno): {}", e.getMessage(), e);
        }
    }

    /**
     * 매일 04:05에 생명의삶(SUM) 큐티 크롤링 배치 실행.
     * Duranno 크롤링 5분 후 실행.
     */
    @Scheduled(cron = "0 5 4 * * ?")
    public void runQtSumCrawlJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis())
                    .toJobParameters();
            log.info("QT 크롤링 배치 잡 시작 (SUM)");
            jobLauncher.run(qtSumCrawlJob, params);
            log.info("QT 크롤링 배치 잡 완료 (SUM)");
        } catch (Exception e) {
            log.error("QT 크롤링 배치 잡 실패 (SUM): {}", e.getMessage(), e);
        }
    }
}