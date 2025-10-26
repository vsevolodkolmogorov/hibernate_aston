package ru.astondevs.controller;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.astondevs.dto.UserDto;
import ru.astondevs.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "API для работы с пользователями")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class)))
    })
    @GetMapping
    public CollectionModel<EntityModel<UserDto>> getAllUsers() {
        List<UserDto> userDtoList = userService.findAll();

        List<EntityModel<UserDto>> usersWithLinks = userDtoList.stream()
                .map(user -> EntityModel.of(user,
                        linkTo(methodOn(UserController.class).getUser(user.getId())).withSelfRel(),
                        linkTo(methodOn(UserController.class).updateUser(user.getId(), user)).withRel("update"),
                        linkTo(methodOn(UserController.class).deleteUser(user.getId())).withRel("delete"),
                        linkTo(methodOn(RoleController.class).getRole(user.getRole_id())).withRel("role")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(usersWithLinks,
                linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
    }

    @Operation(summary = "Получить пользователя по ID", description = "Возвращает пользователя по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь с таким ID не найден")
    })
    @GetMapping("/{id}")
    public EntityModel<UserDto> getUser(@PathVariable("id") Long id) {
        UserDto user = userService.findById(id);

        EntityModel<UserDto> resource = EntityModel.of(user);
        resource.add(linkTo(methodOn(UserController.class).getUser(id)).withSelfRel());
        resource.add(linkTo(methodOn(UserController.class).updateUser(id, user)).withRel("update"));
        resource.add(linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete"));
        resource.add(linkTo(methodOn(RoleController.class).getRole(user.getRole_id())).withRel("role"));

        return resource;
    }

    @Operation(summary = "Создать нового пользователя", description = "Создает нового пользователя с заданными параметрами")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    })
    @PostMapping
    public EntityModel<UserDto> createUser(@RequestBody UserDto userDto) {
        UserDto saved = userService.save(userDto);
        EntityModel<UserDto> resource = EntityModel.of(saved);
        resource.add(linkTo(methodOn(UserController.class).getUser(saved.getId())).withSelfRel());
        return resource;
    }

    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь успешно удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь с таким ID не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Обновить пользователя", description = "Обновляет данные пользователя по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
            @ApiResponse(responseCode = "404", description = "Пользователь с таким ID не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") Long id, @RequestBody UserDto userDto) {
        UserDto updated = userService.update(id, userDto);
        return ResponseEntity.ok(updated);
    }
}

