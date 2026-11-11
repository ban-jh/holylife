package org.shaloman.pj.content.qt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * QT 크롤링 배치 수동 실행 API (운영/관리용).
 * 스케줄러가 실패했거나 누락된 날짜를 보충할 때 사용한다.
 */
@Slf4j
@RestController
@RequestMapping("/api/qt")
@RequiredArgsConstructor
@Tag(name = "QT 배치", description = "QT 크롤링 배치 수동 실행 API")
public class QtBatchController {

    private final JobLauncher jobLauncher;
    private final Job qtCrawlJob;
    private final Job qtSumCrawlJob;

    /**
     * QT 크롤링 배치를 수동 실행한다.
     *
     * @param source duranno | sum | all (기본 all)
     * @param date   크롤링 날짜 (yyyy-MM-dd, 생략 시 오늘)
     */
    @PostMapping("/crawl")
    @Operation(summary = "QT 크롤링 수동 실행",
            description = "duranno/sum/all 소스를 지정해 크롤링 배치를 즉시 실행한다. date 생략 시 오늘 날짜.")
    public ResponseEntity<Map<String, Object>> crawl(
            @RequestParam(defaultValue = "all") String source,
            @RequestParam(required = false) String date) {
        return ResponseEntity.ok(run(source, date));
    }

    /** GET 방식도 지원 (브라우저/curl 테스트 편의) */
    @GetMapping("/crawl")
    @Operation(summary = "QT 크롤링 수동 실행 (GET)")
    public ResponseEntity<Map<String, Object>> crawlGet(
            @RequestParam(defaultValue = "all") String source,
            @RequestParam(required = false) String date) {
        return ResponseEntity.ok(run(source, date));
    }

    private Map<String, Object> run(String source, String date) {
        String targetDate = (date == null || date.isBlank()) ? LocalDate.now().toString() : date;
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("date", targetDate);
        result.put("source", source);

        try {
            if ("duranno".equalsIgnoreCase(source) || "all".equalsIgnoreCase(source)) {
                JobParameters params = new JobParametersBuilder()
                        .addString("job.type", "duranno")
                        .addString("job.date", targetDate)
                        .addLong("run.id", System.currentTimeMillis())
                        .toJobParameters();
                var exec = jobLauncher.run(qtCrawlJob, params);
                result.put("duranno", exec.getStatus().name());
                result.put("durannoExecutionId", exec.getId());
            }
            if ("sum".equalsIgnoreCase(source) || "all".equalsIgnoreCase(source)) {
                JobParameters params = new JobParametersBuilder()
                        .addString("job.type", "sum")
                        .addString("job.date", targetDate)
                        .addLong("run.id", System.currentTimeMillis() + 1)
                        .toJobParameters();
                var exec = jobLauncher.run(qtSumCrawlJob, params);
                result.put("sum", exec.getStatus().name());
                result.put("sumExecutionId", exec.getId());
            }
            if (!result.containsKey("duranno") && !result.containsKey("sum")) {
                result.put("error", "source는 duranno, sum, all 중 하나여야 합니다.");
            }
            log.info("QT 수동 크롤링 실행: date={}, source={}, result={}", targetDate, source, result);
        } catch (Exception e) {
            log.error("QT 수동 크롤링 실패: date={}, source={}", targetDate, source, e);
            result.put("error", e.getMessage());
        }
        return result;
    }
}
