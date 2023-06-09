package pl.wydarzeniawokolicy

import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.server.PWA
import com.vaadin.flow.theme.Theme
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Profile
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import pl.wydarzeniawokolicy.domain.categories.api.CategoryService
import pl.wydarzeniawokolicy.domain.categories.api.NewCategory
import pl.wydarzeniawokolicy.domain.events.api.EventService
import pl.wydarzeniawokolicy.domain.events.api.NewEvent
import pl.wydarzeniawokolicy.domain.events.api.Sender
import pl.wydarzeniawokolicy.domain.roles.api.NewRole
import pl.wydarzeniawokolicy.domain.roles.api.RoleService
import pl.wydarzeniawokolicy.domain.users.api.UserService
import pl.wydarzeniawokolicy.domain.users.api.UserSignUp

@SpringBootApplication
@Theme(value = "theme")
@PWA(
    name = "WydarzeniaWOkolicy",
    shortName = "WWO",
    iconPath = "img/icons/logo.png"
)
@ConfigurationPropertiesScan
class WydarzeniaWOkolicyApplication : AppShellConfigurator

fun main(args: Array<String>) {
    runApplication<WydarzeniaWOkolicyApplication>(*args)
}

@Component
@Profile("!it")
class Initializer(val roleService: RoleService, val userService: UserService, val eventService: EventService, val categoryService: CategoryService) :
    ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        if (roleService.findAll().isEmpty()) {
            roleService.create(NewRole("Admin", "admin"))
            roleService.create(NewRole("Category management", "category-management"))
            roleService.create(NewRole("Category create", "category-create"))
            roleService.create(NewRole("Category delete", "category-delete"))
        }
        if (userService.findAll().isEmpty()) {
            userService.create(
                UserSignUp("john.doe", "john.doe@example.com", "validPassword", "validPassword", listOf("admin"))
            )
        }
        if (eventService.findAll().isEmpty()) {
            eventService.create(
                NewEvent(
                    name = "Custom Event",
                    sender = Sender("John Doe", "john.doe@example.com", null)
                )
            )
        }
        if (categoryService.findAll().isEmpty()) {
            for (i in 1..150) {
                categoryService.create(NewCategory(name = "Category $i", slug = null))
            }
        }
    }
}