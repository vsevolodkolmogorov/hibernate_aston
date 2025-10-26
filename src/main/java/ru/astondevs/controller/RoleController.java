package ru.astondevs.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.astondevs.dto.RoleDto;
import ru.astondevs.service.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/role")
@Tag(name = "Role", description = "API для работы с ролями пользователей")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "Получить все роли", description = "Возвращает список всех ролей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список ролей успешно получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoleDto.class)))
    })
    @GetMapping
    public CollectionModel<EntityModel<RoleDto>> getAllRole() {
        List<RoleDto> roleDtoList = roleService.findAll();

        List<EntityModel<RoleDto>> rolesWithLinks = roleDtoList.stream()
                .map(roleDto -> EntityModel.of(roleDto,
                        linkTo(methodOn(RoleController.class).getRole(roleDto.getId())).withSelfRel(),
                        linkTo(methodOn(RoleController.class).deleteRole(roleDto.getId())).withRel("delete")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(rolesWithLinks,
                linkTo(methodOn(RoleController.class).getAllRole()).withSelfRel());
    }

    @Operation(summary = "Получить роль по ID", description = "Возвращает роль по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Роль успешно получена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoleDto.class))),
            @ApiResponse(responseCode = "404", description = "Роль с таким ID не найдена")
    })
    @GetMapping("/{id}")
    public EntityModel<RoleDto> getRole(@PathVariable Long id) {
        RoleDto roleDto = roleService.findById(id);

        EntityModel<RoleDto> resource = EntityModel.of(roleDto);
        resource.add(linkTo(methodOn(RoleController.class).getRole(id)).withSelfRel());
        resource.add(linkTo(methodOn(RoleController.class).deleteRole(id)).withRel("delete"));

        return resource;
    }

    @Operation(summary = "Создать новую роль", description = "Создает новую роль с заданными параметрами")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Роль успешно создана",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoleDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    })
    @PostMapping
    public ResponseEntity<RoleDto> createRole(@RequestBody RoleDto roleDto) {
        RoleDto saved = roleService.save(roleDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }

    @Operation(summary = "Удалить роль", description = "Удаляет роль по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Роль успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Роль с таким ID не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
