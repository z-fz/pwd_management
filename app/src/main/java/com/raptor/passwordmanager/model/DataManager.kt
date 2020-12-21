package com.raptor.passwordmanager.model

class WebsiteEntry() {
    var website_:String = ""
    var username_:String = ""
    var password_:String = ""
    var comments_:String = ""

    constructor(website:String, username:String, password:String, comments:String) : this() {
        website_ = website
        username_ = username
        password_ = password
        comments_ = comments
    }
}

typealias ListWebsiteEntries = MutableList<WebsiteEntry>

class DataManager {

    private var all_datas : ListWebsiteEntries = MutableList<WebsiteEntry>(0, { i -> WebsiteEntry() })

    init {
        all_datas.add(WebsiteEntry("google.com", "user1", "pass1", "no comments"))
        all_datas.add(WebsiteEntry("yahoo.com", "user2", "passwords2", "no comments"))
        all_datas.add(WebsiteEntry("google2.com", "user3", "pass3", "no comments"))
        println("init all data")
    }

    private var latest_search_results : ListWebsiteEntries = MutableList<WebsiteEntry>(0, { i -> WebsiteEntry() })


    fun UpdateData() {

    }

    fun GetSearchResult():ListWebsiteEntries {
        return latest_search_results
    }

    fun DoSearch(query: String) {
        var ret : ListWebsiteEntries = MutableList<WebsiteEntry>(0, { i -> WebsiteEntry() })
        for (data in all_datas) {
            if (data.website_.contains(query)) {
                ret.add(data)
                println("***adding $data")
            }
        }
        latest_search_results = ret
    }

}