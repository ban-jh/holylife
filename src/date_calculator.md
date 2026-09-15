# Date Calculator (자연어 날짜 계산기)

## 개요

한국어 자연어 표현을 파싱하여 날짜를 계산하는 모듈입니다.
오늘 날짜를 기준으로 "내일", "2026년 9월 셋째 주 화요일", "다음 달 15일" 등의
자연어 입력을 받아 해당 날짜(또는 기간)를 반환합니다.

## 패키지 구조

```
org.shaloman.pj.nlp.date
├── DateExpressionType.java   # 표현 타입 열거형 (10 카테고리, 35 타입)
├── DateParseResult.java       # 파싱 결과 모델 (시작일, 종료일, 타입, 키워드)
├── NaturalDateParser.java     # 자연어 파서 (정규식 기반 분석 + 날짜 계산)
└── NaturalDateParserTest.java # 단위 테스트 (53개 케이스, 기준일 2026-09-15)
```

## 지원 표현 타입

### 1. 상대일 (Relative Day)

| 자연어 예시 | 타입 코드 | 설명 |
|---|---|---|
| 오늘, 금일 | `TODAY` | 기준일 |
| 내일 | `TOMORROW` | 기준일 + 1 |
| 모레 | `DAY_AFTER_TOMORROW` | 기준일 + 2 |
| 글피 | `DAY_AFTER_DAY_AFTER_TOMORROW` | 기준일 + 3 |
| 어제 | `YESTERDAY` | 기준일 - 1 |
| 그제 | `DAY_BEFORE_YESTERDAY` | 기준일 - 2 |
| N일 전 | `DAYS_AGO` | 기준일 - N |
| N일 후 | `DAYS_LATER` | 기준일 + N |

### 2. 상대주 (Relative Week)

| 자연어 예시 | 타입 코드 | 설명 |
|---|---|---|
| 이번 주 | `THIS_WEEK` | 현재 주 (월~일, 기간 반환) |
| 다음 주 | `NEXT_WEEK` | 기준주 + 1 (기간 반환) |
| 저번 주, 지난 주 | `LAST_WEEK` | 기준주 - 1 (기간 반환) |
| N주 전 | `WEEKS_AGO` | 기준주 - N (기간 반환) |
| N주 후 | `WEEKS_LATER` | 기준주 + N (기간 반환) |

### 3. 상대월 (Relative Month)

| 자연어 예시 | 타입 코드 | 설명 |
|---|---|---|
| 이번 달 | `THIS_MONTH` | 현재 월 (1일~말일, 기간 반환) |
| 다음 달 | `NEXT_MONTH` | 기준월 + 1 (기간 반환) |
| 저번 달, 지난 달 | `LAST_MONTH` | 기준월 - 1 (기간 반환) |
| N달 전 | `MONTHS_AGO` | 기준월 - N (기간 반환) |
| N달 후 | `MONTHS_LATER` | 기준월 + N (기간 반환) |

### 4. 상대연도 (Relative Year)

| 자연어 예시 | 타입 코드 | 설명 |
|---|---|---|
| 올해 | `THIS_YEAR` | 현재 연도 (1/1~12/31, 기간 반환) |
| 내년 | `NEXT_YEAR` | 기준연도 + 1 (기간 반환) |
| 작년 | `LAST_YEAR` | 기준연도 - 1 (기간 반환) |
| N년 전 | `YEARS_AGO` | 기준연도 - N (기간 반환) |
| N년 후 | `YEARS_LATER` | 기준연도 + N (기간 반환) |

### 5. 요일 기반 (Weekday-based)

| 자연어 예시 | 타입 코드 | 설명 |
|---|---|---|
| 이번 주 화요일 | `WEEKDAY_THIS` | 현재 주의 지정 요일 |
| 다음 주 수요일 | `WEEKDAY_NEXT` | 다음 주의 지정 요일 |
| 저번 주 금요일 | `WEEKDAY_LAST` | 지난 주의 지정 요일 |

지원 요일: 월, 화, 수, 목, 금, 토, 일 (요일명 또는 한 글자)

### 6. 주차 + 요일 (Nth Weekday of Month)

| 자연어 예시 | 타입 코드 | 설명 |
|---|---|---|
| 9월 셋째 주 화요일 | `NTH_WEEKDAY` | 특정 월의 N번째 요일 |
| 이번 달 첫째 주 월요일 | `NTH_WEEKDAY_RELATIVE` | 현재 월의 N번째 요일 |
| 다음 달 둘째 주 월요일 | `NTH_WEEKDAY_NEXT_MONTH` | 다음 월의 N번째 요일 |
| 9월 마지막 주 금요일 | `LAST_WEEKDAY_OF_MONTH` | 특정 월의 마지막 요일 |

주차 표현: 첫째, 둘째, 셋째, 넷째, 다섯째, 여섯째, 일곱째, 여덟째, 아홉째, 열째, 마지막, 1째~10째

### 7. 명시적 날짜 (Explicit Date)

| 자연어 예시 | 타입 코드 | 설명 |
|---|---|---|
| 2026년 9월 15일 | `EXPLICIT_YMD` | 연/월/일 명시 |
| 9월 15일 | `EXPLICIT_MD` | 월/일 명시 (연도는 현재) |
| 2026년 9월 | `EXPLICIT_YM` | 연/월 명시 (기간 반환) |
| 9월 | `EXPLICIT_MONTH_ONLY` | 월만 명시 (기간 반환) |

### 8. 특수 날짜 (Special Date)

| 자연어 예시 | 타입 코드 | 설명 |
|---|---|---|
| 이번 주 시작 | `START_OF_WEEK` | 월요일 |
| 이번 주 끝 | `END_OF_WEEK` | 일요일 |
| 이번 달 시작 | `START_OF_MONTH` | 1일 |
| 이번 달 끝 | `END_OF_MONTH` | 말일 |
| 올해 시작 | `START_OF_YEAR` | 1월 1일 |
| 올해 끝 | `END_OF_YEAR` | 12월 31일 |

시작/끝 대상: 이번 주, 다음 주, 저번 주, 이번 달, 다음 달, 저번 달, 올해, 내년, 작년

### 9. 기간 표현 (Date Range)

| 자연어 예시 | 타입 코드 | 설명 |
|---|---|---|
| 9월 10일부터 9월 20일까지 | `BETWEEN` | 명시적 기간 (시작일~종료일) |
| 어제부터 내일까지 | `BETWEEN` | 상대일 기반 기간 |

"부터 ~까지" 패턴 내의 각 날짜 표현은 재귀적으로 파싱됩니다.

### 10. 복합 표현 (Compound)

| 자연어 예시 | 타입 코드 | 설명 |
|---|---|---|
| 다음 달 15일 | `COMPOUND` | 상대월 + 일 |
| 내년 3월 15일 | `COMPOUND` | 상대연도 + 월 + 일 |
| 내년 3월 둘째 주 월요일 | `COMPOUND` | 상대연도 + 월 + 주차 + 요일 |

## 사용 방법

### 기본 사용 (현재 날짜 기준)

```java
NaturalDateParser parser = new NaturalDateParser();
DateParseResult result = parser.parse("내일");

if (result.isSingleDate()) {
    LocalDate date = result.getStartDate();
    System.out.println(date); // 2026-09-16
} else if (result.isRange()) {
    LocalDate start = result.getStartDate();
    LocalDate end = result.getEndDate();
    System.out.println(start + " ~ " + end);
}

System.out.println(result.getType()); // TOMORROW
```

### 기준일 지정

```java
LocalDate base = LocalDate.of(2026, 9, 15);
NaturalDateParser parser = new NaturalDateParser(base);

DateParseResult result = parser.parse("9월 셋째 주 화요일");
System.out.println(result.getStartDate()); // 2026-09-15
```

### 기간 표현

```java
DateParseResult result = parser.parse("9월 10일부터 9월 20일까지");
System.out.println(result.getStartDate()); // 2026-09-10
System.out.println(result.getEndDate());   // 2026-09-20
System.out.println(result.isRange());      // true
```

### 타입 확인

```java
DateParseResult result = parser.parse("이번 주 금요일");
System.out.println(result.getType());           // WEEKDAY_THIS
System.out.println(result.getMatchedKeywords()); // ["이번 주 금요일"]
System.out.println(result.getOriginalText());    // 이번 주 금요일
```

### 매칭 실패

```java
DateParseResult result = parser.parse("안녕하세요");
System.out.println(result.getType());       // null
System.out.println(result.getStartDate());  // null
```

## 내부 동작

### 파싱 우선순위

파서는 다음 순서로 패턴 매칭을 시도합니다. 먼저 매칭된 패턴이 결과로 반환됩니다.

1. 기간 표현 (BETWEEN) — "부터 ~까지" 패턴
2. 복합 표현 (COMPOUND) — 연도+월+주차+요일, 상대월+일
3. 특수 날짜 (START/END) — 주/월/연도 시작/끝
4. 주차 + 요일 (NTH_WEEKDAY) — N번째 주 요일
5. 요일 기반 (WEEKDAY_RELATIVE) — 이번/다음/저번 주 요일
6. 명시적 날짜 (EXPLICIT) — 연월일, 월일, 연월, 월
7. 상대일 (RELATIVE_DAY) — 오늘, 내일, N일 전/후
8. 상대주 (RELATIVE_WEEK) — 이번/다음/저번 주, N주 전/후
9. 상대월 (RELATIVE_MONTH) — 이번/다음/저번 달, N달 전/후
10. 상대연도 (RELATIVE_YEAR) — 올해, 내년, N년 전/후

### 정규화

입력 텍스트는 파싱 전에 다음 정규화를 거칩니다:

- 공백 축소 (연속 공백 → 단일 공백)
- "이번주" → "이번 주"
- "다음주" → "다음 주"
- "저번주" → "저번 주"
- "지난주" → "지난 주"
- "이번달" → "이번 달"
- "다음달" → "다음 달"
- "저번달" → "저번 달"
- "지난달" → "지난 달"

### 주 시작 요일

주의 시작은 **월요일**, 주의 끝은 **일요일**로 정의합니다.
(`java.time.temporal.TemporalAdjusters` 기반)

### 한국어 숫자 변환

주차 표현에서 한국어 서수를 숫자로 변환합니다:

| 한국어 | 숫자 |
|---|---|
| 첫째, 첫 | 1 |
| 둘째, 둘 | 2 |
| 셋째, 셋 | 3 |
| 넷째, 넷 | 4 |
| 다섯째, 다섯 | 5 |
| 여섯째, 여섯 | 6 |
| 일곱째, 일곱 | 7 |
| 여덟째, 여덟 | 8 |
| 아홉째, 아홉 | 9 |
| 열째, 열 | 10 |
| 1째~10째 | 1~10 |

## 테스트

### 실행 방법

```bash
cd /home/jhban/holylife/holylife
./mvnw test -Dtest=NaturalDateParserTest
```

### 테스트 기준일

모든 테스트는 **2026-09-15 (화요일)**을 기준일로 사용합니다.

### 테스트 결과

```
Tests run: 53, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

53개 테스트 케이스가 모든 카테고리를 커버합니다:

- 상대일: 9개 (오늘, 금일, 내일, 모레, 글피, 어제, 그제, 3일 전, 5일 후)
- 상대주: 5개 (이번/다음/저번 주, 2주 전, 3주 후)
- 상대월: 5개 (이번/다음/저번 달, 2달 전, 3달 후)
- 상대연도: 5개 (올해, 내년, 작년, 1년 전, 2년 후)
- 요일 기반: 4개 (이번 주 화/금, 다음 주 수, 저번 주 금)
- 주차+요일: 5개 (9월 셋째 주 화, 9월 둘째 주 일, 이번 달 첫째 주 월, 다음 달 둘째 주 월, 9월 마지막 주 금)
- 명시적: 4개 (YMD, MD, YM, Month)
- 특수: 6개 (주/월/연도 시작/끝)
- 기간: 2개 (명시적 기간, 상대일 기반 기간)
- 복합: 3개 (다음 달 15일, 내년 3월 15일, 내년 3월 둘째 주 월요일)
- 예외: 4개 (매칭 불가, 빈 문자열, null, 정규화)
- 기타: 1개 (toString)

## 확장 가이드

### 새 표현 추가 방법

1. `DateExpressionType`에 새 타입 코드 추가
2. `NaturalDateParser`에 새 `try*` 메서드 구현
3. `parse()` 메서드에 새 `try*` 호출 추가 (우선순위 고려)
4. `NaturalDateParserTest`에 테스트 케이스 추가
5. `./mvnw test -Dtest=NaturalDateParserTest` 실행

### Spring Boot API 연동

REST API로 노출하려면 Controller를 추가합니다:

```java
@RestController
@RequestMapping("/api/date")
public class DateCalculatorController {

    @GetMapping
    public DateParseResult calculate(@RequestParam String text) {
        NaturalDateParser parser = new NaturalDateParser();
        return parser.parse(text);
    }
}
```

호출 예: `GET /api/date?text=내일`

## 파일 위치

```
src/main/java/org/shaloman/pj/nlp/date/
├── DateExpressionType.java
├── DateParseResult.java
└── NaturalDateParser.java

src/test/java/org/shaloman/pj/nlp/date/
└── NaturalDateParserTest.java
```