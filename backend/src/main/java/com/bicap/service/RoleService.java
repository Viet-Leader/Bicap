package com.bicap.service;

import com.bicap.common.enums.RoleName;
import com.bicap.entity.Role;

public interface RoleService {

    Role getRole(RoleName roleName);

}
