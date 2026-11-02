package com.collecthub.collecthub.security;

import com.collecthub.collecthub.domain.Rol;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // static: evita una dependencia circular con EmpresaAuthenticationProvider
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            EmpresaAuthenticationProvider authenticationProvider) throws Exception {
        http
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth
                // Públicas
                .requestMatchers("/", "/index", "/login", "/403", "/error",
                        "/tienda", "/tienda/*/catalogo", "/tienda/*/login", "/tienda/*/registro",
                        "/errores/**", "/fav/**", "/js/**", "/css/**", "/webjars/**").permitAll()
                // Por rol (siguen la tabla "ruta" del script SQL)
                .requestMatchers("/plataforma/**").hasRole(Rol.ADMIN_PLATAFORMA)
                .requestMatchers("/empresa/**", "/usuario/**").hasRole(Rol.ADMIN_EMPRESA)
                .requestMatchers("/categoria/**", "/inventario/**", "/tienda/*/producto/**")
                .hasAnyRole(Rol.ADMIN_EMPRESA, Rol.INVENTARIO)
                .requestMatchers("/pedido/**").hasAnyRole(Rol.ADMIN_EMPRESA, Rol.VENTAS)
                .requestMatchers("/carrito/**").hasRole(Rol.CLIENTE)
                // Todo lo demás exige haber iniciado sesión
                .anyRequest().authenticated())
                .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("correo")
                .passwordParameter("contrasena")
                .authenticationDetailsSource(new EmpresaAuthenticationDetailsSource())
                .successHandler(exitoLogin())
                .failureHandler(falloLogin())
                .permitAll())
                .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll())
                .exceptionHandling(ex -> ex.accessDeniedPage("/403"));
        return http.build();
    }

    // Después de entrar, cada rol llega a su pantalla principal
    private AuthenticationSuccessHandler exitoLogin() {
        return (request, response, authentication) ->
                response.sendRedirect(request.getContextPath() + destinoSegunRol(authentication));
    }

        private String destinoSegunRol(Authentication authentication) {
        UsuarioSesion sesion = (UsuarioSesion) authentication.getPrincipal();
        if (sesion.tieneRol(Rol.ADMIN_PLATAFORMA)) {
            return "/plataforma/empresa/nueva";
        }
        if (sesion.tieneRol(Rol.ADMIN_EMPRESA)) {
            return "/empresa/perfil";
        }
        // TODO: cuando se integre el catálogo (rama HU-07/08/09), enviar a
        // "/tienda/" + sesion.getIdentificadorEmpresa() + "/producto/listado" (inventario)
        // o "/catalogo" de la tienda (ventas, consulta y clientes)
        return "/";
    }

    // Si falla, vuelve a la pantalla de login de la MISMA tienda 
    private AuthenticationFailureHandler falloLogin() {
        return (request, response, exception) -> {
            String tienda = request.getParameter("tienda");
            String ruta = "/login?error";
            if (tienda != null && tienda.matches("^[a-z0-9-]+$")) {
                ruta = "/tienda/" + tienda + "/login?error";
            }
            response.sendRedirect(request.getContextPath() + ruta);
        };
    }
}