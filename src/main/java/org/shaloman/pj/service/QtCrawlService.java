package org.shaloman.pj.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.shaloman.pj.domain.QtDaily;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Duranno 큐티 크롤링 서비스.
 * EUC-KR 인코딩으로 HTML을 가져와 JSoup로 파싱한다.
 */
@Slf4j
@Service
public class QtCrawlService {

    @Value("${holylife.qt.crawl-url}")
    private String crawlUrl;

    @Value("${holylife.qt.sum-crawl-url}")
    private String sumCrawlUrl;

    private static final Charset EUC_KR = Charset.forName("EUC-KR");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 지정한 날짜의 큐티를 크롤링하여 QtDaily 객체로 반환한다.
     *
     * @param qtDate 큐티 날짜
     * @return QtDaily 객체, 실패 시 null
     */
    public QtDaily crawlQt(LocalDate qtDate) {
        String dateStr = qtDate.format(DATE_FMT);
        String url = crawlUrl + "?qtDate=" + URLEncoder.encode(dateStr, EUC_KR);
        log.info("QT 크롤링 시작: date={}, url={}", dateStr, url);

        try {
            // EUC-KR 인코딩으로 HTML fetch (User-Agent 설정 필요)
            URL urlObj = new URL(url);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) urlObj.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(30000);
            String html;
            try (var is = conn.getInputStream()) {
                byte[] bytes = is.readAllBytes();
                html = new String(bytes, EUC_KR);
            }

            Document doc = Jsoup.parse(html);
            doc.setBaseUri(url);

            QtDaily qt = new QtDaily();
            qt.setQtDate(qtDate);
            qt.setSource("duranno");

            // 성경 본문 참조: h1 > span
            Element refSpan = doc.selectFirst("h1 > span");
            if (refSpan != null) {
                qt.setBibleRef(cleanText(refSpan.text()));
            }

            // 제목: h1 > em
            Element titleEm = doc.selectFirst("h1 > em");
            if (titleEm != null) {
                qt.setTitle(cleanText(titleEm.text()));
            }

            // 오늘의 찬송: p.title "오늘의 찬송" 다음 p 태그
            Elements titleParagraphs = doc.select("p.title");
            String hymnInfo = null;
            String hymnText = null;
            for (Element pTitle : titleParagraphs) {
                String titleText = cleanText(pTitle.text());
                if (titleText.contains("오늘의 찬송") || titleText.contains("찬송")) {
                    Element next = pTitle.nextElementSibling();
                    if (next != null && "p".equalsIgnoreCase(next.tagName())) {
                        String fullText = cleanText(next.text());
                        // 찬송 정보(장수)와 가사 분리: 첫 줄은 정보, 나머지는 가사
                        String[] lines = fullText.split("\\n");
                        if (lines.length > 0) {
                            hymnInfo = lines[0].trim();
                            StringBuilder sb = new StringBuilder();
                            for (int i = 1; i < lines.length; i++) {
                                if (sb.length() > 0) sb.append("\n");
                                sb.append(lines[i].trim());
                            }
                            hymnText = sb.length() > 0 ? sb.toString() : fullText;
                        } else {
                            hymnInfo = fullText;
                        }
                    }
                }
                if (titleText.contains("오늘의 기도")) {
                    Element next = pTitle.nextElementSibling();
                    if (next != null && "p".equalsIgnoreCase(next.tagName())) {
                        qt.setPrayer(cleanText(next.text()));
                    }
                }
            }
            qt.setHymnInfo(hymnInfo);
            qt.setHymnText(hymnText);

            // 성경 본문: div.bible 내부
            Element bibleDiv = doc.selectFirst("div.bible");
            if (bibleDiv != null) {
                // 섹션 제목: div.bible 내부 p.title
                Elements sectionTitles = bibleDiv.select("p.title");
                if (!sectionTitles.isEmpty()) {
                    qt.setBibleSectionTitle(cleanText(sectionTitles.first().text()));
                }

                // 구절: div.bible 내부 td
                Elements verses = bibleDiv.select("td");
                StringBuilder bibleText = new StringBuilder();
                for (Element verse : verses) {
                    String verseText = cleanText(verse.text());
                    if (!verseText.isEmpty()) {
                        if (bibleText.length() > 0) bibleText.append("\n");
                        bibleText.append(verseText);
                    }
                }
                if (bibleText.length() > 0) {
                    qt.setBibleText(bibleText.toString());
                }
            }

            log.info("QT 크롤링 완료: date={}, ref={}, title={}", dateStr, qt.getBibleRef(), qt.getTitle());
            return qt;

        } catch (IOException e) {
            log.error("QT 크롤링 실패 (IO): date={}, error={}", dateStr, e.getMessage(), e);
            return null;
        } catch (Exception e) {
            log.error("QT 크롤링 실패: date={}, error={}", dateStr, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 생명의삶(SUM) 큐티 크롤링.
     * https://sum.su.or.kr:8888/bible/today (UTF-8, HTTPS self-signed cert)
     * mainView_2: 본문 (date, title, bibleRef, hymnInfo, bibleText)
     * mainView_3: 해설 (commentary/prayer text)
     *
     * @param qtDate 큐티 날짜
     * @return QtDaily 객체 (source="sum"), 실패 시 null
     */
    public QtDaily crawlSum(LocalDate qtDate) {
        String dateStr = qtDate.format(DATE_FMT);
        log.info("SUM QT 크롤링 시작: date={}, url={}", dateStr, sumCrawlUrl);

        try {
            // SSL self-signed cert 우회: 모든 인증서를 신뢰하는 TrustManager 설정
            javax.net.ssl.TrustManager[] trustAll = new javax.net.ssl.TrustManager[]{
                new javax.net.ssl.X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                }
            };
            javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("TLS");
            sc.init(null, trustAll, new java.security.SecureRandom());
            javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            java.net.URL urlObj = new java.net.URL(sumCrawlUrl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) urlObj.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(30000);
            String html;
            try (var is = conn.getInputStream()) {
                byte[] bytes = is.readAllBytes();
                html = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
            }

            Document doc = Jsoup.parse(html);
            doc.setBaseUri(sumCrawlUrl);

            QtDaily qt = new QtDaily();
            qt.setQtDate(qtDate);
            qt.setSource("sum");

            // --- mainView_2: 본문 ---
            Element mainView2 = doc.selectFirst("#mainView_2");
            if (mainView2 != null) {
                // 제목: div.bible_text (id="bible_text")
                Element titleEl = mainView2.selectFirst("#bible_text");
                if (titleEl != null) {
                    qt.setTitle(cleanText(titleEl.text()));
                }

                // 본문 참조 + 찬송가: div.bibleinfo_box
                // "본문 : 사사기(Judges) 16:15 - 16:31 찬송가 214장"
                Element infoBox = mainView2.selectFirst("#bibleinfo_box");
                if (infoBox != null) {
                    String infoText = cleanText(infoBox.text());
                    // 본문 참조 추출: "본문 :" 이후, "찬송가" 이전
                    if (infoText.contains("본문 :")) {
                        String refPart = infoText.substring(infoText.indexOf("본문 :") + 4).trim();
                        if (refPart.contains("찬송가")) {
                            refPart = refPart.substring(0, refPart.indexOf("찬송가")).trim();
                        }
                        qt.setBibleRef(refPart);
                    }
                    // 찬송가 정보 추출
                    if (infoText.contains("찬송가")) {
                        String hymnPart = infoText.substring(infoText.indexOf("찬송가")).trim();
                        qt.setHymnInfo(hymnPart);
                    }
                }

                // 성경 본문: ul.body_list > li > div.num + div.info
                Elements verses = mainView2.select("ul.body_list > li");
                StringBuilder bibleText = new StringBuilder();
                for (Element verse : verses) {
                    Element numEl = verse.selectFirst("div.num");
                    Element infoEl = verse.selectFirst("div.info");
                    String num = numEl != null ? cleanText(numEl.text()) : "";
                    String text = infoEl != null ? cleanText(infoEl.text()) : "";
                    if (!text.isEmpty()) {
                        if (bibleText.length() > 0) bibleText.append("\n");
                        bibleText.append(num).append(" ").append(text);
                    }
                }
                if (bibleText.length() > 0) {
                    qt.setBibleText(bibleText.toString());
                }
            }

            // --- mainView_3: 해설 (commentary + prayer) ---
            Element mainView3 = doc.selectFirst("#mainView_3");
            if (mainView3 != null) {
                StringBuilder commentary = new StringBuilder();

                // div.b_text: 서론
                Element bText = mainView3.selectFirst("div.b_text");
                if (bText != null) {
                    String intro = cleanText(bText.text());
                    if (!intro.isEmpty()) {
                        commentary.append(intro);
                    }
                }

                // div.g_text (소제목) + div.text (내용) 페어
                Elements headings = mainView3.select("div.g_text");
                for (Element heading : headings) {
                    String headingText = cleanText(heading.text());
                    Element textEl = heading.nextElementSibling();
                    if (textEl != null && "div".equalsIgnoreCase(textEl.tagName())
                            && "text".equals(textEl.className())) {
                        String content = cleanText(textEl.text());
                        if (!headingText.isEmpty()) {
                            if (commentary.length() > 0) commentary.append("\n\n");
                            commentary.append("[").append(headingText).append("]\n");
                            commentary.append(content);
                        } else if (!content.isEmpty()) {
                            if (commentary.length() > 0) commentary.append("\n\n");
                            commentary.append(content);
                        }
                    }
                }

                if (commentary.length() > 0) {
                    qt.setPrayer(commentary.toString());
                }
            }

            log.info("SUM QT 크롤링 완료: date={}, ref={}, title={}", dateStr, qt.getBibleRef(), qt.getTitle());
            return qt;

        } catch (Exception e) {
            log.error("SUM QT 크롤링 실패: date={}, error={}", dateStr, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 텍스트 정제: &nbsp; 처리, HTML 태그 제거, 공백 정리.
     */
    private String cleanText(String text) {
        if (text == null) return null;
        // &nbsp; 및 유니코드 non-breaking space 처리
        text = text.replace("\u00a0", " ").replace("&nbsp;", " ").replace("&amp;", "&");
        // 연속 공백 축약
        text = text.replaceAll("[ \\t]+", " ");
        // 앞뒤 trim
        return text.trim();
    }
}