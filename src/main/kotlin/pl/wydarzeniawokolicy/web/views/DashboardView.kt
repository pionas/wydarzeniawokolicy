package pl.wydarzeniawokolicy.web.views

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import jakarta.annotation.security.PermitAll
import pl.wydarzeniawokolicy.web.MainLayout

@PermitAll
@Route(value = "dashboard", layout = MainLayout::class)
class DashboardView : VerticalLayout(), HasDynamicTitle {

    override fun getPageTitle(): String {
        return "Custom Title :D"
    }

}