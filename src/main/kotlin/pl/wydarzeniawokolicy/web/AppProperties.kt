package pl.wydarzeniawokolicy.web

import org.springframework.boot.context.properties.ConfigurationProperties

data class AppProperties(
    val appName: String,
    val category: CategoryProperties
)

@ConfigurationProperties(prefix = "category")
data class CategoryProperties(val title: String)