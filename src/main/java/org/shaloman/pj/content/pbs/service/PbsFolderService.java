package org.shaloman.pj.content.pbs.service;

import org.shaloman.pj.content.pbs.domain.PbsFolder;
import org.shaloman.pj.content.pbs.mapper.PbsFolderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PBS 폴더 서비스.
 * Confluence 스타일 폴더 트리 관리 기능을 제공한다.
 */
@Service
@Transactional
public class PbsFolderService {

    private final PbsFolderMapper folderMapper;

    public PbsFolderService(PbsFolderMapper folderMapper) {
        this.folderMapper = folderMapper;
    }

    /**
     * 전체 폴더 목록 조회 (평면 구조, 읽기 전용)
     */
    @Transactional(readOnly = true)
    public List<PbsFolder> getAllFolders() {
        return folderMapper.findAll();
    }

    /**
     * 폴더 트리 조회 (각 폴더 + 하위 페이지 + 중첩 children)
     * findTree()로 모든 폴더와 각 폴더의 pages를 가져온 후,
     * parent_id를 기준으로 children을 조립하여 트리를 구성한다.
     */
    @Transactional(readOnly = true)
    public List<PbsFolder> getFolderTree() {
        List<PbsFolder> allFolders = folderMapper.findTree();
        // parent_id가 null인 폴더가 루트
        // children 조립
        Map<Long, List<PbsFolder>> byParent = allFolders.stream()
                .filter(f -> f.getParentId() != null)
                .collect(Collectors.groupingBy(PbsFolder::getParentId));

        for (PbsFolder folder : allFolders) {
            List<PbsFolder> children = byParent.get(folder.getFolderId());
            folder.setChildren(children != null ? children : new ArrayList<>());
        }

        // 루트 폴더들만 반환 (parentId == null)
        return allFolders.stream()
                .filter(f -> f.getParentId() == null)
                .collect(Collectors.toList());
    }

    /**
     * 폴더 생성
     * request 맵: folderName, parentId(optional), sortOrder(optional)
     */
    public PbsFolder createFolder(Map<String, Object> request) {
        PbsFolder folder = new PbsFolder();
        Object folderName = request.get("folderName");
        if (folderName == null) {
            throw new IllegalArgumentException("folderName은 필수입니다.");
        }
        folder.setFolderName(folderName.toString());
        Object parentId = request.get("parentId");
        if (parentId != null) {
            folder.setParentId(Long.valueOf(parentId.toString()));
        }
        Object sortOrder = request.get("sortOrder");
        if (sortOrder != null) {
            folder.setSortOrder(Integer.valueOf(sortOrder.toString()));
        }
        folderMapper.insert(folder);
        return folderMapper.findById(folder.getFolderId());
    }

    /**
     * 폴더 수정
     * request 맵: folderName, parentId, sortOrder (모두 optional)
     */
    public PbsFolder updateFolder(Long folderId, Map<String, Object> request) {
        PbsFolder existing = folderMapper.findById(folderId);
        if (existing == null) {
            throw new IllegalArgumentException("폴더를 찾을 수 없습니다: " + folderId);
        }
        Object folderName = request.get("folderName");
        if (folderName != null) existing.setFolderName(folderName.toString());
        Object parentId = request.get("parentId");
        if (parentId != null) existing.setParentId(Long.valueOf(parentId.toString()));
        Object sortOrder = request.get("sortOrder");
        if (sortOrder != null) existing.setSortOrder(Integer.valueOf(sortOrder.toString()));
        folderMapper.update(existing);
        return folderMapper.findById(folderId);
    }

    /**
     * 폴더 삭제 (하위 폴더/페이지가 있으면 함께 삭제)
     */
    public void deleteFolder(Long folderId) {
        PbsFolder existing = folderMapper.findById(folderId);
        if (existing == null) {
            throw new IllegalArgumentException("폴더를 찾을 수 없습니다: " + folderId);
        }
        // 하위 폴더 재귀 삭제
        deleteFolderRecursive(folderId);
    }

    private void deleteFolderRecursive(Long folderId) {
        List<PbsFolder> children = folderMapper.findByParentId(folderId);
        for (PbsFolder child : children) {
            deleteFolderRecursive(child.getFolderId());
        }
        folderMapper.deleteById(folderId);
    }
}
