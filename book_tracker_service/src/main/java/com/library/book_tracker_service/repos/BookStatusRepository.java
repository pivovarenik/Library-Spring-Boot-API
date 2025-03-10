package com.library.book_tracker_service.repos;

import com.library.book_tracker_service.models.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookStatusRepository extends JpaRepository<BookStatus, Long> {
    List<BookStatus> findAllByStatus(String status);
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE BookStatus b SET b.status = 'DELETED' WHERE b.bookId = :id")
    void markBookAsDeleted(Long id);
}
