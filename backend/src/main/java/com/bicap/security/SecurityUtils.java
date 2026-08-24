package com.bicap.security;

import com.bicap.entity.Account;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Account getCurrentAccount() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof CustomUserDetails user)) {
            return null;
        }

        return user.getAccount();
    }

    public static Long getCurrentAccountId() {

        Account account = getCurrentAccount();

        return account != null
                ? account.getAccountId()
                : null;
    }

}