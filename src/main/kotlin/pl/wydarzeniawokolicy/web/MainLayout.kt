package pl.wydarzeniawokolicy.web

import com.vaadin.flow.component.Key
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.RouterLink
import com.vaadin.flow.theme.lumo.LumoUtility
import org.springframework.beans.factory.annotation.Value
import pl.wydarzeniawokolicy.web.security.SecurityService
import pl.wydarzeniawokolicy.web.views.DashboardView
import pl.wydarzeniawokolicy.web.views.EventView

class MainLayout(
    @Value("\${spring.application.name}") val appName: String,
    private val securityService: SecurityService
) :
    AppLayout() {

    init {
        createHeader()
        createDrawer()
    }

    private fun createHeader() {
        val logo = H1(appName)
        logo.addClassNames(
            LumoUtility.FontSize.LARGE,
            LumoUtility.Margin.MEDIUM
        )

        val header = HorizontalLayout(DrawerToggle(), logo)
        header.defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
        header.expand(logo)
        header.setWidthFull()
        header.addClassNames(
            LumoUtility.Padding.Vertical.NONE,
            LumoUtility.Padding.Horizontal.MEDIUM
        )
        addToNavbar(header)

        securityService.getAuthenticatedUser()?.let {
            val u: String = it.user.name
            header.add(Button("Log out $u") { _ -> securityService.logout() })
        } ?: run {
            val login = Button("Login")
            login.addClickListener {
                login.ui.ifPresent {
                    it.navigate("login")
                }
            }
            login.addClickShortcut(Key.ENTER)
            header.add(login)
        }

    }

    private fun createDrawer() {
        addToDrawer(
            VerticalLayout(
                RouterLink("Events", EventView::class.java),
                RouterLink("Dashboard", DashboardView::class.java)
            )
        )
    }
}