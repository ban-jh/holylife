package org.shaloman.pj.nlp.date;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 자연어 날짜 표현 파서.
 * 한국어 자연어 입력을 분석하여 DateParseResult를 반환한다.
 *
 * 지원 표현 카테고리:
 *   1. 상대일 (오늘, 내일, 모레, 글피, 어제, 그제, N일 전/후)
 *   2. 상대주 (이번 주, 다음 주, 저번 주, N주 전/후)
 *   3. 상대월 (이번 달, 다음 달, 저번 달, N달 전/후)
 *   4. 상대연도 (올해, 내년, 작년, N년 전/후)
 *   5. 요일 기반 (이번/다음/저번 주 + 요일)
 *   6. 주차 + 요일 (N번째 주 요일, 마지막 주 요일)
 *   7. 명시적 날짜 (2026년 9월 15일, 9월 15일, 2026년 9월, 9월)
 *   8. 특수 날짜 (주/월/연도 시작/끝)
 *   9. 기간 표현 (이번 주, 9월 둘째 주, ~부터 ~까지)
 *  10. 복합 표현 (다음 달 15일, 내년 3월 둘째 주 월요일)
 */
public class NaturalDateParser {

    private final LocalDate baseDate;
    private final List<String> matchedKeywords;

    // ── 한국어 숫자 변환 ──
    private static final Map<String, Integer> KOREAN_NUMBERS = new HashMap<>();
    static {
        KOREAN_NUMBERS.put("첫", 1); KOREAN_NUMBERS.put("첫째", 1); KOREAN_NUMBERS.put("1째", 1); KOREAN_NUMBERS.put("1일", 1);
        KOREAN_NUMBERS.put("둘", 2); KOREAN_NUMBERS.put("둘째", 2); KOREAN_NUMBERS.put("2째", 2);
        KOREAN_NUMBERS.put("셋", 3); KOREAN_NUMBERS.put("셋째", 3); KOREAN_NUMBERS.put("3째", 3);
        KOREAN_NUMBERS.put("넷", 4); KOREAN_NUMBERS.put("넷째", 4); KOREAN_NUMBERS.put("4째", 4);
        KOREAN_NUMBERS.put("다섯", 5); KOREAN_NUMBERS.put("다섯째", 5); KOREAN_NUMBERS.put("5째", 5);
        KOREAN_NUMBERS.put("여섯", 6); KOREAN_NUMBERS.put("여섯째", 6); KOREAN_NUMBERS.put("6째", 6);
        KOREAN_NUMBERS.put("일곱", 7); KOREAN_NUMBERS.put("일곱째", 7); KOREAN_NUMBERS.put("7째", 7);
        KOREAN_NUMBERS.put("여덟", 8); KOREAN_NUMBERS.put("여덟째", 8); KOREAN_NUMBERS.put("8째", 8);
        KOREAN_NUMBERS.put("아홉", 9); KOREAN_NUMBERS.put("아홉째", 9); KOREAN_NUMBERS.put("9째", 9);
        KOREAN_NUMBERS.put("열", 10); KOREAN_NUMBERS.put("열째", 10); KOREAN_NUMBERS.put("10째", 10);
    }

    // ── 요일 매핑 ──
    private static final Map<String, DayOfWeek> WEEKDAY_MAP = new HashMap<>();
    static {
        WEEKDAY_MAP.put("월요일", DayOfWeek.MONDAY);
        WEEKDAY_MAP.put("월", DayOfWeek.MONDAY);
        WEEKDAY_MAP.put("화요일", DayOfWeek.TUESDAY);
        WEEKDAY_MAP.put("화", DayOfWeek.TUESDAY);
        WEEKDAY_MAP.put("수요일", DayOfWeek.WEDNESDAY);
        WEEKDAY_MAP.put("수", DayOfWeek.WEDNESDAY);
        WEEKDAY_MAP.put("목요일", DayOfWeek.THURSDAY);
        WEEKDAY_MAP.put("목", DayOfWeek.THURSDAY);
        WEEKDAY_MAP.put("금요일", DayOfWeek.FRIDAY);
        WEEKDAY_MAP.put("금", DayOfWeek.FRIDAY);
        WEEKDAY_MAP.put("토요일", DayOfWeek.SATURDAY);
        WEEKDAY_MAP.put("토", DayOfWeek.SATURDAY);
        WEEKDAY_MAP.put("일요일", DayOfWeek.SUNDAY);
        WEEKDAY_MAP.put("일", DayOfWeek.SUNDAY);
    }

    // ── 정규식 패턴 ──
    private static final Pattern N_DAYS_AGO = Pattern.compile("(\\d+)\\s*일\\s*전");
    private static final Pattern N_DAYS_LATER = Pattern.compile("(\\d+)\\s*일\\s*후");
    private static final Pattern N_WEEKS_AGO = Pattern.compile("(\\d+)\\s*주\\s*전");
    private static final Pattern N_WEEKS_LATER = Pattern.compile("(\\d+)\\s*주\\s*후");
    private static final Pattern N_MONTHS_AGO = Pattern.compile("(\\d+)\\s*달\\s*전");
    private static final Pattern N_MONTHS_LATER = Pattern.compile("(\\d+)\\s*달\\s*후");
    private static final Pattern N_YEARS_AGO = Pattern.compile("(\\d+)\\s*년\\s*전");
    private static final Pattern N_YEARS_LATER = Pattern.compile("(\\d+)\\s*년\\s*후");

    private static final Pattern EXPLICIT_YMD = Pattern.compile("(\\d{4})\\s*년\\s*(\\d{1,2})\\s*월\\s*(\\d{1,2})\\s*일");
    private static final Pattern EXPLICIT_MD = Pattern.compile("(\\d{1,2})\\s*월\\s*(\\d{1,2})\\s*일");
    private static final Pattern EXPLICIT_YM = Pattern.compile("(\\d{4})\\s*년\\s*(\\d{1,2})\\s*월(?!\\s*\\d)");
    private static final Pattern EXPLICIT_MONTH = Pattern.compile("(\\d{1,2})\\s*월(?!\\s*\\d)");

    private static final Pattern BETWEEN = Pattern.compile("(.+?)\\s*부터\\s*(.+?)\\s*까지");

    // "9월 셋째 주 화요일", "이번 달 둘째 주 월요일", "다음 달 마지막 주 금요일"
    private static final Pattern NTH_WEEKDAY_PATTERN = Pattern.compile(
        "(?:(이번\\s*달|다음\\s*달|저번\\s*달|지난\\s*달)|(\\d{1,2})\\s*월)?\\s*" +
        "(첫째|둘째|셋째|넷째|다섯째|여섯째|일곱째|여덟째|아홉째|열째|마지막|1째|2째|3째|4째|5째|6째|7째|8째|9째|10째)\\s*" +
        "주\\s*" +
        "(월요일|화요일|수요일|목요일|금요일|토요일|일요일|월|화|수|목|금|토|일)");

    // "이번 주 화요일", "다음 주 수요일", "저번 주 금요일"
    private static final Pattern WEEKDAY_RELATIVE_PATTERN = Pattern.compile(
        "(이번\\s*주|다음\\s*주|저번\\s*주|지난\\s*주)\\s*" +
        "(월요일|화요일|수요일|목요일|금요일|토요일|일요일|월|화|수|목|금|토|일)");

    // 복합: "다음 달 15일", "내년 3월 15일"
    private static final Pattern COMPOUND_MONTH_DAY = Pattern.compile(
        "(내년|작년|올해|다음\\s*달|저번\\s*달|지난\\s*달|이번\\s*달)\\s*" +
        "(?:(\\d{1,2})\\s*월)?\\s*(\\d{1,2})\\s*일");

    // 복합: "내년 3월 둘째 주 월요일"
    private static final Pattern COMPOUND_NTH_WEEKDAY = Pattern.compile(
        "(내년|작년|올해|다음\\s*년)\\s*" +
        "(\\d{1,2})\\s*월\\s*" +
        "(첫째|둘째|셋째|넷째|다섯째|여섯째|일곱째|여덟째|아홉째|열째|마지막|1째|2째|3째|4째|5째|6째|7째|8째|9째|10째)\\s*" +
        "주\\s*" +
        "(월요일|화요일|수요일|목요일|금요일|토요일|일요일|월|화|수|목|금|토|일)");

    // 주 시작/끝
    private static final Pattern START_END_PATTERN = Pattern.compile(
        "(이번\\s*주|다음\\s*주|저번\\s*주|지난\\s*주|이번\\s*달|다음\\s*달|저번\\s*달|지난\\s*달|올해|내년|작년)\\s*" +
        "(시작|끝|마지막)");

    /** 기본 생성자: 현재 날짜를 기준으로 함 */
    public NaturalDateParser() {
        this(LocalDate.now());
    }

    /** 기준일 지정 생성자 */
    public NaturalDateParser(LocalDate baseDate) {
        this.baseDate = baseDate;
        this.matchedKeywords = new ArrayList<>();
    }

    /**
     * 자연어 텍스트를 파싱하여 날짜 결과를 반환.
     *
     * @param text 자연어 날짜 표현 (예: "내일", "2026년 9월 셋째 주 화요일")
     * @return 파싱 결과
     */
    public DateParseResult parse(String text) {
        matchedKeywords.clear();
        if (text == null || text.trim().isEmpty()) {
            return new DateParseResult(null, null, null, text, matchedKeywords);
        }

        String normalized = normalize(text);

        // 9. 기간 표현 (BETWEEN) — 먼저 체크 (다른 패턴과 충돌 방지)
        DateParseResult result = tryBetween(normalized);
        if (result != null) return result;

        // 10. 복합 표현
        result = tryCompoundNthWeekday(normalized);
        if (result != null) return result;

        result = tryCompoundMonthDay(normalized);
        if (result != null) return result;

        // 8. 특수 날짜 (시작/끝)
        result = tryStartEnd(normalized);
        if (result != null) return result;

        // 6. 주차 + 요일 표현
        result = tryNthWeekday(normalized);
        if (result != null) return result;

        // 5. 요일 기반 표현
        result = tryWeekdayRelative(normalized);
        if (result != null) return result;

        // 7. 명시적 날짜 표현
        result = tryExplicitYMD(normalized);
        if (result != null) return result;

        result = tryExplicitMD(normalized);
        if (result != null) return result;

        result = tryExplicitYM(normalized);
        if (result != null) return result;

        result = tryExplicitMonth(normalized);
        if (result != null) return result;

        // 1. 상대일 표현
        result = tryRelativeDay(normalized);
        if (result != null) return result;

        // 2. 상대주 표현
        result = tryRelativeWeek(normalized);
        if (result != null) return result;

        // 3. 상대월 표현
        result = tryRelativeMonth(normalized);
        if (result != null) return result;

        // 4. 상대연도 표현
        result = tryRelativeYear(normalized);
        if (result != null) return result;

        // 매칭 실패
        return new DateParseResult(null, (LocalDate) null, null, text, matchedKeywords);
    }

    // ══════════════════════════════════════════════════════════
    // 1. 상대일 표현 (Relative Day)
    // ══════════════════════════════════════════════════════════

    private DateParseResult tryRelativeDay(String text) {
        if (containsAny(text, "오늘", "금일")) {
            matchedKeywords.add("오늘");
            return new DateParseResult(DateExpressionType.TODAY, baseDate, text, matchedKeywords);
        }
        if (text.contains("내일")) {
            matchedKeywords.add("내일");
            return new DateParseResult(DateExpressionType.TOMORROW, baseDate.plusDays(1), text, matchedKeywords);
        }
        if (text.contains("모레")) {
            matchedKeywords.add("모레");
            return new DateParseResult(DateExpressionType.DAY_AFTER_TOMORROW, baseDate.plusDays(2), text, matchedKeywords);
        }
        if (text.contains("글피")) {
            matchedKeywords.add("글피");
            return new DateParseResult(DateExpressionType.DAY_AFTER_DAY_AFTER_TOMORROW, baseDate.plusDays(3), text, matchedKeywords);
        }
        if (text.contains("어제")) {
            matchedKeywords.add("어제");
            return new DateParseResult(DateExpressionType.YESTERDAY, baseDate.minusDays(1), text, matchedKeywords);
        }
        if (text.contains("그제")) {
            matchedKeywords.add("그제");
            return new DateParseResult(DateExpressionType.DAY_BEFORE_YESTERDAY, baseDate.minusDays(2), text, matchedKeywords);
        }

        Matcher m = N_DAYS_AGO.matcher(text);
        if (m.find()) {
            int n = Integer.parseInt(m.group(1));
            matchedKeywords.add(n + "일 전");
            return new DateParseResult(DateExpressionType.DAYS_AGO, baseDate.minusDays(n), text, matchedKeywords);
        }

        m = N_DAYS_LATER.matcher(text);
        if (m.find()) {
            int n = Integer.parseInt(m.group(1));
            matchedKeywords.add(n + "일 후");
            return new DateParseResult(DateExpressionType.DAYS_LATER, baseDate.plusDays(n), text, matchedKeywords);
        }

        return null;
    }

    // ══════════════════════════════════════════════════════════
    // 2. 상대주 표현 (Relative Week)
    // ══════════════════════════════════════════════════════════

    private DateParseResult tryRelativeWeek(String text) {
        if (containsAny(text, "이번 주", "이번주")) {
            matchedKeywords.add("이번 주");
            LocalDate start = baseDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate end = baseDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            return new DateParseResult(DateExpressionType.THIS_WEEK, start, end, text, matchedKeywords);
        }
        if (containsAny(text, "다음 주", "다음주")) {
            matchedKeywords.add("다음 주");
            LocalDate start = baseDate.plusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate end = baseDate.plusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            return new DateParseResult(DateExpressionType.NEXT_WEEK, start, end, text, matchedKeywords);
        }
        if (containsAny(text, "저번 주", "저번주", "지난 주", "지난주")) {
            matchedKeywords.add("저번 주");
            LocalDate start = baseDate.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate end = baseDate.minusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            return new DateParseResult(DateExpressionType.LAST_WEEK, start, end, text, matchedKeywords);
        }

        Matcher m = N_WEEKS_AGO.matcher(text);
        if (m.find()) {
            int n = Integer.parseInt(m.group(1));
            matchedKeywords.add(n + "주 전");
            LocalDate start = baseDate.minusWeeks(n).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate end = baseDate.minusWeeks(n).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            return new DateParseResult(DateExpressionType.WEEKS_AGO, start, end, text, matchedKeywords);
        }

        m = N_WEEKS_LATER.matcher(text);
        if (m.find()) {
            int n = Integer.parseInt(m.group(1));
            matchedKeywords.add(n + "주 후");
            LocalDate start = baseDate.plusWeeks(n).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate end = baseDate.plusWeeks(n).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            return new DateParseResult(DateExpressionType.WEEKS_LATER, start, end, text, matchedKeywords);
        }

        return null;
    }

    // ══════════════════════════════════════════════════════════
    // 3. 상대월 표현 (Relative Month)
    // ══════════════════════════════════════════════════════════

    private DateParseResult tryRelativeMonth(String text) {
        if (containsAny(text, "이번 달", "이번달")) {
            matchedKeywords.add("이번 달");
            LocalDate start = baseDate.withDayOfMonth(1);
            LocalDate end = baseDate.with(TemporalAdjusters.lastDayOfMonth());
            return new DateParseResult(DateExpressionType.THIS_MONTH, start, end, text, matchedKeywords);
        }
        if (containsAny(text, "다음 달", "다음달")) {
            matchedKeywords.add("다음 달");
            LocalDate nextMonth = baseDate.plusMonths(1);
            LocalDate start = nextMonth.withDayOfMonth(1);
            LocalDate end = nextMonth.with(TemporalAdjusters.lastDayOfMonth());
            return new DateParseResult(DateExpressionType.NEXT_MONTH, start, end, text, matchedKeywords);
        }
        if (containsAny(text, "저번 달", "저번달", "지난 달", "지난달")) {
            matchedKeywords.add("저번 달");
            LocalDate lastMonth = baseDate.minusMonths(1);
            LocalDate start = lastMonth.withDayOfMonth(1);
            LocalDate end = lastMonth.with(TemporalAdjusters.lastDayOfMonth());
            return new DateParseResult(DateExpressionType.LAST_MONTH, start, end, text, matchedKeywords);
        }

        Matcher m = N_MONTHS_AGO.matcher(text);
        if (m.find()) {
            int n = Integer.parseInt(m.group(1));
            matchedKeywords.add(n + "달 전");
            LocalDate target = baseDate.minusMonths(n);
            LocalDate start = target.withDayOfMonth(1);
            LocalDate end = target.with(TemporalAdjusters.lastDayOfMonth());
            return new DateParseResult(DateExpressionType.MONTHS_AGO, start, end, text, matchedKeywords);
        }

        m = N_MONTHS_LATER.matcher(text);
        if (m.find()) {
            int n = Integer.parseInt(m.group(1));
            matchedKeywords.add(n + "달 후");
            LocalDate target = baseDate.plusMonths(n);
            LocalDate start = target.withDayOfMonth(1);
            LocalDate end = target.with(TemporalAdjusters.lastDayOfMonth());
            return new DateParseResult(DateExpressionType.MONTHS_LATER, start, end, text, matchedKeywords);
        }

        return null;
    }

    // ══════════════════════════════════════════════════════════
    // 4. 상대연도 표현 (Relative Year)
    // ══════════════════════════════════════════════════════════

    private DateParseResult tryRelativeYear(String text) {
        if (containsAny(text, "올해")) {
            matchedKeywords.add("올해");
            LocalDate start = LocalDate.of(baseDate.getYear(), 1, 1);
            LocalDate end = LocalDate.of(baseDate.getYear(), 12, 31);
            return new DateParseResult(DateExpressionType.THIS_YEAR, start, end, text, matchedKeywords);
        }
        if (text.contains("내년")) {
            matchedKeywords.add("내년");
            int year = baseDate.getYear() + 1;
            LocalDate start = LocalDate.of(year, 1, 1);
            LocalDate end = LocalDate.of(year, 12, 31);
            return new DateParseResult(DateExpressionType.NEXT_YEAR, start, end, text, matchedKeywords);
        }
        if (text.contains("작년")) {
            matchedKeywords.add("작년");
            int year = baseDate.getYear() - 1;
            LocalDate start = LocalDate.of(year, 1, 1);
            LocalDate end = LocalDate.of(year, 12, 31);
            return new DateParseResult(DateExpressionType.LAST_YEAR, start, end, text, matchedKeywords);
        }

        Matcher m = N_YEARS_AGO.matcher(text);
        if (m.find()) {
            int n = Integer.parseInt(m.group(1));
            matchedKeywords.add(n + "년 전");
            int year = baseDate.getYear() - n;
            LocalDate start = LocalDate.of(year, 1, 1);
            LocalDate end = LocalDate.of(year, 12, 31);
            return new DateParseResult(DateExpressionType.YEARS_AGO, start, end, text, matchedKeywords);
        }

        m = N_YEARS_LATER.matcher(text);
        if (m.find()) {
            int n = Integer.parseInt(m.group(1));
            matchedKeywords.add(n + "년 후");
            int year = baseDate.getYear() + n;
            LocalDate start = LocalDate.of(year, 1, 1);
            LocalDate end = LocalDate.of(year, 12, 31);
            return new DateParseResult(DateExpressionType.YEARS_LATER, start, end, text, matchedKeywords);
        }

        return null;
    }

    // ══════════════════════════════════════════════════════════
    // 5. 요일 기반 표현 (Weekday-based)
    // ══════════════════════════════════════════════════════════

    private DateParseResult tryWeekdayRelative(String text) {
        Matcher m = WEEKDAY_RELATIVE_PATTERN.matcher(text);
        if (!m.find()) return null;

        String weekRef = m.group(1);
        String weekdayStr = m.group(2);
        DayOfWeek targetDay = WEEKDAY_MAP.get(weekdayStr);
        if (targetDay == null) return null;

        matchedKeywords.add(weekRef + " " + weekdayStr);

        LocalDate weekBase;
        DateExpressionType type;

        if (weekRef.contains("이번")) {
            weekBase = baseDate;
            type = DateExpressionType.WEEKDAY_THIS;
        } else if (weekRef.contains("다음")) {
            weekBase = baseDate.plusWeeks(1);
            type = DateExpressionType.WEEKDAY_NEXT;
        } else {
            weekBase = baseDate.minusWeeks(1);
            type = DateExpressionType.WEEKDAY_LAST;
        }

        // 해당 주의 지정 요일 계산
        LocalDate mondayOfWeek = weekBase.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate result = mondayOfWeek.with(TemporalAdjusters.nextOrSame(targetDay));

        return new DateParseResult(type, result, text, matchedKeywords);
    }

    // ══════════════════════════════════════════════════════════
    // 6. 주차 + 요일 표현 (Nth Weekday of Month)
    // ══════════════════════════════════════════════════════════

    private DateParseResult tryNthWeekday(String text) {
        Matcher m = NTH_WEEKDAY_PATTERN.matcher(text);
        if (!m.find()) return null;

        String monthRef = m.group(1); // 이번 달, 다음 달, 저번 달
        String monthNumStr = m.group(2); // 숫자 월
        String nthStr = m.group(3); // 첫째, 둘째, 셋째, 마지막
        String weekdayStr = m.group(4); // 월요일 등

        DayOfWeek targetDay = WEEKDAY_MAP.get(weekdayStr);
        if (targetDay == null) return null;

        matchedKeywords.add((monthRef != null ? monthRef : (monthNumStr != null ? monthNumStr + "월 " : ""))
                + nthStr + " 주 " + weekdayStr);

        // 대상 연월 계산
        int year = baseDate.getYear();
        int month = baseDate.getMonthValue();

        if (monthRef != null) {
            if (monthRef.contains("다음")) {
                LocalDate next = baseDate.plusMonths(1);
                year = next.getYear();
                month = next.getMonthValue();
            } else if (monthRef.contains("저번") || monthRef.contains("지난")) {
                LocalDate prev = baseDate.minusMonths(1);
                year = prev.getYear();
                month = prev.getMonthValue();
            }
        } else if (monthNumStr != null) {
            month = Integer.parseInt(monthNumStr);
        }

        // N번째 계산
        if (nthStr.equals("마지막")) {
            LocalDate lastDay = LocalDate.of(year, month, 1).with(TemporalAdjusters.lastDayOfMonth());
            LocalDate result = lastDay.with(TemporalAdjusters.previousOrSame(targetDay));
            return new DateParseResult(DateExpressionType.LAST_WEEKDAY_OF_MONTH, result, text, matchedKeywords);
        }

        int n = parseKoreanOrdinal(nthStr);
        if (n < 1) return null;

        LocalDate firstOfMonth = LocalDate.of(year, month, 1);
        LocalDate firstTarget = firstOfMonth.with(TemporalAdjusters.nextOrSame(targetDay));
        LocalDate result = firstTarget.plusWeeks(n - 1);

        // 해당 월을 벗어나지 않는지 확인
        if (result.getMonthValue() != month || result.getYear() != year) {
            return null;
        }

        DateExpressionType type;
        if (monthRef != null && monthRef.contains("다음")) {
            type = DateExpressionType.NTH_WEEKDAY_NEXT_MONTH;
        } else if (monthRef != null) {
            type = DateExpressionType.NTH_WEEKDAY_RELATIVE;
        } else {
            type = DateExpressionType.NTH_WEEKDAY;
        }

        return new DateParseResult(type, result, text, matchedKeywords);
    }

    // ══════════════════════════════════════════════════════════
    // 7. 명시적 날짜 표현 (Explicit Date)
    // ══════════════════════════════════════════════════════════

    private DateParseResult tryExplicitYMD(String text) {
        Matcher m = EXPLICIT_YMD.matcher(text);
        if (!m.find()) return null;

        int year = Integer.parseInt(m.group(1));
        int month = Integer.parseInt(m.group(2));
        int day = Integer.parseInt(m.group(3));
        matchedKeywords.add(year + "년 " + month + "월 " + day + "일");

        try {
            LocalDate date = LocalDate.of(year, month, day);
            return new DateParseResult(DateExpressionType.EXPLICIT_YMD, date, text, matchedKeywords);
        } catch (Exception e) {
            return null;
        }
    }

    private DateParseResult tryExplicitMD(String text) {
        Matcher m = EXPLICIT_MD.matcher(text);
        if (!m.find()) return null;

        int month = Integer.parseInt(m.group(1));
        int day = Integer.parseInt(m.group(2));
        matchedKeywords.add(month + "월 " + day + "일");

        try {
            LocalDate date = LocalDate.of(baseDate.getYear(), month, day);
            return new DateParseResult(DateExpressionType.EXPLICIT_MD, date, text, matchedKeywords);
        } catch (Exception e) {
            return null;
        }
    }

    private DateParseResult tryExplicitYM(String text) {
        Matcher m = EXPLICIT_YM.matcher(text);
        if (!m.find()) return null;

        int year = Integer.parseInt(m.group(1));
        int month = Integer.parseInt(m.group(2));
        matchedKeywords.add(year + "년 " + month + "월");

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.with(TemporalAdjusters.lastDayOfMonth());
        return new DateParseResult(DateExpressionType.EXPLICIT_YM, start, end, text, matchedKeywords);
    }

    private DateParseResult tryExplicitMonth(String text) {
        Matcher m = EXPLICIT_MONTH.matcher(text);
        if (!m.find()) return null;

        int month = Integer.parseInt(m.group(1));
        matchedKeywords.add(month + "월");

        LocalDate start = LocalDate.of(baseDate.getYear(), month, 1);
        LocalDate end = start.with(TemporalAdjusters.lastDayOfMonth());
        return new DateParseResult(DateExpressionType.EXPLICIT_MONTH_ONLY, start, end, text, matchedKeywords);
    }

    // ══════════════════════════════════════════════════════════
    // 8. 특수 날짜 표현 (Start/End)
    // ══════════════════════════════════════════════════════════

    private DateParseResult tryStartEnd(String text) {
        Matcher m = START_END_PATTERN.matcher(text);
        if (!m.find()) return null;

        String period = m.group(1);
        String position = m.group(2);
        matchedKeywords.add(period + " " + position);

        boolean isStart = position.equals("시작");
        boolean isEnd = position.equals("끝") || position.equals("마지막");

        // 주 단위
        if (period.contains("주")) {
            LocalDate weekBase;
            if (period.contains("이번")) weekBase = baseDate;
            else if (period.contains("다음")) weekBase = baseDate.plusWeeks(1);
            else weekBase = baseDate.minusWeeks(1);

            if (isStart) {
                LocalDate result = weekBase.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                return new DateParseResult(DateExpressionType.START_OF_WEEK, result, text, matchedKeywords);
            } else {
                LocalDate result = weekBase.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                return new DateParseResult(DateExpressionType.END_OF_WEEK, result, text, matchedKeywords);
            }
        }

        // 월 단위
        if (period.contains("달")) {
            LocalDate monthBase;
            if (period.contains("이번")) monthBase = baseDate;
            else if (period.contains("다음")) monthBase = baseDate.plusMonths(1);
            else monthBase = baseDate.minusMonths(1);

            if (isStart) {
                LocalDate result = monthBase.withDayOfMonth(1);
                return new DateParseResult(DateExpressionType.START_OF_MONTH, result, text, matchedKeywords);
            } else {
                LocalDate result = monthBase.with(TemporalAdjusters.lastDayOfMonth());
                return new DateParseResult(DateExpressionType.END_OF_MONTH, result, text, matchedKeywords);
            }
        }

        // 연도 단위
        if (period.contains("올해") || period.contains("이번") && period.contains("년")) {
            if (isStart) {
                return new DateParseResult(DateExpressionType.START_OF_YEAR, LocalDate.of(baseDate.getYear(), 1, 1), text, matchedKeywords);
            } else {
                return new DateParseResult(DateExpressionType.END_OF_YEAR, LocalDate.of(baseDate.getYear(), 12, 31), text, matchedKeywords);
            }
        }
        if (period.contains("내년")) {
            int y = baseDate.getYear() + 1;
            if (isStart) {
                return new DateParseResult(DateExpressionType.START_OF_YEAR, LocalDate.of(y, 1, 1), text, matchedKeywords);
            } else {
                return new DateParseResult(DateExpressionType.END_OF_YEAR, LocalDate.of(y, 12, 31), text, matchedKeywords);
            }
        }
        if (period.contains("작년")) {
            int y = baseDate.getYear() - 1;
            if (isStart) {
                return new DateParseResult(DateExpressionType.START_OF_YEAR, LocalDate.of(y, 1, 1), text, matchedKeywords);
            } else {
                return new DateParseResult(DateExpressionType.END_OF_YEAR, LocalDate.of(y, 12, 31), text, matchedKeywords);
            }
        }

        return null;
    }

    // ══════════════════════════════════════════════════════════
    // 9. 기간 표현 (Between)
    // ══════════════════════════════════════════════════════════

    private DateParseResult tryBetween(String text) {
        Matcher m = BETWEEN.matcher(text);
        if (!m.find()) return null;

        String fromText = m.group(1).trim();
        String toText = m.group(2).trim();

        DateParseResult fromResult = new NaturalDateParser(baseDate).parse(fromText);
        DateParseResult toResult = new NaturalDateParser(baseDate).parse(toText);

        if (fromResult.getStartDate() == null || toResult.getStartDate() == null) return null;

        matchedKeywords.add(fromText + "부터 " + toText + "까지");

        return new DateParseResult(DateExpressionType.BETWEEN,
                fromResult.getStartDate(), toResult.getStartDate(), text, matchedKeywords);
    }

    // ══════════════════════════════════════════════════════════
    // 10. 복합 표현 (Compound)
    // ══════════════════════════════════════════════════════════

    private DateParseResult tryCompoundNthWeekday(String text) {
        Matcher m = COMPOUND_NTH_WEEKDAY.matcher(text);
        if (!m.find()) return null;

        String yearRef = m.group(1); // 내년, 작년, 올해
        int month = Integer.parseInt(m.group(2));
        String nthStr = m.group(3);
        String weekdayStr = m.group(4);

        DayOfWeek targetDay = WEEKDAY_MAP.get(weekdayStr);
        if (targetDay == null) return null;

        int year = baseDate.getYear();
        if (yearRef.contains("내년")) year++;
        else if (yearRef.contains("작년")) year--;

        matchedKeywords.add(yearRef + " " + month + "월 " + nthStr + " 주 " + weekdayStr);

        if (nthStr.equals("마지막")) {
            LocalDate lastDay = LocalDate.of(year, month, 1).with(TemporalAdjusters.lastDayOfMonth());
            LocalDate result = lastDay.with(TemporalAdjusters.previousOrSame(targetDay));
            return new DateParseResult(DateExpressionType.COMPOUND, result, text, matchedKeywords);
        }

        int n = parseKoreanOrdinal(nthStr);
        if (n < 1) return null;

        LocalDate firstOfMonth = LocalDate.of(year, month, 1);
        LocalDate firstTarget = firstOfMonth.with(TemporalAdjusters.nextOrSame(targetDay));
        LocalDate result = firstTarget.plusWeeks(n - 1);

        if (result.getMonthValue() != month || result.getYear() != year) return null;

        return new DateParseResult(DateExpressionType.COMPOUND, result, text, matchedKeywords);
    }

    private DateParseResult tryCompoundMonthDay(String text) {
        Matcher m = COMPOUND_MONTH_DAY.matcher(text);
        if (!m.find()) return null;

        String monthRef = m.group(1); // 내년, 작년, 다음 달, 저번 달, 이번 달, 올해
        String monthStr = m.group(2); // 숫자 월 (선택)
        String day = m.group(3);

        int year = baseDate.getYear();
        int month = baseDate.getMonthValue();

        if (monthRef.contains("내년")) year++;
        else if (monthRef.contains("작년")) year--;
        else if (monthRef.contains("다음")) {
            LocalDate next = baseDate.plusMonths(1);
            year = next.getYear();
            month = next.getMonthValue();
        } else if (monthRef.contains("저번") || monthRef.contains("지난")) {
            LocalDate prev = baseDate.minusMonths(1);
            year = prev.getYear();
            month = prev.getMonthValue();
        }

        if (monthStr != null) {
            month = Integer.parseInt(monthStr);
        }

        matchedKeywords.add(monthRef + (monthStr != null ? " " + monthStr + "월" : "") + " " + day + "일");

        try {
            LocalDate result = LocalDate.of(year, month, Integer.parseInt(day));
            return new DateParseResult(DateExpressionType.COMPOUND, result, text, matchedKeywords);
        } catch (Exception e) {
            return null;
        }
    }

    // ══════════════════════════════════════════════════════════
    // 유틸리티 메서드
    // ══════════════════════════════════════════════════════════

    private String normalize(String text) {
        return text.trim()
                .replaceAll("\\s+", " ")
                .replaceAll("이번주", "이번 주")
                .replaceAll("다음주", "다음 주")
                .replaceAll("저번주", "저번 주")
                .replaceAll("지난주", "지난 주")
                .replaceAll("이번달", "이번 달")
                .replaceAll("다음달", "다음 달")
                .replaceAll("저번달", "저번 달")
                .replaceAll("지난달", "지난 달");
    }

    private boolean containsAny(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw)) return true;
        }
        return false;
    }

    private int parseKoreanOrdinal(String str) {
        if (KOREAN_NUMBERS.containsKey(str)) {
            return KOREAN_NUMBERS.get(str);
        }
        // "N째" 패턴
        Pattern p = Pattern.compile("(\\d+)째");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return -1;
    }
}