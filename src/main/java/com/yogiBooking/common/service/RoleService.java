package com.yogiBooking.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yogiBooking.common.dto.RoleDTO;
import com.yogiBooking.common.entity.Role;
import com.yogiBooking.common.mapper.RoleMapper;
import com.yogiBooking.common.repository.RoleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Transactional
    public RoleDTO createRole(RoleDTO roleDTO) {
        Role role = roleMapper.toEntity(roleDTO);

        Role savedRole = roleRepository.save(role);
        return roleMapper.toDto(savedRole);
    }

    @Transactional
    public List<RoleDTO> getRoles() {
        return roleRepository.findAll().stream().map(roleMapper::toDto).toList();
    }

    @Transactional
    public RoleDTO updateRole(Long id, RoleDTO roleDTO) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());
        role.setStatus(roleDTO.getStatus());
        Role updatedRole = roleRepository.save(role);
        return roleMapper.toDto(updatedRole);
    }

    @Transactional
    public boolean deleteRole(Long id) {
        roleRepository.deleteById(id);
        return true;
    }

    public Page<RoleDTO> searchRoles(String name, String description, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Role> rolesPage = roleRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(name, description, pageable);
        return rolesPage.map(roleMapper::toDto);
    }
}
