package br.com.datainfo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import br.com.datainfo.security.AuthoritiesConstants;
import br.com.datainfo.security.jwt.JWTConfigurer;
import br.com.datainfo.security.jwt.TokenProvider;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private final TokenProvider tokenProvider;

	private final SecurityProblemSupport problemSupport;

	public SecurityConfiguration(TokenProvider tokenProvider, SecurityProblemSupport problemSupport) {
		this.tokenProvider = tokenProvider;
		this.problemSupport = problemSupport;
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**").antMatchers("/swagger-ui/index.html").antMatchers("/**");
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().exceptionHandling().authenticationEntryPoint(problemSupport)
				.accessDeniedHandler(problemSupport).and().headers().frameOptions().disable().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
				// .antMatchers("/api/**").authenticated()
				.antMatchers("/api/**").permitAll().antMatchers("/management/health").permitAll()
				.antMatchers("/management/info").permitAll().antMatchers("/management/**")
				.hasAuthority(AuthoritiesConstants.ADMIN).and().apply(securityConfigurerAdapter());

	}

	private JWTConfigurer securityConfigurerAdapter() {
		return new JWTConfigurer(tokenProvider);
	}
}
