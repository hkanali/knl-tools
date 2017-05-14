
package knl.tools.config;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.validation.annotation.Validated;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
@Configuration
@ConfigurationProperties(prefix = "project")
@Validated
public class ProjectProperties {
	
	@NonNull
	private Environment environment;
	
	public List<String> getProfiles() {
		
		List<String> profiles = new ArrayList<>();
		for (String profile : this.environment.getActiveProfiles()) {
			
			profiles.add(profile);
		}
		
		return profiles;
	}
	
	@NotNull
	private String packageName;
}
