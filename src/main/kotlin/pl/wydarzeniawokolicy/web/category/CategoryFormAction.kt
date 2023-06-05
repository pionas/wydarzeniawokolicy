package pl.wydarzeniawokolicy.web.category

interface CategoryFormAction {

    fun save(name: String, slug: String?)
    fun close()
    fun delete()

}
