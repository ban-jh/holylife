package org.shaloman.pj.nlp.date;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 대한민국 법정 공휴일 및 대체공휴일 판별기.
 *
 * 고정 공휴일(양력)은 매년 같은 날짜이므로 직접 비교하고,
 * 음력 기반 공휴일(설날, 추석, 부처님오신날)은 연도별로 미리 계산하여 등록한다.
 *
 * 대체공휴일은 공휴일이 주말(토/일)과 겹칠 경우 다음 평일을 공휴일로 지정한다.
 */
public class KoreanHolidayChecker {

    /** 공휴일명을 반환하기 위한 맵 (날짜 → 공휴일명) */
    private final Map<LocalDate, String> holidays;

    /**
     * 지정한 연도의 한국 공휴일 판별기를 생성한다.
     *
     * @param year 연도 (예: 2026)
     */
    public KoreanHolidayChecker(int year) {
        holidays = new HashMap<>();
        loadHolidays(year);
    }

    /**
     * 2025~2027년을 모두 포함하는 판별기를 생성한다.
     * 기본적으로 3년치를 미리 로드하여 연도 경계 처리를 단순화한다.
     */
    public KoreanHolidayChecker() {
        holidays = new HashMap<>();
        for (int y = 2025; y <= 2027; y++) {
            loadHolidays(y);
        }
    }

    /**
     * 해당 날짜가 공휴일 또는 주말(토/일)인지 판별.
     *
     * @param date 판별할 날짜
     * @return 공휴일/주말이면 true, 평일이면 false
     */
    public boolean isHolidayOrWeekend(LocalDate date) {
        if (date == null) return false;
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return true;
        }
        return holidays.containsKey(date);
    }

    /**
     * 해당 날짜가 공휴일(법정공휴일 + 대체공휴일)인지 판별.
     * 주말(토/일)은 공휴일에서 제외한다.
     *
     * @param date 판별할 날짜
     * @return 공휴일이면 true, 아니면 false
     */
    public boolean isHoliday(LocalDate date) {
        if (date == null) return false;
        return holidays.containsKey(date);
    }

    /**
     * 해당 날짜가 주말(토/일)인지 판별.
     *
     * @param date 판별할 날짜
     * @return 주말이면 true, 아니면 false
     */
    public boolean isWeekend(LocalDate date) {
        if (date == null) return false;
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    /**
     * 공휴일/주말 여부에 따른 메시지 반환.
     *
     * @param date 판별할 날짜
     * @return "공휴일입니다" 또는 "평일입니다"
     */
    public String getDayTypeMessage(LocalDate date) {
        if (date == null) return "날짜를 확인할 수 없습니다";
        if (isHoliday(date)) {
            return "공휴일입니다 (" + holidays.get(date) + ")";
        }
        if (isWeekend(date)) {
            return "주말입니다 (" + dayName(date) + ")";
        }
        return "평일입니다 (" + dayName(date) + ")";
    }

    /**
     * 공휴일명 반환.
     *
     * @param date 날짜
     * @return 공휴일명 (공휴일이 아닌 경우 null)
     */
    public String getHolidayName(LocalDate date) {
        return holidays.get(date);
    }

    // ── 공휴일 데이터 로드 ──

    private void loadHolidays(int year) {
        // ── 고정 공휴일 (양력) ──
        put(year, 1, 1, "신정");
        put(year, 3, 1, "3·1절");
        put(year, 5, 5, "어린이날");
        put(year, 6, 6, "현충일");
        put(year, 8, 15, "광복절");
        put(year, 10, 3, "개천절");
        put(year, 10, 9, "한글날");
        put(year, 12, 25, "크리스마스");

        // ── 노동절 (근로자의 날, 5월 1일) ──
        put(year, 5, 1, "근로자의날");

        // ── 음력 기반 공휴일 (연도별 미리 계산된 날짜) ──
        loadLunarHolidays(year);
    }

    /**
     * 음력 기반 공휴일을 연도별로 등록한다.
     * 2025~2027년까지의 정확한 양력 변환 날짜를 하드코딩한다.
     * 이후 연도가 필요하면 여기에 추가한다.
     */
    private void loadLunarHolidays(int year) {
        switch (year) {
            case 2025:
                // 설날: 2025-01-29 (수)
                put(2025, 1, 28, "설날 연휴");
                put(2025, 1, 29, "설날");
                put(2025, 1, 30, "설날 연휴");
                // 부처님 오신날: 2025-05-05 (월)
                put(2025, 5, 5, "부처님오신날");
                // 추석: 2025-10-06 (월)
                put(2025, 10, 5, "추석 연휴");
                put(2025, 10, 6, "추석");
                put(2025, 10, 7, "추석 연휴");
                put(2025, 10, 8, "대체공휴일(추석)");
                break;

            case 2026:
                // 설날: 2026-02-17 (화)
                put(2026, 2, 16, "설날 연휴");
                put(2026, 2, 17, "설날");
                put(2026, 2, 18, "설날 연휴");
                // 3·1절 대체공휴일: 2026-03-02 (월) — 3/1이 일요일
                put(2026, 3, 2, "대체공휴일(3·1절)");
                // 부처님 오신날: 2026-05-24 (일)
                put(2026, 5, 24, "부처님오신날");
                put(2026, 5, 25, "대체공휴일(부처님오신날)");
                // 지방선거: 2026-06-03 (수)
                put(2026, 6, 3, "제9회 전국동시지방선거");
                // 광복절 대체공휴일: 2026-08-17 (월) — 8/15가 토요일
                put(2026, 8, 17, "대체공휴일(광복절)");
                // 추석: 2026-09-25 (금)
                put(2026, 9, 24, "추석 연휴");
                put(2026, 9, 25, "추석");
                put(2026, 9, 26, "추석 연휴");
                // 개천절 대체공휴일: 2026-10-05 (월) — 10/3이 토요일
                put(2026, 10, 5, "대체공휴일(개천절)");
                break;

            case 2027:
                // 설날: 2027-02-07 (일)
                put(2027, 2, 6, "설날 연휴");
                put(2027, 2, 7, "설날");
                put(2027, 2, 8, "설날 연휴");
                put(2027, 2, 9, "대체공휴일(설날)");
                // 부처님 오신날: 2027-05-13 (목)
                put(2027, 5, 13, "부처님오신날");
                // 추석: 2027-09-15 (수)
                put(2027, 9, 14, "추석 연휴");
                put(2027, 9, 15, "추석");
                put(2027, 9, 16, "추석 연휴");
                break;
        }
    }

    private void put(int year, int month, int day, String name) {
        try {
            holidays.put(LocalDate.of(year, month, day), name);
        } catch (Exception e) {
            // 잘못된 날짜는 무시
        }
    }

    private String dayName(LocalDate date) {
        String[] days = {"월", "화", "수", "목", "금", "토", "일"};
        return days[date.getDayOfWeek().getValue() - 1] + "요일";
    }
}