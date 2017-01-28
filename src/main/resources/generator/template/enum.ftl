package ${packageName}.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ${enumClassName} {

	TYPE1(1l);

	@Getter
	private Long id;
	
	public static ${enumClassName} of(Long id) {
		
		for (${enumClassName} type : values()) {
			
			if (type.getId().equals(id)) {
			
				return type;
			}
		}
		
		throw new IllegalStateException(String.format("Illegal ${enumClassName} id. id=%s", id));
	}
}

