package com.app.myapplication

class RssItem {
    var title = ""
    var link = ""
    var pubDate = ""
    var description = ""
    var category = ""
    var author = ""
    var url = ""
    override fun toString(): String {
        return "RssItem(title='$title', link='$link', pubDate='$pubDate', description='$description', category='$category', author='$author', url='$url')"
    }

}