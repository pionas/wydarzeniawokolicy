package pl.wydarzeniawokolicy

import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.server.PWA
import com.vaadin.flow.theme.Theme
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Profile
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import pl.wydarzeniawokolicy.domain.users.api.UserService
import pl.wydarzeniawokolicy.domain.users.api.UserSignUp

@SpringBootApplication
@Theme(value = "theme")
@PWA(
    name = "WydarzeniaWOkolicy",
    shortName = "WWO",
    iconPath = "img/icons/logo.png"
)
class WydarzeniaWOkolicyApplication : AppShellConfigurator

fun main(args: Array<String>) {
    runApplication<WydarzeniaWOkolicyApplication>(*args)
}

@Component
@Profile("!it")
class Initializer(val userService: UserService) : ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        if (userService.findAll().isEmpty()) {
            userService.create(UserSignUp("john.doe", "john.doe@example.com", "validPassword", "validPassword"))
        }
    }
}