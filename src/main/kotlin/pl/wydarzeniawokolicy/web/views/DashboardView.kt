package pl.wydarzeniawokolicy.web.views

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import pl.wydarzeniawokolicy.web.MainLayout

@Route(value = "dashboard", layout = MainLayout::class)
@PermitAll
@Component
@Scope("prototype")
class DashboardView : VerticalLayout(), HasDynamicTitle {

    override fun getPageTitle(): String {
        return "Custom Title :D"
    }

}