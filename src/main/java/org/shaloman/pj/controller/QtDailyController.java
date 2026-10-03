package org.shaloman.pj.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.shaloman.pj.domain.QtDaily;
import org.shaloman.pj.mapper.QtDailyMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * QT 일일(qt_daily) 조회 RESTful API 컨트롤러.
 * 크롤러가 저장한 오늘의 QT 데이터를 조회한다.
 */
@RestController
@RequestMapping("/api/qt-daily")
@Tag(name = "QT 일일", description = "오늘의 QT (qt_daily) 조회 API")
public class QtDailyController {

    private final QtDailyMapper qtDailyMapper;

    public QtDailyController(QtDailyMapper qtDailyMapper) {
        this.qtDailyMapper = qtDailyMapper;
    }

    @GetMapping("/today")
    @Operation(summary = "오늘의 QT 조회", description = "오늘 날짜의 qt_daily 데이터를 조회한다. source 파라미터 미지정 시 기본값 duranno.")
    public ResponseEntity<QtDaily> getTodayQt(
            @Parameter(description = "QT 출처 (기본값: duranno)", example = "duranno")
            @RequestParam(defaultValue = "duranno") String source) {
        QtDaily qtDaily = qtDailyMapper.findByDateAndSource(LocalDate.now(), source);
        if (qtDaily == null) {
            throw new IllegalArgumentException("오늘 날짜의 QT를 찾을 수 없습니다 (source=" + source + ")");
        }
        return ResponseEntity.ok(qtDaily);
    }

    @GetMapping
    @Operation(summary = "오늘의 QT 전체 출처 조회", description = "오늘 날짜의 모든 출처 qt_daily 데이터를 조회한다.")
    public ResponseEntity<List<QtDaily>> getTodayAllSources() {
        return ResponseEntity.ok(qtDailyMapper.findAllByDate(LocalDate.now()));
    }

    @GetMapping("/by-date")
    @Operation(summary = "특정 날짜 QT 조회", description = "지정한 날짜의 qt_daily 데이터를 조회한다. source 지정 시 해당 출처만, 미지정 시 해당 날짜 전체.")
    public ResponseEntity<List<QtDaily>> getQtByDate(
            @Parameter(description = "조회 날짜 (YYYY-MM-DD)", required = true, example = "2026-10-03")
            @RequestParam String date,
            @Parameter(description = "QT 출처 (선택)", example = "duranno")
            @RequestParam(required = false) String source) {
        LocalDate qtDate = LocalDate.parse(date);
        if (source != null && !source.isEmpty()) {
            QtDaily qtDaily = qtDailyMapper.findByDateAndSource(qtDate, source);
            if (qtDaily == null) {
                return ResponseEntity.ok(List.of());
            }
            return ResponseEntity.ok(List.of(qtDaily));
        } else {
            return ResponseEntity.ok(qtDailyMapper.findAllByDate(qtDate));
        }
    }
}