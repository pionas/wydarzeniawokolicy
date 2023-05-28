package pl.wydarzeniawokolicy

import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.server.PWA
import com.vaadin.flow.theme.Theme
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@Theme(value = "theme")
@PWA(name = "WydarzeniaWOkolicy",
    shortName = "WWO",
    iconPath = "img/icons/logo.png")
class WydarzeniaWOkolicyApplication: AppShellConfigurator

fun main(args: Array<String>) {
    runApplication<WydarzeniaWOkolicyApplication>(*args)
}
