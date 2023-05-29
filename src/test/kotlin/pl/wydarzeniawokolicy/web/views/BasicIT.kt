package pl.wydarzeniawokolicy.web.views

import com.github.mvysny.dynatest.DynaNodeGroup
import com.github.mvysny.dynatest.DynaTestDsl
import com.github.mvysny.kaributesting.v10.MockVaadin
import com.github.mvysny.kaributesting.v10.Routes

private val routes = Routes().autoDiscoverViews("pl.wydarzeniawokolicy.web")

@DynaTestDsl
fun DynaNodeGroup.usingApp() {
    beforeEach { MockVaadin.setup(routes) }
    afterEach { MockVaadin.tearDown() }
}