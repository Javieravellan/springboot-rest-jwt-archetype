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
		var app = new SpringApplication(${mainClass}.class);
		${mainClass}.addDefaultProfile(app);
		Environment env = app.run(args).getEnvironment();
	}

	private static void addDefaultProfile(SpringApplication app) {
		Map<String, Object> defProperties = new HashMap<>();
		defProperties.put(SPRING_PROFILE_DEFAULT, "default,dev");
		app.setDefaultProperties(defProperties);
	}

	private static void logProfilesActives(Environment env) {
		log.info("Aplicación en ejecución con los perfiles: {}", (Object) env.getActiveProfiles());
	}
}
