package ${packageName}.data.repository;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ${packageName}.data.entity.${tableUpperCamel};

@Repository
public interface ${tableUpperCamel}Repository extends JpaRepository<${tableUpperCamel}, ${entity.id.embeddedId?then(tableUpperCamel + '.Id', 'Long')}> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Transactional(readOnly = false)
	Optional<${tableUpperCamel}> findForUpdateById(${entity.id.embeddedId?then(tableUpperCamel + '.Id', 'Long')} id);
}
