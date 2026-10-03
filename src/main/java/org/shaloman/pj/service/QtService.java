package org.shaloman.pj.service;

import org.shaloman.pj.domain.Qt;
import org.shaloman.pj.mapper.QtMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 큐티 서비스.
 * @Transactional로 트랜잭션을 관리한다.
 */
@Service
@Transactional
public class QtService {

    private final QtMapper qtMapper;

    public QtService(QtMapper qtMapper) {
        this.qtMapper = qtMapper;
    }

    /**
     * 전체 큐티 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public List<Qt> getAllQts() {
        return qtMapper.findAll();
    }

    /**
     * 큐티 ID로 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public Qt getQtById(Long qtId) {
        Qt qt = qtMapper.findById(qtId);
        if (qt == null) {
            throw new IllegalArgumentException("큐티를 찾을 수 없습니다: " + qtId);
        }
        return qt;
    }

    /**
     * 큐티 등록
     * request 맵: title, qtDate, bibleRef, bibleText, content, visibility, userId
     * userId는 요청에 없으면 기본값 1(관리자), visibility는 기본값 PUBLIC.
     */
    public Qt createQt(Map<String, Object> request) {
        Qt qt = new Qt();
        // userId: 요청에서 가져오거나 기본값 1
        Object userIdObj = request.get("userId");
        if (userIdObj != null) {
            qt.setUserId(Long.valueOf(userIdObj.toString()));
        } else {
            qt.setUserId(1L);
        }
        // visibility: 요청에서 가져오거나 기본값 PUBLIC
        Object visibilityObj = request.get("visibility");
        if (visibilityObj != null && !visibilityObj.toString().isEmpty()) {
            qt.setVisibility(visibilityObj.toString());
        } else {
            qt.setVisibility("PUBLIC");
        }
        applyRequestFields(qt, request);
        qtMapper.insert(qt);
        return qtMapper.findById(qt.getQtId());
    }

    /**
     * 큐티 수정
     */
    public Qt updateQt(Long qtId, Map<String, Object> request) {
        Qt existing = qtMapper.findById(qtId);
        if (existing == null) {
            throw new IllegalArgumentException("큐티를 찾을 수 없습니다: " + qtId);
        }
        applyRequestFields(existing, request);
        qtMapper.update(existing);
        return qtMapper.findById(qtId);
    }

    /**
     * 큐티 삭제
     */
    public void deleteQt(Long qtId) {
        Qt existing = qtMapper.findById(qtId);
        if (existing == null) {
            throw new IllegalArgumentException("큐티를 찾을 수 없습니다: " + qtId);
        }
        qtMapper.deleteById(qtId);
    }

    // ── 변환 헬퍼 ──

    /**
     * Map의 필드를 Qt 도메인에 적용한다.
     * qtDate 문자열은 LocalDate로 변환.
     */
    private void applyRequestFields(Qt qt, Map<String, Object> request) {
        if (request == null) return;
        Object title = request.get("title");
        if (title != null) qt.setTitle(title.toString());
        Object qtDate = request.get("qtDate");
        if (qtDate != null) {
            qt.setQtDate(LocalDate.parse(qtDate.toString()));
        }
        Object bibleRef = request.get("bibleRef");
        if (bibleRef != null) qt.setBibleRef(bibleRef.toString());
        Object bibleText = request.get("bibleText");
        if (bibleText != null) qt.setBibleText(bibleText.toString());
        Object content = request.get("content");
        if (content != null) qt.setContent(content.toString());
        Object visibility = request.get("visibility");
        if (visibility != null && !visibility.toString().isEmpty()) {
            qt.setVisibility(visibility.toString());
        }
        Object userId = request.get("userId");
        if (userId != null) {
            qt.setUserId(Long.valueOf(userId.toString()));
        }
    }
}