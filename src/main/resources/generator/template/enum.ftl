package ${packageName}.constant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ${enumClassName} {

	TYPE1(1l);

	@Getter
	private Long id;
	
	public static ${enumClassName} of(Long id) {

		return Arrays.stream(values()).filter(o -> o.getId().equals(id)).findFirst().orElse(null);
	}
}

