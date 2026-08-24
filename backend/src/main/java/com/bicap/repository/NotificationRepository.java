package com.bicap.repository;

import com.bicap.entity.Account;
import com.bicap.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    /**
     * Danh sách notification của tài khoản
     */
    List<Notification> findByAccountOrderByCreatedAtDesc(Account account);

    /**
     * Chi tiết notification của tài khoản
     */
    Optional<Notification> findByNotificationIdAndAccount(
            Long notificationId,
            Account account
    );

    /**
     * Đánh dấu tất cả notification chưa đọc
     */
   @Modifying
@Query("""
       UPDATE Notification n
       SET n.isRead = true
       WHERE n.account = :account
       AND n.isRead = false
       """)
void markAllAsRead(@Param("account") Account account);

    /**
     * Đếm số notification chưa đọc
     * (phục vụ badge nếu sau này cần)
     */
    long countByAccountAndIsReadFalse(Account account);

}