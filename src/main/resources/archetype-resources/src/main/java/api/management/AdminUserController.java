#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.api.management;

import ${package}.api.request.SignUpRequest;
import ${package}.api.response.MessageResponse;
import ${package}.services.UserService;
import ${package}.util.PaginationUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/management/users")
public class AdminUserController {
    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignUpRequest userDTO) {
        userService.registerUser(userDTO);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @GetMapping
    public ResponseEntity<Iterable<SignUpRequest>> getAllUsers(Pageable pageable) {
        var users = userService.getAllUsers(pageable);
        var headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), users);
        return ResponseEntity.ok()
                .headers(headers)
                .body(users.getContent());
    }

    @GetMapping("/{username}")
    public ResponseEntity<SignUpRequest> getUserByUsername(@PathVariable String username) {
        var user = userService.getUserByUsername(username);
        return ResponseEntity.ok()
                .body(user);
    }

    @PutMapping("/{username}")
    @ResponseStatus(code = HttpStatus.OK)
    public void updateUserByUsername(@PathVariable String username, @RequestBody SignUpRequest updatedUserDTO) {
        userService.updateUserByUsername(username, updatedUserDTO);
    }

    @DeleteMapping("/{username}")
    @ResponseStatus(code = HttpStatus.OK)
    public void deleteUserByUsername(@PathVariable String username) {
        userService.deleteUserByUsername(username);
    }
}
