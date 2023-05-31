package pl.wydarzeniawokolicy.web

import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.RouterLink
import com.vaadin.flow.theme.lumo.LumoUtility
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import pl.wydarzeniawokolicy.web.security.SecurityService
import pl.wydarzeniawokolicy.web.views.DashboardView
import pl.wydarzeniawokolicy.web.views.EventView
import pl.wydarzeniawokolicy.web.views.LoginView
import pl.wydarzeniawokolicy.web.views.WelcomeView
import com.vaadin.flow.component.Component as VaadinComponent


@Component
@Scope("prototype")
class MainLayout(
    private val appConfiguration: AppConfiguration,
    private val securityService: SecurityService
) :
    AppLayout() {

    init {
        createHeader()
        createDrawer()
    }

    private fun createHeader() {
        val logo = H1(appConfiguration.appName)
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
    }

    private fun createDrawer() {
        addToDrawer(
            VerticalLayout(
                createRouterLink(VaadinIcon.HOME, "Welcome", WelcomeView::class.java),
                createRouterLink(VaadinIcon.DASHBOARD, "Dashboard", DashboardView::class.java),
                createRouterLink(VaadinIcon.CALENDAR_USER, "Events", EventView::class.java),
                getUserLink(),
            )
        )
        isDrawerOpened = false
    }

    private fun createRouterLink(
        viewIcon: VaadinIcon,
        viewName: String,
        navigationTarget: Class<out VaadinComponent>?
    ): RouterLink {
        val icon: Icon = viewIcon.create()
        icon.style["box-sizing"] = "border-box"
        icon.style["margin-inline-end"] = "var(--lumo-space-m)"
        icon.style["margin-inline-start"] = "var(--lumo-space-xs)"
        icon.style["padding"] = "var(--lumo-space-xs)"

        val link = RouterLink()
        link.add(icon, Span(viewName))
        navigationTarget?.let { link.setRoute(it) }
        link.tabIndex = -1
        return link
    }

    private fun getUserLink(): VaadinComponent {
        return securityService.getAuthenticatedUser()?.let {
            val u: String = it.user.name
            Button("Log out $u") { _ -> securityService.logout() }
        } ?: run {
            createRouterLink(VaadinIcon.SIGN_IN, "Login", LoginView::class.java)
        }
    }
}