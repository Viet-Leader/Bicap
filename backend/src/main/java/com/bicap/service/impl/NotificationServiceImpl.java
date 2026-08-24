package com.bicap.service.impl;

import com.bicap.dto.response.notification.NotificationResponse;
import com.bicap.entity.Account;
import com.bicap.entity.Notification;
import com.bicap.exception.ResourceNotFoundException;
import com.bicap.exception.UnauthorizedException;
import com.bicap.mapper.NotificationMapper;
import com.bicap.repository.AccountRepository;
import com.bicap.repository.NotificationRepository;
import com.bicap.security.SecurityUtils;
import com.bicap.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;
    private final NotificationMapper notificationMapper;

    private Account getCurrentAccount() {

        Long accountId = SecurityUtils.getCurrentAccountId();

        if (accountId == null) {
            throw new UnauthorizedException("Please login first.");
        }

        return accountRepository
                .findById(accountId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found."));
    }

    private Notification findMyNotification(Long notificationId) {

        Account account = getCurrentAccount();

        return notificationRepository
                .findByNotificationIdAndAccount(
                        notificationId,
                        account
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Notification not found."
                        ));
    }

    @Override
    public List<NotificationResponse> getMyNotifications() {

        Account account = getCurrentAccount();

        return notificationRepository
                .findByAccountOrderByCreatedAtDesc(account)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Override
    public NotificationResponse getNotification(Long notificationId) {

        Notification notification =
                findMyNotification(notificationId);

        return notificationMapper.toResponse(notification);
    }

    @Override
    public NotificationResponse markAsRead(Long notificationId) {

        Notification notification =
                findMyNotification(notificationId);

        if (!notification.getIsRead()) {

            notification.setIsRead(true);

            notification =
                    notificationRepository.save(notification);
        }

        return notificationMapper.toResponse(notification);
    }

    @Override
    public void markAllAsRead() {

        Account account = getCurrentAccount();

        notificationRepository.markAllAsRead(account);
    }

    @Override
    public void createNotification(
            Account account,
            String title,
            String content
    ) {

        Notification notification = Notification.builder()
                .account(account)
                .title(title)
                .content(content)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }
}