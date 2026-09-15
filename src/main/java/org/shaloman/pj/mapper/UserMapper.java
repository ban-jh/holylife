package org.shaloman.pj.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.shaloman.pj.domain.User;

import java.util.List;

/**
 * 사용자 MyBatis Mapper 인터페이스.
 * XML 매퍼 파일: src/main/resources/mapper/UserMapper.xml
 */
@Mapper
public interface UserMapper {

    /** 전체 사용자 조회 */
    List<User> findAll();

    /** ID로 사용자 조회 */
    User findById(@Param("userId") Long userId);

    /** 이메일로 사용자 조회 */
    User findByEmail(@Param("email") String email);

    /** 사용자 등록 */
    int insert(User user);

    /** 사용자 수정 */
    int update(User user);

    /** 사용자 삭제 */
    int deleteById(@Param("userId") Long userId);

    /** 총 사용자 수 */
    int count();
}