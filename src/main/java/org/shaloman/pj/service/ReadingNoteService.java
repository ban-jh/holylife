package org.shaloman.pj.service;

import org.shaloman.pj.domain.ReadingNote;
import org.shaloman.pj.mapper.ReadingNoteMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 독서 노트 서비스.
 * @Transactional로 트랜잭션을 관리한다.
 */
@Service
@Transactional
public class ReadingNoteService {

    private final ReadingNoteMapper readingNoteMapper;

    public ReadingNoteService(ReadingNoteMapper readingNoteMapper) {
        this.readingNoteMapper = readingNoteMapper;
    }

    /**
     * 전체 독서 노트 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public List<ReadingNote> getAllReadingNotes() {
        return readingNoteMapper.findAll();
    }

    /**
     * 독서 노트 ID로 조회 (읽기 전용)
     */
    @Transactional(readOnly = true)
    public ReadingNote getReadingNoteById(Long readingId) {
        ReadingNote readingNote = readingNoteMapper.findById(readingId);
        if (readingNote == null) {
            throw new IllegalArgumentException("독서 노트를 찾을 수 없습니다: " + readingId);
        }
        return readingNote;
    }

    /**
     * 독서 노트 등록
     * request 맵: bookTitle, date, author, translator, toc, coverUrl, summary, impression
     * userId는 프론트엔드에서 전송하지 않으므로 기본값 1(관리자) 사용.
     */
    public ReadingNote createReadingNote(Map<String, Object> request) {
        ReadingNote readingNote = new ReadingNote();
        readingNote.setUserId(1L);
        applyRequestFields(readingNote, request);
        readingNoteMapper.insert(readingNote);
        return readingNoteMapper.findById(readingNote.getReadingId());
    }

    /**
     * 독서 노트 수정
     */
    public ReadingNote updateReadingNote(Long readingId, Map<String, Object> request) {
        ReadingNote existing = readingNoteMapper.findById(readingId);
        if (existing == null) {
            throw new IllegalArgumentException("독서 노트를 찾을 수 없습니다: " + readingId);
        }
        applyRequestFields(existing, request);
        readingNoteMapper.update(existing);
        return readingNoteMapper.findById(readingId);
    }

    /**
     * 독서 노트 삭제
     */
    public void deleteReadingNote(Long readingId) {
        ReadingNote existing = readingNoteMapper.findById(readingId);
        if (existing == null) {
            throw new IllegalArgumentException("독서 노트를 찾을 수 없습니다: " + readingId);
        }
        readingNoteMapper.deleteById(readingId);
    }

    // ── 변환 헬퍼 ──

    /**
     * Map의 필드를 ReadingNote 도메인에 적용한다.
     * 프론트엔드 필드 매핑:
     *   bookTitle  -> bookTitle
     *   date       -> readDate (LocalDate.parse)
     *   coverUrl   -> coverImage
     */
    private void applyRequestFields(ReadingNote readingNote, Map<String, Object> request) {
        if (request == null) return;
        Object bookTitle = request.get("bookTitle");
        if (bookTitle != null) readingNote.setBookTitle(bookTitle.toString());
        Object date = request.get("date");
        if (date != null) {
            readingNote.setReadDate(LocalDate.parse(date.toString()));
        }
        Object author = request.get("author");
        if (author != null) readingNote.setAuthor(author.toString());
        Object translator = request.get("translator");
        if (translator != null) readingNote.setTranslator(translator.toString());
        Object toc = request.get("toc");
        if (toc != null) readingNote.setToc(toc.toString());
        Object coverUrl = request.get("coverUrl");
        if (coverUrl != null) readingNote.setCoverImage(coverUrl.toString());
        Object summary = request.get("summary");
        if (summary != null) readingNote.setSummary(summary.toString());
        Object impression = request.get("impression");
        if (impression != null) readingNote.setImpression(impression.toString());
    }
}