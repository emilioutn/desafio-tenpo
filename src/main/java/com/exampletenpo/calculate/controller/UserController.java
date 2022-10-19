package com.exampletenpo.calculate.controller;

import com.exampletenpo.calculate.dto.account.UserDto;
import com.exampletenpo.calculate.service.CommonService;
import com.exampletenpo.calculate.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final CommonService commonService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto dto) {
        UserDto userDto = userService.createUser(dto);
        commonService.registerResultToContext(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto dto) {
        UserDto userDto = userService.updateUser(dto);
        commonService.registerResultToContext(userDto);
        return ResponseEntity.ok(userDto);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping(value = "/username/{username}")
    public ResponseEntity deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok().build();
    }

}
