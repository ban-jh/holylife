package org.shaloman.pj.nlp.date;

import java.time.LocalDate;
import java.util.List;

/**
 * 자연어 날짜 표현 파싱 결과.
 * 파서가 분석한 타입, 계산된 날짜(또는 기간), 원본 텍스트를 보관한다.
 */
public class DateParseResult {

    private final DateExpressionType type;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String originalText;
    private final List<String> matchedKeywords;

    public DateParseResult(DateExpressionType type, LocalDate startDate, LocalDate endDate,
                           String originalText, List<String> matchedKeywords) {
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.originalText = originalText;
        this.matchedKeywords = matchedKeywords;
    }

    /** 단일 날짜 결과용 편의 생성자 */
    public DateParseResult(DateExpressionType type, LocalDate date,
                           String originalText, List<String> matchedKeywords) {
        this(type, date, null, originalText, matchedKeywords);
    }

    public DateExpressionType getType() {
        return type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getOriginalText() {
        return originalText;
    }

    public List<String> getMatchedKeywords() {
        return matchedKeywords;
    }

    /** 기간 표현인지 여부 */
    public boolean isRange() {
        return endDate != null;
    }

    /** 단일 날짜인지 여부 */
    public boolean isSingleDate() {
        return startDate != null && endDate == null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DateParseResult{");
        sb.append("type=").append(type);
        sb.append(", text='").append(originalText).append("'");
        if (isRange()) {
            sb.append(", range=").append(startDate).append(" ~ ").append(endDate);
        } else if (startDate != null) {
            sb.append(", date=").append(startDate);
        }
        sb.append(", keywords=").append(matchedKeywords);
        sb.append("}");
        return sb.toString();
    }
}