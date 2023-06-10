package pl.wydarzeniawokolicy.domain.roles

import org.springframework.stereotype.Service
import pl.wydarzeniawokolicy.domain.roles.api.NewRole
import pl.wydarzeniawokolicy.domain.roles.api.Role
import pl.wydarzeniawokolicy.domain.roles.api.RoleException
import pl.wydarzeniawokolicy.domain.roles.api.RoleService
import pl.wydarzeniawokolicy.domain.shared.DateTimeUtils
import pl.wydarzeniawokolicy.domain.shared.SlugHelper
import pl.wydarzeniawokolicy.domain.shared.StringUtils
import java.util.*

@Service
class RoleServiceImpl(
    val roleRepository: RoleRepository,
    val dateTimeUtils: DateTimeUtils,
    val stringUtils: StringUtils
) :
    RoleService {

    override fun findAll(): List<Role> = roleRepository.findAll()

    override fun create(newRole: NewRole): Role {
        verifySlug(null, newRole.slug)
        val role = Role(
            name = newRole.name.uppercase(),
            slug = getSlug(newRole),
            createdAt = dateTimeUtils.getLocalDateTimeNow()
        )
        return roleRepository.create(role)
    }

    override fun delete(slug: String) {
        roleRepository.delete(slug)
    }

    override fun update(currentSlug: String, newRole: NewRole): Role {
        val role = roleRepository.findBySlug(currentSlug) ?: throw RoleException.slugNotFound(currentSlug)
        verifySlug(role.slug, newRole.slug)
        role.update(newRole.name.uppercase(), getSlug(newRole), dateTimeUtils.getLocalDateTimeNow())
        return roleRepository.create(role)
    }

    override fun findBySlug(slug: String): Role {
        return roleRepository.findBySlug(slug) ?: throw RoleException.slugNotFound(slug)
    }

    private fun verifySlug(currentSlug: String?, slug: String?) {
        if (slug == null) {
            return
        }
        val roleBySlug = roleRepository.findBySlug(slug) ?: return
        if (currentSlug == null) {
            throw RoleException.slugExist(slug)
        }
        if (!Objects.equals(roleBySlug.slug, currentSlug)) {
            throw RoleException.slugExist(currentSlug)
        }
    }

    private fun getSlug(newRole: NewRole): String {
        return newRole.slug ?: SlugHelper.generateSlug(roleRepository, stringUtils.slug(newRole.name, "_"))
    }

}