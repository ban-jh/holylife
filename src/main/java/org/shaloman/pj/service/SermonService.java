package org.shaloman.pj.service;

import org.shaloman.pj.domain.Sermon;
import org.shaloman.pj.mapper.SermonMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

/**
 * 설교 서비스.
 * @Transactional로 트랜잭션을 관리한다.
 */
@Service
@Transactional
public class SermonService {

    private final SermonMapper sermonMapper;

    public SermonService(SermonMapper sermonMapper) {
        this.sermonMapper = sermonMapper;
    }

    /**
     * 전체 설교 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public java.util.List<Sermon> getAllSermons() {
        return sermonMapper.findAll();
    }

    /**
     * 설교 ID로 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public Sermon getSermonById(Long sermonId) {
        Sermon sermon = sermonMapper.findById(sermonId);
        if (sermon == null) {
            throw new IllegalArgumentException("설교를 찾을 수 없습니다: " + sermonId);
        }
        return sermon;
    }

    /**
     * 설교 등록
     * request 맵: title, sermonDate, preacher, bibleRef, content
     * userId는 프론트엔드에서 전송하지 않으므로 기본값 1(관리자) 사용.
     */
    public Sermon createSermon(Map<String, Object> request) {
        Sermon sermon = new Sermon();
        sermon.setUserId(1L);
        sermon.setStatus("DRAFT");
        applyRequestFields(sermon, request);
        sermonMapper.insert(sermon);
        return sermonMapper.findById(sermon.getSermonId());
    }

    /**
     * 설교 수정
     */
    public Sermon updateSermon(Long sermonId, Map<String, Object> request) {
        Sermon existing = sermonMapper.findById(sermonId);
        if (existing == null) {
            throw new IllegalArgumentException("설교를 찾을 수 없습니다: " + sermonId);
        }
        applyRequestFields(existing, request);
        sermonMapper.update(existing);
        return sermonMapper.findById(sermonId);
    }

    /**
     * 설교 삭제
     */
    public void deleteSermon(Long sermonId) {
        Sermon existing = sermonMapper.findById(sermonId);
        if (existing == null) {
            throw new IllegalArgumentException("설교를 찾을 수 없습니다: " + sermonId);
        }
        sermonMapper.deleteById(sermonId);
    }

    // ── 변환 헬퍼 ──

    /**
     * Map의 필드를 Sermon 도메인에 적용한다.
     * sermonDate 문자열은 LocalDate로 변환.
     */
    private void applyRequestFields(Sermon sermon, Map<String, Object> request) {
        if (request == null) return;
        Object title = request.get("title");
        if (title != null) sermon.setTitle(title.toString());
        Object sermonDate = request.get("sermonDate");
        if (sermonDate != null) {
            sermon.setSermonDate(LocalDate.parse(sermonDate.toString()));
        }
        Object preacher = request.get("preacher");
        if (preacher != null) sermon.setPreacher(preacher.toString());
        Object bibleRef = request.get("bibleRef");
        if (bibleRef != null) sermon.setBibleRef(bibleRef.toString());
        Object content = request.get("content");
        if (content != null) sermon.setContent(content.toString());
    }
}