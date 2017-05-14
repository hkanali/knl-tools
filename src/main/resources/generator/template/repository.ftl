package ${packageName}.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ${packageName}.entity.${tableUpperCamel};

@Repository
public interface ${tableUpperCamel}Repository extends JpaRepository<${tableUpperCamel}, ${entity.id.embeddedId?then(tableUpperCamel + '.Id', 'Long')}> {
}