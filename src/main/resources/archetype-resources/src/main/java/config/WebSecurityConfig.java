#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.config;

import ${package}.domain.enums.ERole;
import ${package}.filter.AuthTokenFilter;
import ${package}.security.AuthEntryPointJwt;
import ${package}.security.JwtTokenUtils;
import ${package}.services.JwtTokenUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    @Autowired
    JwtTokenUserDetailsService userDetailsService;

    @Bean
    public AuthTokenFilter authTokenFilter(JwtTokenUtils jwtTokenUtils) {
        return new AuthTokenFilter(jwtTokenUtils, userDetailsService);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfig) throws Exception {
        return authenticationConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(JwtTokenUtils jwtTokenUtils, AuthEntryPointJwt authEntryPointJwt, HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(req -> req.requestMatchers("/api/auth/**", "/api/management/health")
                        .permitAll().requestMatchers("/api/management").hasAuthority(ERole.ROLE_ADMIN.name())
                    .anyRequest().authenticated())
            .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exHandle -> exHandle.authenticationEntryPoint(authEntryPointJwt));
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authTokenFilter(jwtTokenUtils), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
