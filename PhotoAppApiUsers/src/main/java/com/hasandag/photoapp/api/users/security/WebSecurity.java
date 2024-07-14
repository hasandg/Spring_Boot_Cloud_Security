package com.hasandag.photoapp.api.users.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.hasandag.photoapp.api.users.service.UsersService;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EnableMethodSecurity(prePostEnabled = true)
@Configuration
@EnableWebSecurity
public class WebSecurity {

    private Environment environment;
    private UsersService usersService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private List<String> ipAddresses;


    public WebSecurity(Environment environment, UsersService usersService, BCryptPasswordEncoder bCryptPasswordEncoder, @Value("#{'${allowed.ip.addresses}'.split(',')}") List<String> ipAddresses) {
        this.environment = environment;
        this.usersService = usersService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.ipAddresses = ipAddresses;
    }


    @Bean
    public RequestMatcher ipAddressMatcher() {
        return new OrRequestMatcher(ipAddresses.stream()
                .map(IpAddressMatcher::new).toArray(RequestMatcher[]::new));
    }

/*    @Bean
    public RequestMatcher ipAddressMatcher(List<String> ipAddresses) {
        List<IpAddressMatcher> whitelist = ipAddresses.stream().map(IpAddressMatcher::new).collect(Collectors.toList());
        return new OrRequestMatcher((RequestMatcher) whitelist);
    }*/

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {

        // Configure AuthenticationManagerBuilder
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.userDetailsService(usersService).passwordEncoder(bCryptPasswordEncoder);

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        // Create AuthenticationFilter
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(usersService, environment, authenticationManager);
        authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));

        //http.cors(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);


        http.authorizeHttpRequests(authz -> authz.requestMatchers(
                new AntPathRequestMatcher("/users/**"))
                        /*.access(
                        new WebExpressionAuthorizationManager("@ipAddressMatcher.matches(request)"))*/
                        //.access(new WebExpressionAuthorizationManager("hasAnyIpAddress('"+environment.getProperty("allowed.ip.addresses")+"')"))
                        //.access(new WebExpressionAuthorizationManager("hasIpAddress('"+environment.getProperty("allowed.ip.addresses")+"')"))
                        .access((authentication, context) -> new AuthorizationDecision(ipAddressMatcher().matches(context.getRequest())))
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll())
                .addFilter(new AuthorizationFilter(authenticationManager, environment))
                .addFilter(authenticationFilter).authenticationManager(authenticationManager)
                .sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.sameOrigin()));
        return http.build();

    }
}
