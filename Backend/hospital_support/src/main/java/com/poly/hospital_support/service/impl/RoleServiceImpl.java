package com.poly.hospital_support.service.impl;

import com.poly.hospital_support.dto.RoleDTO;
import com.poly.hospital_support.entity.Role;
import com.poly.hospital_support.repository.RoleRepository;
import com.poly.hospital_support.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò với ID: " + id));
        return convertToDTO(role);
    }

    @Override
    public RoleDTO createRole(RoleDTO roleDTO) {
        if (roleRepository.existsByName(roleDTO.getName())) {
            throw new RuntimeException("Tên vai trò '" + roleDTO.getName() + "' đã tồn tại");
        }
        Role role = Role.builder()
                .name(roleDTO.getName())
                .description(roleDTO.getDescription())
                .build();
        Role savedRole = roleRepository.save(role);
        return convertToDTO(savedRole);
    }

    @Override
    public RoleDTO updateRole(Long id, RoleDTO roleDTO) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò với ID: " + id));
        
        // Kiểm tra xem tên mới có trùng với vai trò khác không
        if (!role.getName().equalsIgnoreCase(roleDTO.getName()) && roleRepository.existsByName(roleDTO.getName())) {
            throw new RuntimeException("Tên vai trò '" + roleDTO.getName() + "' đã tồn tại");
        }

        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());
        
        Role updatedRole = roleRepository.save(role);
        return convertToDTO(updatedRole);
    }

    @Override
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy vai trò với ID: " + id);
        }
        roleRepository.deleteById(id);
    }

    private RoleDTO convertToDTO(Role role) {
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .build();
    }
}
