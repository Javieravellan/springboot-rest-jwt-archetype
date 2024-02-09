#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.api;

import ${package}.api.request.JwtRequest;
import ${package}.api.response.JwtResponse;
import ${package}.domain.UserDetailsImpl;
import ${package}.exception.GenericException;
import ${package}.security.JwtTokenUtils;
import ${package}.services.JwtTokenUserDetailsService;
import ${package}.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
@Log4j2
public class JwtAuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtils jwtTokenUtil;

    @Autowired
    private JwtTokenUserDetailsService userDetailsService;
    @Autowired
    private UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> createAuthenticationToken(@Valid @RequestBody JwtRequest authenticationRequest) throws Exception {
        Authentication auth = this.authenticate(authenticationRequest);

        SecurityContextHolder.getContext().setAuthentication(auth);
        final String token = jwtTokenUtil.generateToken(auth);

        final UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        final List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return ResponseEntity.ok(new JwtResponse(
            userDetails.getId(),
            token,
            authorities,
            userDetails.getUsername()
        ));
    }

    private Authentication authenticate(JwtRequest request) {
        try {
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            ));
        } catch (DisabledException e) {
            throw new GenericException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (BadCredentialsException e) {
            throw new GenericException(HttpStatus.FORBIDDEN, "Usuario o contraseña incorrectos");
        }
    }
}
