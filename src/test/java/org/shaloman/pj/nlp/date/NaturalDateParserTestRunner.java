package org.shaloman.pj.nlp.date;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * NaturalDateParser 테스트 결과 출력기.
 * 각 테스트 케이스의 입력, 예상값, 실제값, 통과여부, 공휴일/주말 여부를 표 형태로 출력한다.
 */
public class NaturalDateParserTestRunner {

    private static final LocalDate BASE = LocalDate.of(2026, 9, 15); // 화요일
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private int passed = 0;
    private int failed = 0;
    private KoreanHolidayChecker holidayChecker = new KoreanHolidayChecker();

    public static void main(String[] args) {
        NaturalDateParserTestRunner runner = new NaturalDateParserTestRunner();
        runner.run();
    }

    public void run() {
        NaturalDateParser parser = new NaturalDateParser(BASE);

        System.out.println();
        System.out.println("기준일: " + BASE.format(FMT) + " (" + dayName(BASE) + ") — " + holidayChecker.getDayTypeMessage(BASE));
        System.out.println();
        System.out.println("┌────┬──────────────────┬────────────────────────────────────────┬──────────────────┬──────────────────┬──────┬──────────────────────┐");
        System.out.println("│ No │ 카테고리         │ 입력                                    │ 예상값           │ 실제값           │ 결과 │ 공휴일/주말          │");
        System.out.println("├────┼──────────────────┼────────────────────────────────────────┼──────────────────┼──────────────────┼──────┼──────────────────────┤");

        int no = 1;

        // ── 1. 상대일 ──
        no = test(parser, no, "상대일", "오늘", "2026-09-15", formatDate(parseSingle(parser, "오늘")));
        no = test(parser, no, "상대일", "금일", "2026-09-15", formatDate(parseSingle(parser, "금일")));
        no = test(parser, no, "상대일", "내일", "2026-09-16", formatDate(parseSingle(parser, "내일")));
        no = test(parser, no, "상대일", "모레", "2026-09-17", formatDate(parseSingle(parser, "모레")));
        no = test(parser, no, "상대일", "글피", "2026-09-18", formatDate(parseSingle(parser, "글피")));
        no = test(parser, no, "상대일", "어제", "2026-09-14", formatDate(parseSingle(parser, "어제")));
        no = test(parser, no, "상대일", "그제", "2026-09-13", formatDate(parseSingle(parser, "그제")));
        no = test(parser, no, "상대일", "3일 전", "2026-09-12", formatDate(parseSingle(parser, "3일 전")));
        no = test(parser, no, "상대일", "5일 후", "2026-09-20", formatDate(parseSingle(parser, "5일 후")));

        // ── 2. 상대주 ──
        no = test(parser, no, "상대주", "이번 주", "2026-09-14 ~ 2026-09-20", formatRange(parser.parse("이번 주")));
        no = test(parser, no, "상대주", "다음 주", "2026-09-21 ~ 2026-09-27", formatRange(parser.parse("다음 주")));
        no = test(parser, no, "상대주", "저번 주", "2026-09-07 ~ 2026-09-13", formatRange(parser.parse("저번 주")));
        no = test(parser, no, "상대주", "2주 전", "2026-08-31 ~ 2026-09-06", formatRange(parser.parse("2주 전")));
        no = test(parser, no, "상대주", "3주 후", "2026-10-05 ~ 2026-10-11", formatRange(parser.parse("3주 후")));

        // ── 3. 상대월 ──
        no = test(parser, no, "상대월", "이번 달", "2026-09-01 ~ 2026-09-30", formatRange(parser.parse("이번 달")));
        no = test(parser, no, "상대월", "다음 달", "2026-10-01 ~ 2026-10-31", formatRange(parser.parse("다음 달")));
        no = test(parser, no, "상대월", "저번 달", "2026-08-01 ~ 2026-08-31", formatRange(parser.parse("저번 달")));
        no = test(parser, no, "상대월", "2달 전", "2026-07-01 ~ 2026-07-31", formatRange(parser.parse("2달 전")));
        no = test(parser, no, "상대월", "3달 후", "2026-12-01 ~ 2026-12-31", formatRange(parser.parse("3달 후")));

        // ── 4. 상대연도 ──
        no = test(parser, no, "상대연도", "올해", "2026-01-01 ~ 2026-12-31", formatRange(parser.parse("올해")));
        no = test(parser, no, "상대연도", "내년", "2027-01-01 ~ 2027-12-31", formatRange(parser.parse("내년")));
        no = test(parser, no, "상대연도", "작년", "2025-01-01 ~ 2025-12-31", formatRange(parser.parse("작년")));
        no = test(parser, no, "상대연도", "1년 전", "2025-01-01 ~ 2025-12-31", formatRange(parser.parse("1년 전")));
        no = test(parser, no, "상대연도", "2년 후", "2028-01-01 ~ 2028-12-31", formatRange(parser.parse("2년 후")));

        // ── 5. 요일 기반 ──
        no = test(parser, no, "요일기반", "이번 주 화요일", "2026-09-15", formatDate(parseSingle(parser, "이번 주 화요일")));
        no = test(parser, no, "요일기반", "이번 주 금요일", "2026-09-18", formatDate(parseSingle(parser, "이번 주 금요일")));
        no = test(parser, no, "요일기반", "다음 주 수요일", "2026-09-23", formatDate(parseSingle(parser, "다음 주 수요일")));
        no = test(parser, no, "요일기반", "저번 주 금요일", "2026-09-11", formatDate(parseSingle(parser, "저번 주 금요일")));
        no = test(parser, no, "요일기반", "3주 후 월요일", "2026-10-05", formatDate(parseSingle(parser, "3주 후 월요일")));
        no = test(parser, no, "요일기반", "3주 후 수요일", "2026-10-07", formatDate(parseSingle(parser, "3주 후 수요일")));
        no = test(parser, no, "요일기반", "2주 전 금요일", "2026-09-04", formatDate(parseSingle(parser, "2주 전 금요일")));

        // ── 6. 주차 + 요일 ──
        no = test(parser, no, "주차+요일", "9월 셋째 주 화요일", "2026-09-15", formatDate(parseSingle(parser, "9월 셋째 주 화요일")));
        no = test(parser, no, "주차+요일", "9월 둘째 주 일요일", "2026-09-13", formatDate(parseSingle(parser, "9월 둘째 주 일요일")));
        no = test(parser, no, "주차+요일", "이번 달 첫째 주 월요일", "2026-09-07", formatDate(parseSingle(parser, "이번 달 첫째 주 월요일")));
        no = test(parser, no, "주차+요일", "다음 달 둘째 주 월요일", "2026-10-12", formatDate(parseSingle(parser, "다음 달 둘째 주 월요일")));
        no = test(parser, no, "주차+요일", "9월 마지막 주 금요일", "2026-09-25", formatDate(parseSingle(parser, "9월 마지막 주 금요일")));

        // ── 7. 명시적 날짜 ──
        no = test(parser, no, "명시적", "2026년 9월 15일", "2026-09-15", formatDate(parseSingle(parser, "2026년 9월 15일")));
        no = test(parser, no, "명시적", "9월 15일", "2026-09-15", formatDate(parseSingle(parser, "9월 15일")));
        no = test(parser, no, "명시적", "2026년 9월", "2026-09-01 ~ 2026-09-30", formatRange(parser.parse("2026년 9월")));
        no = test(parser, no, "명시적", "9월", "2026-09-01 ~ 2026-09-30", formatRange(parser.parse("9월")));

        // ── 8. 특수 날짜 ──
        no = test(parser, no, "특수", "이번 주 시작", "2026-09-14", formatDate(parseSingle(parser, "이번 주 시작")));
        no = test(parser, no, "특수", "이번 주 끝", "2026-09-20", formatDate(parseSingle(parser, "이번 주 끝")));
        no = test(parser, no, "특수", "이번 달 시작", "2026-09-01", formatDate(parseSingle(parser, "이번 달 시작")));
        no = test(parser, no, "특수", "이번 달 끝", "2026-09-30", formatDate(parseSingle(parser, "이번 달 끝")));
        no = test(parser, no, "특수", "올해 시작", "2026-01-01", formatDate(parseSingle(parser, "올해 시작")));
        no = test(parser, no, "특수", "올해 끝", "2026-12-31", formatDate(parseSingle(parser, "올해 끝")));

        // ── 9. 기간 표현 ──
        no = test(parser, no, "기간", "9월 10일부터 9월 20일까지", "2026-09-10 ~ 2026-09-20", formatRange(parser.parse("9월 10일부터 9월 20일까지")));
        no = test(parser, no, "기간", "어제부터 내일까지", "2026-09-14 ~ 2026-09-16", formatRange(parser.parse("어제부터 내일까지")));

        // ── 10. 복합 표현 ──
        no = test(parser, no, "복합", "다음 달 15일", "2026-10-15", formatDate(parseSingle(parser, "다음 달 15일")));
        no = test(parser, no, "복합", "내년 3월 15일", "2027-03-15", formatDate(parseSingle(parser, "내년 3월 15일")));
        no = test(parser, no, "복합", "내년 3월 둘째 주 월요일", "2027-03-08", formatDate(parseSingle(parser, "내년 3월 둘째 주 월요일")));

        // ── 11. 특정 날짜 이후 가장 빠른 요일 ──
        no = test(parser, no, "이후요일", "2026년 9월 30일 후 가장 빠른 금요일", "2026-10-02", formatDate(parseSingle(parser, "2026년 9월 30일 후 가장 빠른 금요일")));
        no = test(parser, no, "이후요일", "9월 30일 이후 첫 번째 금요일", "2026-10-02", formatDate(parseSingle(parser, "9월 30일 이후 첫 번째 금요일")));

        // ── 공휴일/주말 테스트 ──
        no = test(parser, no, "공휴일", "2026년 9월 25일 (추석)", "2026-09-25", formatDate(parseSingle(parser, "2026년 9월 25일")));
        no = test(parser, no, "공휴일", "2026년 10월 5일 (대체공휴일)", "2026-10-05", formatDate(parseSingle(parser, "2026년 10월 5일")));
        no = test(parser, no, "공휴일", "2026년 12월 25일 (크리스마스)", "2026-12-25", formatDate(parseSingle(parser, "2026년 12월 25일")));
        no = test(parser, no, "주말", "2026년 9월 19일 (토요일)", "2026-09-19", formatDate(parseSingle(parser, "2026년 9월 19일")));
        no = test(parser, no, "주말", "2026년 9월 20일 (일요일)", "2026-09-20", formatDate(parseSingle(parser, "2026년 9월 20일")));

        // ── 예외 케이스 ──
        no = test(parser, no, "예외", "안녕하세요", "null", parseSingle(parser, "안녕하세요") == null ? "null" : "not null");
        no = test(parser, no, "예외", "빈 문자열", "null", parseSingle(parser, "") == null ? "null" : "not null");
        no = test(parser, no, "예외", "null 입력", "null", parseSingle(parser, null) == null ? "null" : "not null");
        no = test(parser, no, "예외", "이번주 (공백없음)", "2026-09-14 ~ 2026-09-20", formatRange(parser.parse("이번주")));

        System.out.println("└────┴──────────────────┴────────────────────────────────────────┴──────────────────┴──────────────────┴──────┴──────────────────────┘");
        System.out.println();
        System.out.println("  통과: " + passed + " / 실패: " + failed + " / 전체: " + (passed + failed));
        System.out.println();

        // ── 공휴일/주말 상세 출력 ──
        System.out.println("── 공휴일/주말 판별 결과 ──");
        System.out.println();
        printHolidayDetail("2026-09-15", "오늘 (화요일)");
        printHolidayDetail("2026-09-19", "토요일");
        printHolidayDetail("2026-09-20", "일요일");
        printHolidayDetail("2026-09-25", "추석");
        printHolidayDetail("2026-09-24", "추석 연휴");
        printHolidayDetail("2026-09-26", "추석 연휴 (토요일)");
        printHolidayDetail("2026-10-05", "대체공휴일(개천절)");
        printHolidayDetail("2026-10-03", "개천절 (토요일)");
        printHolidayDetail("2026-12-25", "크리스마스");
        printHolidayDetail("2026-01-01", "신정");
        printHolidayDetail("2026-02-17", "설날");
        printHolidayDetail("2026-03-01", "3·1절 (일요일)");
        printHolidayDetail("2026-03-02", "대체공휴일(3·1절)");
        printHolidayDetail("2026-08-15", "광복절 (토요일)");
        printHolidayDetail("2026-08-17", "대체공휴일(광복절)");
        printHolidayDetail("2026-05-05", "어린이날");
        printHolidayDetail("2026-05-24", "부처님오신날 (일요일)");
        printHolidayDetail("2026-05-25", "대체공휴일(부처님오신날)");
        printHolidayDetail("2026-09-18", "이번 주 금요일 (평일)");
        System.out.println();
    }

    private int test(NaturalDateParser parser, int no, String category, String input, String expected, String actual) {
        boolean isPass = expected.equals(actual);
        if (isPass) passed++; else failed++;
        String status = isPass ? "PASS" : "FAIL";
        String cat = padRight(category, 14);
        String inp = padRight(input, 38);
        String exp = padRight(expected, 16);
        String act = padRight(actual, 16);

        // 공휴일/주말 메시지
        String dayMsg = "";
        LocalDate resultDate = parser.parse(input).getStartDate();
        if (resultDate != null) {
            dayMsg = holidayChecker.getDayTypeMessage(resultDate);
        }
        String dayMsgPadded = padRight(dayMsg, 20);

        System.out.printf("│ %2d │ %s │ %s │ %s │ %s │ %s │ %s │%n", no, cat, inp, exp, act, status, dayMsgPadded);
        return no + 1;
    }

    private void printHolidayDetail(String dateStr, String description) {
        LocalDate date = LocalDate.parse(dateStr, FMT);
        boolean isHoliday = holidayChecker.isHoliday(date);
        boolean isWeekend = holidayChecker.isWeekend(date);
        String message = holidayChecker.getDayTypeMessage(date);

        String type;
        if (isHoliday) type = "공휴일";
        else if (isWeekend) type = "주말";
        else type = "평일";

        System.out.printf("  %-12s %-30s → %-8s  %s%n", dateStr, description, type, message);
    }

    private LocalDate parseSingle(NaturalDateParser parser, String text) {
        DateParseResult r = parser.parse(text);
        return r.getStartDate();
    }

    private String formatDate(LocalDate date) {
        return date == null ? "null" : date.format(FMT);
    }

    private String formatRange(DateParseResult r) {
        if (r.getStartDate() == null) return "null";
        if (r.getEndDate() != null) {
            return r.getStartDate().format(FMT) + " ~ " + r.getEndDate().format(FMT);
        }
        return r.getStartDate().format(FMT);
    }

    private String dayName(LocalDate date) {
        String[] days = {"월", "화", "수", "목", "금", "토", "일"};
        return days[date.getDayOfWeek().getValue() - 1] + "요일";
    }

    private String padRight(String str, int width) {
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() < width) sb.append(" ");
        return sb.substring(0, Math.min(sb.length(), width));
    }
}