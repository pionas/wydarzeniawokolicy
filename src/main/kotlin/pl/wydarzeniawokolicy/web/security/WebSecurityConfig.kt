package pl.wydarzeniawokolicy.web.security

import com.vaadin.flow.spring.security.VaadinWebSecurity
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import pl.wydarzeniawokolicy.web.views.LoginView


@Configuration(proxyBeanMethods = false)
class WebSecurityConfig : VaadinWebSecurity() {

    override fun configure(http: HttpSecurity) {
        super.configure(http)
        setLoginView(http, LoginView::class.java)
    }


}