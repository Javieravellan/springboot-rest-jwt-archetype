#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import com.github.cloudyrock.spring.v5.EnableMongock;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Log4j2
@EnableMongock
public class ${mainClass} {

	@Autowired
	Environment env;

	public static void main(String[] args) {
		SpringApplication.run(${mainClass}.class, args);
	}

	@PostConstruct
	void showProfilesActive() {
		log.warn("Run app on profile: {}", (Object) env.getActiveProfiles());
	}
}
