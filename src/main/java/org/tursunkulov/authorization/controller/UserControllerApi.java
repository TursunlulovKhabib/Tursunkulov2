package org.tursunkulov.authorization.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.tursunkulov.authorization.entity.User;

import java.util.List;
import java.util.Optional;

@Tag(name = "User Api", description = "Управление пользователем")
public interface UserControllerApi {

    @Operation(
            summary = "Получение всех пользователей",
            description = "Возвращает список всех пользователей")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Список успешно получен")})
    ResponseEntity<Optional<List<User>>> info();

    @Operation(
            summary = "Получение имени пользователя",
            description = "Возвращает имя пользователя по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Имя пользователя успешно получено")
    })
    ResponseEntity<Optional<User>> getUser(@PathVariable int id);

    @Operation(summary = "Удаление пользователя по id", description = "Удаляет пользователя по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Имя пользователя успешно удалено")
    })
    ResponseEntity<Void> deleteById(@PathVariable int id);

    @Operation(
            summary = "Удаление пользователя по username",
            description = "Удаляет пользователя по username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Имя пользователя успешно удалено")
    })
    ResponseEntity<Void> deleteByUsername(@PathVariable String username);

    @Operation(
            summary = "Смена номера телефона пользователя",
            description = "Обновляет номер телефона пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Номера телефона успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    ResponseEntity<Optional<User>> patchPhoneNumber(
            @Parameter(description = "Идентификатор пользователя", required = true) @PathVariable int id,
            @Parameter(description = "Номер телефона", required = true) @PathVariable String phoneNumber);

    @Operation(
            summary = "Смена электронной почты пользователя",
            description = "Обновляет электронной почты пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    ResponseEntity<Optional<User>> patchEmail(
            @Parameter(description = "Идентификатор пользователя", required = true) @PathVariable int id,
            @Parameter(description = "Email", required = true) @PathVariable String email);

    @Operation(summary = "Обновление пользователя", description = "Полное обновление пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    ResponseEntity<Void> updateUserById(
            @Parameter(description = "Идентификатор пользователя", required = true) @PathVariable int id,
            @Parameter(description = "Обновленные данные пользователя", required = true)
            @RequestBody
            @Valid
            User user);

    @Operation(summary = "Обновление пользователя", description = "Полное обновление пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    ResponseEntity<Void> updateUserByUsername(
            @Parameter(description = "Имя пользователя пользователя", required = true) @PathVariable
            String username,
            @Parameter(description = "Обновленные данные пользователя", required = true)
            @RequestBody
            @Valid
            User user);
}
