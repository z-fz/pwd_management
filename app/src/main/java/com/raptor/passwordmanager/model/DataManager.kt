package com.raptor.passwordmanager.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class WebsiteEntry(
  var website_: String,
  var email_: String,
  var username_: String,
  var password_: String,
  var comments_: String
) {
  var questions_: MutableList<Pair<String, String>> =
    MutableList<Pair<String, String>>(0, { index -> Pair<String, String>("", "") })

  fun AddQuestion(question: String, answer: String) {
    var question_answer: Pair<String, String> = Pair<String, String>(question, answer)
    questions_.add(question_answer)
  }
}

typealias ListWebsiteEntries = MutableList<WebsiteEntry>
typealias SearchResultListWebsiteEntries = MutableList<Pair<Int, WebsiteEntry>>

class DataManager {

  val LOCAL_DATA_JSON_VERSION = 1

  // @Serializable
  private var all_datas_: ListWebsiteEntries =
    MutableList<WebsiteEntry>(0, { i -> WebsiteEntry("", "", "", "", "") })
  private var local_file_path_: File? = null
  private var latest_search_results: MutableList<Pair<Int, WebsiteEntry>> =
    MutableList<Pair<Int, WebsiteEntry>>(0, { i -> Pair(0, WebsiteEntry("", "", "", "", "")) })

  init {
    all_datas_.add(WebsiteEntry("google.com", "email", "user1", "pass1", "no comments"))
    all_datas_.add(WebsiteEntry("yahoo.com", "email", "user2", "passwords2", "no comments"))
    all_datas_.add(WebsiteEntry("google2.com", "email", "user3", "pass3", "no comments"))
    all_datas_[0].AddQuestion("question1", "answer1")
    all_datas_[0].AddQuestion("q2", "a2")
    DoSearch("")
  }

  fun SetLocalPath(file_path: File) {
    local_file_path_ = file_path
  }


  fun GetDataAt(index: Int): WebsiteEntry {
    return all_datas_[index]
  }

  fun GetSearchResult(): SearchResultListWebsiteEntries {
    return latest_search_results
  }

  fun DoSearch(query: String) {
    var ret: SearchResultListWebsiteEntries =
      MutableList<Pair<Int, WebsiteEntry>>(0, { i -> Pair(0, WebsiteEntry("", "", "", "", "")) })
    var index: Int = 0
    for (data in all_datas_) {
      if (data.website_.contains(query) || data.comments_.contains(query) || query == "") {
        ret.add(Pair(index, data))
      }
      ++index
    }
    latest_search_results = ret
  }

  fun SaveToLocalStorage() {
    assert(local_file_path_ != null)
    var file: File = File(local_file_path_, "file.txt")
    println("Using file path for writing " + file.absolutePath)
    file.createNewFile()
    if (!file.canWrite()) {
      throw error("File " + file.absolutePath + " can't write.")
    }
    var content: String = Json.encodeToString(all_datas_)
    file.writeText(content)
  }

  fun ReadFromLocalStorage() {
    assert(local_file_path_ != null)
    var file: File = File(local_file_path_, "file.txt")
    println("Using file path for reading " + file.absolutePath)
    if (!file.canRead()) {
      throw error("File " + file.absolutePath + " can't read.")
    }
    var content = file.readText()
    println("context " + content)
    all_datas_ = Json.decodeFromString<ListWebsiteEntries>(content)
  }
}