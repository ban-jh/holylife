package org.shaloman.pj.content.pbs.service;

import org.shaloman.pj.content.pbs.domain.Pbs;
import org.shaloman.pj.content.pbs.mapper.PbsMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * PBS 서비스.
 * @Transactional로 트랜잭션을 관리한다.
 */
@Service
@Transactional
public class PbsService {

    private final PbsMapper pbsMapper;

    public PbsService(PbsMapper pbsMapper) {
        this.pbsMapper = pbsMapper;
    }

    /**
     * 전체 PBS 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public List<Pbs> getAllPbs() {
        return pbsMapper.findAll();
    }

    /**
     * PBS ID로 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public Pbs getPbsById(Long pbsId) {
        Pbs pbs = pbsMapper.findById(pbsId);
        if (pbs == null) {
            throw new IllegalArgumentException("PBS를 찾을 수 없습니다: " + pbsId);
        }
        return pbs;
    }

    /**
     * PBS 등록
     * request 맵: title, date, bookFrom, bookTo, bibleText, content
     * userId는 프론트엔드에서 전송하지 않으므로 기본값 1(관리자) 사용.
     */
    public Pbs createPbs(Map<String, Object> request) {
        Pbs pbs = new Pbs();
        pbs.setUserId(1L);
        pbs.setStatus("DRAFT");
        applyRequestFields(pbs, request);
        pbsMapper.insert(pbs);
        return pbsMapper.findById(pbs.getPbsId());
    }

    /**
     * PBS 수정
     */
    public Pbs updatePbs(Long pbsId, Map<String, Object> request) {
        Pbs existing = pbsMapper.findById(pbsId);
        if (existing == null) {
            throw new IllegalArgumentException("PBS를 찾을 수 없습니다: " + pbsId);
        }
        applyRequestFields(existing, request);
        pbsMapper.update(existing);
        return pbsMapper.findById(pbsId);
    }

    /**
     * PBS 삭제
     */
    public void deletePbs(Long pbsId) {
        Pbs existing = pbsMapper.findById(pbsId);
        if (existing == null) {
            throw new IllegalArgumentException("PBS를 찾을 수 없습니다: " + pbsId);
        }
        pbsMapper.deleteById(pbsId);
    }

    // ── 변환 헬퍼 ──

    /**
     * Map의 필드를 Pbs 도메인에 적용한다.
     * 프론트엔드는 'date' 필드명을 사용하며 pbsDate로 매핑한다.
     * date 문자열은 LocalDate로 변환.
     */
    private void applyRequestFields(Pbs pbs, Map<String, Object> request) {
        if (request == null) return;
        Object title = request.get("title");
        if (title != null) pbs.setTitle(title.toString());
        // 프론트엔드는 'date' 필드명 사용, 'pbsDate'도 허용
        Object dateVal = request.get("date");
        if (dateVal == null) dateVal = request.get("pbsDate");
        if (dateVal != null) {
            pbs.setPbsDate(LocalDate.parse(dateVal.toString()));
        }
        Object bookFrom = request.get("bookFrom");
        if (bookFrom != null) pbs.setBookFrom(bookFrom.toString());
        Object bookTo = request.get("bookTo");
        if (bookTo != null) pbs.setBookTo(bookTo.toString());
        Object bibleText = request.get("bibleText");
        if (bibleText != null) pbs.setBibleText(bibleText.toString());
        Object content = request.get("content");
        if (content != null) pbs.setContent(content.toString());
    }
}