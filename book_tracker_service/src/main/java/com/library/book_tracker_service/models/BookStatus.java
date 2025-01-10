package com.library.book_tracker_service.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "book_status")
public class BookStatus {
    @Id
    @JsonProperty("bookId")
    private Long bookId;
    @Column(nullable = false)
    @JsonProperty("status")
    private String status;
    @Column
    @JsonProperty("borrowedAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime borrowedAt;
    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("dueAt")
    private LocalDateTime dueAt;

    public BookStatus(Long bookId, String status, LocalDateTime borrowedAt, LocalDateTime dueAt) {
        this.bookId = bookId;
        this.status = status;
        this.borrowedAt = borrowedAt;
        this.dueAt = dueAt;
    }

    public BookStatus() {
    }

    public static BookStatusBuilder builder() {
        return new BookStatusBuilder();
    }

    public Long getBookId() {
        return this.bookId;
    }

    public String getStatus() {
        return this.status;
    }

    public LocalDateTime getBorrowedAt() {
        return this.borrowedAt;
    }

    public LocalDateTime getDueAt() {
        return this.dueAt;
    }

    @JsonProperty("bookId")
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("borrowedAt")
    public void setBorrowedAt(LocalDateTime borrowedAt) {
        this.borrowedAt = borrowedAt;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("dueAt")
    public void setDueAt(LocalDateTime dueAt) {
        this.dueAt = dueAt;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BookStatus)) return false;
        final BookStatus other = (BookStatus) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$bookId = this.getBookId();
        final Object other$bookId = other.getBookId();
        if (this$bookId == null ? other$bookId != null : !this$bookId.equals(other$bookId)) return false;
        final Object this$status = this.getStatus();
        final Object other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) return false;
        final Object this$borrowedAt = this.getBorrowedAt();
        final Object other$borrowedAt = other.getBorrowedAt();
        if (this$borrowedAt == null ? other$borrowedAt != null : !this$borrowedAt.equals(other$borrowedAt))
            return false;
        final Object this$dueAt = this.getDueAt();
        final Object other$dueAt = other.getDueAt();
        if (this$dueAt == null ? other$dueAt != null : !this$dueAt.equals(other$dueAt)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BookStatus;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $bookId = this.getBookId();
        result = result * PRIME + ($bookId == null ? 43 : $bookId.hashCode());
        final Object $status = this.getStatus();
        result = result * PRIME + ($status == null ? 43 : $status.hashCode());
        final Object $borrowedAt = this.getBorrowedAt();
        result = result * PRIME + ($borrowedAt == null ? 43 : $borrowedAt.hashCode());
        final Object $dueAt = this.getDueAt();
        result = result * PRIME + ($dueAt == null ? 43 : $dueAt.hashCode());
        return result;
    }

    public String toString() {
        return "BookStatus(bookId=" + this.getBookId() + ", status=" + this.getStatus() + ", borrowedAt=" + this.getBorrowedAt() + ", dueAt=" + this.getDueAt() + ")";
    }

    public static class BookStatusBuilder {
        private Long bookId;
        private String status;
        private LocalDateTime borrowedAt;
        private LocalDateTime dueAt;

        BookStatusBuilder() {
        }

        @JsonProperty("bookId")
        public BookStatusBuilder bookId(Long bookId) {
            this.bookId = bookId;
            return this;
        }

        @JsonProperty("status")
        public BookStatusBuilder status(String status) {
            this.status = status;
            return this;
        }

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("borrowedAt")
        public BookStatusBuilder borrowedAt(LocalDateTime borrowedAt) {
            this.borrowedAt = borrowedAt;
            return this;
        }

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("dueAt")
        public BookStatusBuilder dueAt(LocalDateTime dueAt) {
            this.dueAt = dueAt;
            return this;
        }

        public BookStatus build() {
            return new BookStatus(this.bookId, this.status, this.borrowedAt, this.dueAt);
        }

        public String toString() {
            return "BookStatus.BookStatusBuilder(bookId=" + this.bookId + ", status=" + this.status + ", borrowedAt=" + this.borrowedAt + ", dueAt=" + this.dueAt + ")";
        }
    }
}