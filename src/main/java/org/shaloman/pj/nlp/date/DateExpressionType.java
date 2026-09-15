package org.shaloman.pj.nlp.date;

/**
 * 자연어 날짜 표현 타입 열거형.
 * 파서가 분석한 결과를 이 타입으로 분류한다.
 */
public enum DateExpressionType {

    // ── 1. 상대일 표현 (Relative Day) ──
    TODAY("오늘", "금일"),
    TOMORROW("내일", null),
    DAY_AFTER_TOMORROW("모레", null),
    DAY_AFTER_DAY_AFTER_TOMORROW("글피", null),
    YESTERDAY("어제", null),
    DAY_BEFORE_YESTERDAY("그제", null),
    DAYS_AGO("N일 전", null),
    DAYS_LATER("N일 후", null),

    // ── 2. 상대주 표현 (Relative Week) ──
    THIS_WEEK("이번 주", null),
    NEXT_WEEK("다음 주", null),
    LAST_WEEK("저번 주", "지난 주"),
    WEEKS_AGO("N주 전", null),
    WEEKS_LATER("N주 후", null),

    // ── 3. 상대월 표현 (Relative Month) ──
    THIS_MONTH("이번 달", null),
    NEXT_MONTH("다음 달", null),
    LAST_MONTH("저번 달", "지난 달"),
    MONTHS_AGO("N달 전", null),
    MONTHS_LATER("N달 후", null),

    // ── 4. 상대연도 표현 (Relative Year) ──
    THIS_YEAR("올해", null),
    NEXT_YEAR("내년", null),
    LAST_YEAR("작년", null),
    YEARS_AGO("N년 전", null),
    YEARS_LATER("N년 후", null),

    // ── 5. 요일 기반 표현 (Weekday-based) ──
    WEEKDAY_THIS("이번 주 요일", null),
    WEEKDAY_NEXT("다음 주 요일", null),
    WEEKDAY_LAST("저번 주 요일", null),
    WEEKDAY_COMING("이번 주 남은 요일", null),
    WEEKDAY_PASSED("이번 주 지난 요일", null),

    // ── 6. 주차 + 요일 표현 (Nth Weekday of Month) ──
    NTH_WEEKDAY("특정 월 N번째 요일", null),
    NTH_WEEKDAY_RELATIVE("이번 달 N번째 요일", null),
    NTH_WEEKDAY_NEXT_MONTH("다음 달 N번째 요일", null),
    LAST_WEEKDAY_OF_MONTH("마지막 주 요일", null),

    // ── 7. 명시적 날짜 표현 (Explicit Date) ──
    EXPLICIT_YMD("연월일 명시", null),
    EXPLICIT_MD("월일 명시", null),
    EXPLICIT_YM("연월 명시", null),
    EXPLICIT_MONTH_ONLY("월만 명시", null),

    // ── 8. 특수 날짜 표현 (Special Date) ──
    START_OF_WEEK("주 시작", null),
    END_OF_WEEK("주 끝", null),
    START_OF_MONTH("월 시작", null),
    END_OF_MONTH("월 끝", null),
    START_OF_YEAR("연도 시작", null),
    END_OF_YEAR("연도 끝", null),

    // ── 9. 기간 표현 (Date Range) ──
    DATE_RANGE("기간 표현", null),
    BETWEEN("명시적 기간", null),

    // ── 10. 복합 표현 (Compound) ──
    COMPOUND("복합 표현", null),

    // ── 11. 특정 날짜 이후 가장 가까운 요일 (After Date Nearest Weekday) ──
    AFTER_DATE_NEAREST_WEEKDAY("특정 날짜 이후 가장 빠른 요일", null);

    private final String primaryLabel;
    private final String secondaryLabel;

    DateExpressionType(String primaryLabel, String secondaryLabel) {
        this.primaryLabel = primaryLabel;
        this.secondaryLabel = secondaryLabel;
    }

    public String getPrimaryLabel() {
        return primaryLabel;
    }

    public String getSecondaryLabel() {
        return secondaryLabel;
    }
}