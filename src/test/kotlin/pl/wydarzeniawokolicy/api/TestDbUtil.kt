package pl.wydarzeniawokolicy.api

import jakarta.persistence.EntityManager
import jakarta.persistence.Table
import org.springframework.transaction.annotation.Transactional

open class TestDbUtil(private val entityManager: EntityManager) {
    private var tablesToClean: List<String> = entityManager.metamodel.entities
        .filter { entityType -> entityType.javaType.getAnnotation(Table::class.java) != null }
        .map { entityType -> entityType.javaType.getAnnotation(Table::class.java) }
        .map {
            it.name
        }

    @Transactional
    open fun clean() {
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate()
        tablesToClean.forEach {
            entityManager.createNativeQuery("TRUNCATE TABLE " + it).executeUpdate()
        }
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate()
    }

    fun em(): EntityManager {
        return entityManager
    }

}
