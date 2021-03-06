package web_demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private DataSource dataSource;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource);
	}

	@Value("${frontend.endpoint}")
	private String frontendEndpoint;

	@Bean
	public CorsConfigurationSource corsConfigurationSource(){
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedOrigin("http://localhost:8080");
		configuration.addAllowedOrigin("http://localhost:8081");
		configuration.addAllowedOrigin(frontendEndpoint);
		configuration.addAllowedMethod("*");
		configuration.addAllowedHeader("*");
		configuration.addExposedHeader("Authorization");
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.cors()
				.and()
				.csrf().disable()
				.authorizeRequests()
				.antMatchers("/healthCheck").permitAll()
				.antMatchers("/user/add").permitAll()
				.antMatchers("/user/isNameUsed").permitAll()
				.antMatchers("/isSessionValid").permitAll()
                .anyRequest().authenticated()
			.and()
			.formLogin()
				.loginProcessingUrl("/authenticateUser")
				.successForwardUrl("/loginSuccess")
				.failureForwardUrl("/loginFailed")
				.permitAll()
			.and()
			.oauth2Login()
				.successHandler(new Oauth2LoginSuccessHandler())
				.permitAll()
			.and()
			.logout()
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID")
				.clearAuthentication(true)
				.logoutSuccessHandler((new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)))
				.permitAll();

		http.oauth2Login()
				.userInfoEndpoint()
				.customUserType(CustomOAuth2User.class, "github")
				.customUserType(CustomOAuth2User.class, "google");

//			.and()
//				.exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint());
//			.exceptionHandling().accessDeniedPage("/accessDenied");
	}
}






