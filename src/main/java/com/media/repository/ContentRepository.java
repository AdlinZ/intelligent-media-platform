package com.media.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.media.entity.Content;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {
    // 添加正确的查询方法
    @Query("SELECT c FROM Content c WHERE c.isPublished = true AND c.title LIKE %:keyword%")
    Page<Content> findByTitleContainingAndPublished(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT c FROM Content c WHERE c.isPublished = true")
    Page<Content> findAllPublished(Pageable pageable);
    
    // 管理端需要的查询方法
    Long countByIsPublishedTrue();
    Long countByIsPublishedFalse();
    Long countByCreatedBy(Long createdBy);
    List<Content> findTop10ByOrderByCreatedAtDesc();
    
    @Query("SELECT COALESCE(SUM(c.viewCount), 0) FROM Content c WHERE DATE(c.createdAt) = DATE(:today)")
    Long sumViewCountToday(@Param("today") LocalDateTime today);
    
    void deleteByCreatedBy(Long createdBy);
}