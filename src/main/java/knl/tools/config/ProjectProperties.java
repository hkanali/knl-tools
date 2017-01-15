
package knl.tools.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import lombok.Getter;

@Configuration
@ConfigurationProperties(prefix = "project")
@Getter
public class ProjectProperties {
	
	@Autowired
	private Environment environment;
	
	public List<String> getProfiles() {
		
		List<String> profiles = new ArrayList<>();
		for (String profile : this.environment.getActiveProfiles()) {
			
			profiles.add(profile);
		}
		
		return profiles;
	}
}
