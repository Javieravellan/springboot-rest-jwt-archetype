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
import ${package}.exception.NotFoundException;
import ${package}.exception.UsernameAlreadyExistsException;
import ${package}.exception.BadRequestException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
        if (Boolean.TRUE.equals(this.existsByUsername(request.getUsername()))) {
            throw new UsernameAlreadyExistsException("El nombre de usuario '"+ request.getUsername()+"' ya está ocupado");
        }

        request.setPassword(encoder.encode(request.getPassword()));
        User user = userMapper.toEntity(request);

        user.setAuthorities(this.mapAuthorities(request.getAuthorities()));
        userRepository.save(user);
    }

    public Page<SignUpRequest> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> {
                    var authorities = user.getAuthorities()
                            .stream().map(Role::getName)
                            .collect(Collectors.toSet());

                    var userDTO = userMapper.toDto(user);
                    userDTO.setAuthorities(authorities);

                    return userDTO;
                });
    }

    public SignUpRequest getUserByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new BadRequestException("Username no puede ser null o vacío.");
        }

        return userRepository.findByUsername(username)
                .map(user -> {
                    var userDTO = userMapper.toDto(user);
                    userDTO.setAuthorities(mapEntityAuthorities(user.getAuthorities()));
                    return userDTO;
                })
                .orElseThrow(() -> new NotFoundException(String.format("Username '%s' no encontrado.", username)));
    }

    public void updateUserByUsername(String username, SignUpRequest updateUserDTO) {
        var userFound = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(String.format("Username '%s' no encontrado.", username)));
        updateUserDTO.setUsername(username);
        // no change password
        updateUserDTO.setPassword(null);
        userMapper.partialUpdate(userFound, updateUserDTO);
        userFound.setAuthorities(this.mapAuthorities(updateUserDTO.getAuthorities()));

        log.debug("Datos de usuario actualizados: {}", userFound);
        userRepository.save(userFound);
    }

    public void deleteUserByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new BadRequestException("Username no puede ser null o vacío.");
        }
        log.warn("Eliminando usuario: {}", username);
        userRepository.deleteByUsername(username);
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

    private Set<ERole> mapEntityAuthorities(Set<Role> roles) {
        return roles.stream().map(Role::getName)
                .collect(Collectors.toSet());
    }
}
