#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.services;

import ${package}.api.request.SignUpRequest;
import ${package}.domain.Role;
import ${package}.domain.User;
import ${package}.domain.enums.ERole;
import ${package}.repository.RoleRepository;
import ${package}.repository.UserRepository;
import ${package}.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder encoder,
                       RoleRepository roleRepository,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }

    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public void registerUser(SignUpRequest request) {
        request.setPassword(encoder.encode(request.getPassword()));
        User user = userMapper.toEntity(request);

        user.setAuthorities(this.mapAuthorities(request.getAuthorities()));

        userRepository.save(user);
    }

    private Set<Role> mapAuthorities(Set<ERole> eRoles) {
        final Set<Role> roles = new HashSet<>();
        if (eRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            eRoles.forEach(role -> {
                Role currRole = roleRepository.findByName(role).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(currRole);
            });
        }

        return roles;
    }
}
