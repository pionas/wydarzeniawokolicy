package pl.wydarzeniawokolicy.domain.categories.api

import org.springframework.data.domain.Pageable

class CategoryFilter(var searchPhrase: String? = null, var pageable: Pageable? = null)