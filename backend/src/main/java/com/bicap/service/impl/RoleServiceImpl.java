package com.bicap.service.impl;

import com.bicap.common.enums.RoleName;
import com.bicap.entity.Role;
import com.bicap.exception.ResourceNotFoundException;
import com.bicap.repository.RoleRepository;
import com.bicap.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role getRole(RoleName roleName) {

        return roleRepository.findByRoleName(roleName.name())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role " + roleName + " not found."
                        ));
    }

}