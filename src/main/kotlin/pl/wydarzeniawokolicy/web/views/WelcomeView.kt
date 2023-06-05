package pl.wydarzeniawokolicy.web.views

import com.vaadin.flow.component.Text
import com.vaadin.flow.component.avatar.Avatar
import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.virtuallist.VirtualList
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.dom.ElementFactory
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import pl.wydarzeniawokolicy.domain.events.api.Event
import pl.wydarzeniawokolicy.domain.events.api.EventService
import pl.wydarzeniawokolicy.web.MainLayout
import com.vaadin.flow.component.Component as VaadingComponent


@Route(value = "", layout = MainLayout::class)
@AnonymousAllowed
@Component
@Scope("prototype")
class WelcomeView(eventService: EventService) : Div() {

    private val events: List<Event> = eventService.findAll()

    private val eventCardRenderer: ComponentRenderer<VaadingComponent, Event> = ComponentRenderer { event: Event ->
        val cardLayout = HorizontalLayout()
        cardLayout.isMargin = true
        val avatar = Avatar(
            event.name
        )
        avatar.height = "64px"
        avatar.width = "64px"
        val infoLayout = VerticalLayout()
        infoLayout.isSpacing = false
        infoLayout.isPadding = false
        infoLayout.element.appendChild(
            ElementFactory.createStrong(event.name)
        )
        infoLayout.add(Div(Text(event.description)))
        val contactLayout = VerticalLayout()
        contactLayout.isSpacing = false
        contactLayout.isPadding = false
        contactLayout.add(Div(Text(event.sender.name)))
        contactLayout
            .add(Div(Text(event.address.toString())))
        infoLayout
            .add(Details("Contact information", contactLayout))
        cardLayout.add(avatar, infoLayout)
        cardLayout
    }

    init {
        val list: VirtualList<Event> = VirtualList<Event>()
        list.setItems(events)
        list.setRenderer(eventCardRenderer)
        add(Div(Text("TODO: Implements map")), list)
    }
}