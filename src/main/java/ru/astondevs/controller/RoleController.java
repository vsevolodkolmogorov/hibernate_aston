package ru.astondevs.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.astondevs.dto.RoleDto;
import ru.astondevs.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<RoleDto> getAllRole() {
        return roleService.findAll();
    }

    @GetMapping("/{id}")
    public RoleDto getRole(@PathVariable Long id) {
        return roleService.findById(id);
    }

    @PostMapping
    public ResponseEntity<RoleDto> createRole(@RequestBody RoleDto roleDto) {
        RoleDto saved = roleService.save(roleDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
