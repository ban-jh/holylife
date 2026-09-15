package org.shaloman.pj.nlp.date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NaturalDateParser 단위 테스트.
 * 기준일: 2026-09-15 (화요일)
 */
@DisplayName("자연어 날짜 파서 테스트")
class NaturalDateParserTest {

    private NaturalDateParser parser;
    private static final LocalDate BASE = LocalDate.of(2026, 9, 15); // 화요일

    @BeforeEach
    void setUp() {
        parser = new NaturalDateParser(BASE);
    }

    // ── 1. 상대일 표현 ──

    @Test
    @DisplayName("오늘")
    void testToday() {
        DateParseResult r = parser.parse("오늘");
        assertEquals(DateExpressionType.TODAY, r.getType());
        assertEquals(BASE, r.getStartDate());
        assertTrue(r.isSingleDate());
    }

    @Test
    @DisplayName("금일")
    void testToday2() {
        DateParseResult r = parser.parse("금일");
        assertEquals(DateExpressionType.TODAY, r.getType());
        assertEquals(BASE, r.getStartDate());
    }

    @Test
    @DisplayName("내일")
    void testTomorrow() {
        DateParseResult r = parser.parse("내일");
        assertEquals(DateExpressionType.TOMORROW, r.getType());
        assertEquals(LocalDate.of(2026, 9, 16), r.getStartDate());
    }

    @Test
    @DisplayName("모레")
    void testDayAfterTomorrow() {
        DateParseResult r = parser.parse("모레");
        assertEquals(LocalDate.of(2026, 9, 17), r.getStartDate());
    }

    @Test
    @DisplayName("글피")
    void testDayAfterDayAfterTomorrow() {
        DateParseResult r = parser.parse("글피");
        assertEquals(LocalDate.of(2026, 9, 18), r.getStartDate());
    }

    @Test
    @DisplayName("어제")
    void testYesterday() {
        DateParseResult r = parser.parse("어제");
        assertEquals(LocalDate.of(2026, 9, 14), r.getStartDate());
    }

    @Test
    @DisplayName("그제")
    void testDayBeforeYesterday() {
        DateParseResult r = parser.parse("그제");
        assertEquals(LocalDate.of(2026, 9, 13), r.getStartDate());
    }

    @Test
    @DisplayName("3일 전")
    void testDaysAgo() {
        DateParseResult r = parser.parse("3일 전");
        assertEquals(DateExpressionType.DAYS_AGO, r.getType());
        assertEquals(LocalDate.of(2026, 9, 12), r.getStartDate());
    }

    @Test
    @DisplayName("5일 후")
    void testDaysLater() {
        DateParseResult r = parser.parse("5일 후");
        assertEquals(DateExpressionType.DAYS_LATER, r.getType());
        assertEquals(LocalDate.of(2026, 9, 20), r.getStartDate());
    }

    // ── 2. 상대주 표현 ──

    @Test
    @DisplayName("이번 주")
    void testThisWeek() {
        DateParseResult r = parser.parse("이번 주");
        assertEquals(DateExpressionType.THIS_WEEK, r.getType());
        assertEquals(LocalDate.of(2026, 9, 14), r.getStartDate()); // 월
        assertEquals(LocalDate.of(2026, 9, 20), r.getEndDate());   // 일
        assertTrue(r.isRange());
    }

    @Test
    @DisplayName("다음 주")
    void testNextWeek() {
        DateParseResult r = parser.parse("다음 주");
        assertEquals(LocalDate.of(2026, 9, 21), r.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 27), r.getEndDate());
    }

    @Test
    @DisplayName("저번 주")
    void testLastWeek() {
        DateParseResult r = parser.parse("저번 주");
        assertEquals(LocalDate.of(2026, 9, 7), r.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 13), r.getEndDate());
    }

    @Test
    @DisplayName("2주 전")
    void testWeeksAgo() {
        DateParseResult r = parser.parse("2주 전");
        assertEquals(LocalDate.of(2026, 9, 1) // minus 2 weeks from 9/15 → 9/1 is Monday of that week? No: base minus 2 weeks = 9/1, Monday of that week
                .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)),
                r.getStartDate());
    }

    @Test
    @DisplayName("3주 후")
    void testWeeksLater() {
        DateParseResult r = parser.parse("3주 후");
        LocalDate expectedStart = BASE.plusWeeks(3).with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        assertEquals(expectedStart, r.getStartDate());
    }

    // ── 3. 상대월 표현 ──

    @Test
    @DisplayName("이번 달")
    void testThisMonth() {
        DateParseResult r = parser.parse("이번 달");
        assertEquals(DateExpressionType.THIS_MONTH, r.getType());
        assertEquals(LocalDate.of(2026, 9, 1), r.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 30), r.getEndDate());
    }

    @Test
    @DisplayName("다음 달")
    void testNextMonth() {
        DateParseResult r = parser.parse("다음 달");
        assertEquals(LocalDate.of(2026, 10, 1), r.getStartDate());
        assertEquals(LocalDate.of(2026, 10, 31), r.getEndDate());
    }

    @Test
    @DisplayName("저번 달")
    void testLastMonth() {
        DateParseResult r = parser.parse("저번 달");
        assertEquals(LocalDate.of(2026, 8, 1), r.getStartDate());
        assertEquals(LocalDate.of(2026, 8, 31), r.getEndDate());
    }

    @Test
    @DisplayName("2달 전")
    void testMonthsAgo() {
        DateParseResult r = parser.parse("2달 전");
        assertEquals(LocalDate.of(2026, 7, 1), r.getStartDate());
        assertEquals(LocalDate.of(2026, 7, 31), r.getEndDate());
    }

    @Test
    @DisplayName("3달 후")
    void testMonthsLater() {
        DateParseResult r = parser.parse("3달 후");
        assertEquals(LocalDate.of(2026, 12, 1), r.getStartDate());
        assertEquals(LocalDate.of(2026, 12, 31), r.getEndDate());
    }

    // ── 4. 상대연도 표현 ──

    @Test
    @DisplayName("올해")
    void testThisYear() {
        DateParseResult r = parser.parse("올해");
        assertEquals(LocalDate.of(2026, 1, 1), r.getStartDate());
        assertEquals(LocalDate.of(2026, 12, 31), r.getEndDate());
    }

    @Test
    @DisplayName("내년")
    void testNextYear() {
        DateParseResult r = parser.parse("내년");
        assertEquals(LocalDate.of(2027, 1, 1), r.getStartDate());
        assertEquals(LocalDate.of(2027, 12, 31), r.getEndDate());
    }

    @Test
    @DisplayName("작년")
    void testLastYear() {
        DateParseResult r = parser.parse("작년");
        assertEquals(LocalDate.of(2025, 1, 1), r.getStartDate());
        assertEquals(LocalDate.of(2025, 12, 31), r.getEndDate());
    }

    @Test
    @DisplayName("1년 전")
    void testYearsAgo() {
        DateParseResult r = parser.parse("1년 전");
        assertEquals(LocalDate.of(2025, 1, 1), r.getStartDate());
    }

    @Test
    @DisplayName("2년 후")
    void testYearsLater() {
        DateParseResult r = parser.parse("2년 후");
        assertEquals(LocalDate.of(2028, 1, 1), r.getStartDate());
    }

    // ── 5. 요일 기반 표현 ──

    @Test
    @DisplayName("이번 주 화요일")
    void testWeekdayThis() {
        DateParseResult r = parser.parse("이번 주 화요일");
        assertEquals(DateExpressionType.WEEKDAY_THIS, r.getType());
        assertEquals(LocalDate.of(2026, 9, 15), r.getStartDate());
    }

    @Test
    @DisplayName("이번 주 금요일")
    void testWeekdayThisFriday() {
        DateParseResult r = parser.parse("이번 주 금요일");
        assertEquals(LocalDate.of(2026, 9, 18), r.getStartDate());
    }

    @Test
    @DisplayName("다음 주 수요일")
    void testWeekdayNext() {
        DateParseResult r = parser.parse("다음 주 수요일");
        assertEquals(DateExpressionType.WEEKDAY_NEXT, r.getType());
        assertEquals(LocalDate.of(2026, 9, 23), r.getStartDate());
    }

    @Test
    @DisplayName("저번 주 금요일")
    void testWeekdayLast() {
        DateParseResult r = parser.parse("저번 주 금요일");
        assertEquals(LocalDate.of(2026, 9, 11), r.getStartDate());
    }

    // ── 6. 주차 + 요일 표현 ──

    @Test
    @DisplayName("9월 셋째 주 화요일")
    void testNthWeekday() {
        DateParseResult r = parser.parse("9월 셋째 주 화요일");
        assertEquals(DateExpressionType.NTH_WEEKDAY, r.getType());
        assertEquals(LocalDate.of(2026, 9, 15), r.getStartDate());
    }

    @Test
    @DisplayName("9월 둘째 주 일요일")
    void testNthWeekday2() {
        DateParseResult r = parser.parse("9월 둘째 주 일요일");
        assertEquals(LocalDate.of(2026, 9, 13), r.getStartDate());
    }

    @Test
    @DisplayName("이번 달 첫째 주 월요일")
    void testNthWeekdayRelative() {
        DateParseResult r = parser.parse("이번 달 첫째 주 월요일");
        assertEquals(DateExpressionType.NTH_WEEKDAY_RELATIVE, r.getType());
        assertEquals(LocalDate.of(2026, 9, 7), r.getStartDate());
    }

    @Test
    @DisplayName("다음 달 둘째 주 월요일")
    void testNthWeekdayNextMonth() {
        DateParseResult r = parser.parse("다음 달 둘째 주 월요일");
        assertEquals(DateExpressionType.NTH_WEEKDAY_NEXT_MONTH, r.getType());
        assertEquals(LocalDate.of(2026, 10, 12), r.getStartDate());
    }

    @Test
    @DisplayName("9월 마지막 주 금요일")
    void testLastWeekdayOfMonth() {
        DateParseResult r = parser.parse("9월 마지막 주 금요일");
        assertEquals(DateExpressionType.LAST_WEEKDAY_OF_MONTH, r.getType());
        assertEquals(LocalDate.of(2026, 9, 25), r.getStartDate());
    }

    // ── 7. 명시적 날짜 표현 ──

    @Test
    @DisplayName("2026년 9월 15일")
    void testExplicitYMD() {
        DateParseResult r = parser.parse("2026년 9월 15일");
        assertEquals(DateExpressionType.EXPLICIT_YMD, r.getType());
        assertEquals(LocalDate.of(2026, 9, 15), r.getStartDate());
    }

    @Test
    @DisplayName("9월 15일")
    void testExplicitMD() {
        DateParseResult r = parser.parse("9월 15일");
        assertEquals(DateExpressionType.EXPLICIT_MD, r.getType());
        assertEquals(LocalDate.of(2026, 9, 15), r.getStartDate());
    }

    @Test
    @DisplayName("2026년 9월")
    void testExplicitYM() {
        DateParseResult r = parser.parse("2026년 9월");
        assertEquals(DateExpressionType.EXPLICIT_YM, r.getType());
        assertEquals(LocalDate.of(2026, 9, 1), r.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 30), r.getEndDate());
    }

    @Test
    @DisplayName("9월")
    void testExplicitMonth() {
        DateParseResult r = parser.parse("9월");
        assertEquals(DateExpressionType.EXPLICIT_MONTH_ONLY, r.getType());
        assertEquals(LocalDate.of(2026, 9, 1), r.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 30), r.getEndDate());
    }

    // ── 8. 특수 날짜 표현 ──

    @Test
    @DisplayName("이번 주 시작")
    void testStartOfWeek() {
        DateParseResult r = parser.parse("이번 주 시작");
        assertEquals(DateExpressionType.START_OF_WEEK, r.getType());
        assertEquals(LocalDate.of(2026, 9, 14), r.getStartDate());
    }

    @Test
    @DisplayName("이번 주 끝")
    void testEndOfWeek() {
        DateParseResult r = parser.parse("이번 주 끝");
        assertEquals(DateExpressionType.END_OF_WEEK, r.getType());
        assertEquals(LocalDate.of(2026, 9, 20), r.getStartDate());
    }

    @Test
    @DisplayName("이번 달 시작")
    void testStartOfMonth() {
        DateParseResult r = parser.parse("이번 달 시작");
        assertEquals(LocalDate.of(2026, 9, 1), r.getStartDate());
    }

    @Test
    @DisplayName("이번 달 끝")
    void testEndOfMonth() {
        DateParseResult r = parser.parse("이번 달 끝");
        assertEquals(LocalDate.of(2026, 9, 30), r.getStartDate());
    }

    @Test
    @DisplayName("올해 시작")
    void testStartOfYear() {
        DateParseResult r = parser.parse("올해 시작");
        assertEquals(LocalDate.of(2026, 1, 1), r.getStartDate());
    }

    @Test
    @DisplayName("올해 끝")
    void testEndOfYear() {
        DateParseResult r = parser.parse("올해 끝");
        assertEquals(LocalDate.of(2026, 12, 31), r.getStartDate());
    }

    // ── 9. 기간 표현 ──

    @Test
    @DisplayName("9월 10일부터 9월 20일까지")
    void testBetween() {
        DateParseResult r = parser.parse("9월 10일부터 9월 20일까지");
        assertEquals(DateExpressionType.BETWEEN, r.getType());
        assertEquals(LocalDate.of(2026, 9, 10), r.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 20), r.getEndDate());
        assertTrue(r.isRange());
    }

    @Test
    @DisplayName("어제부터 내일까지")
    void testBetweenRelative() {
        DateParseResult r = parser.parse("어제부터 내일까지");
        assertEquals(LocalDate.of(2026, 9, 14), r.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 16), r.getEndDate());
    }

    // ── 10. 복합 표현 ──

    @Test
    @DisplayName("다음 달 15일")
    void testCompoundMonthDay() {
        DateParseResult r = parser.parse("다음 달 15일");
        assertEquals(DateExpressionType.COMPOUND, r.getType());
        assertEquals(LocalDate.of(2026, 10, 15), r.getStartDate());
    }

    @Test
    @DisplayName("내년 3월 15일")
    void testCompoundMonthDay2() {
        DateParseResult r = parser.parse("내년 3월 15일");
        assertEquals(LocalDate.of(2027, 3, 15), r.getStartDate());
    }

    @Test
    @DisplayName("내년 3월 둘째 주 월요일")
    void testCompoundNthWeekday() {
        DateParseResult r = parser.parse("내년 3월 둘째 주 월요일");
        assertEquals(DateExpressionType.COMPOUND, r.getType());
        assertEquals(LocalDate.of(2027, 3, 8), r.getStartDate());
    }

    // ── 예외 케이스 ──

    @Test
    @DisplayName("매칭 불가 — null 반환")
    void testNoMatch() {
        DateParseResult r = parser.parse("안녕하세요");
        assertNull(r.getType());
        assertNull(r.getStartDate());
    }

    @Test
    @DisplayName("빈 문자열")
    void testEmpty() {
        DateParseResult r = parser.parse("");
        assertNull(r.getType());
    }

    @Test
    @DisplayName("null 입력")
    void testNull() {
        DateParseResult r = parser.parse(null);
        assertNull(r.getType());
    }

    @Test
    @DisplayName("이번주 (공백 없음) 정규화")
    void testNormalize() {
        DateParseResult r = parser.parse("이번주");
        assertEquals(DateExpressionType.THIS_WEEK, r.getType());
        assertEquals(LocalDate.of(2026, 9, 14), r.getStartDate());
    }

    @Test
    @DisplayName("이번달 (공백 없음) 정규화")
    void testNormalize2() {
        DateParseResult r = parser.parse("이번달");
        assertEquals(DateExpressionType.THIS_MONTH, r.getType());
        assertEquals(LocalDate.of(2026, 9, 1), r.getStartDate());
    }
}