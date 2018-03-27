package com.dogoodapps.studybot

import com.dogoodapps.studybot.parser.LuaParser
import java.io.*
import java.util.*

@Suppress("MayBeConstant")
class StudyBot {

    companion object {
        private val OUTPUT_FILE_NAME = "output.html"
        private val TEXT = "text"
        private val HIGHLIGHT = "highlight"
        private val LUA_HEADER_TO_SKIP = "-- we can read Lua syntax here!"
        private val LUA_OBJECT_START_TO_REPLACE = "return {"
        private val LUA_OBJECT_START = "{"
        private val HTML_META_HEADER = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />"
        private val STATS = "stats"
        private val AUTHORS = "authors"
        private val TITLE = "title"
    }

    // TODO: Pass in output location!! Html settings etc..
    fun parse(fileName: String?) {
        val hashMap = readFile(fileName)
        writeOutToFile(hashMap)
        println("Success!")
    }

    private fun readFile(fileName: String?): HashMap<*, *> {
        val file = File(fileName)
        val bufferedReader = file.bufferedReader()
        val text: List<String> = bufferedReader.readLines()
        val builder = StringBuilder()
        for (line in text) {
            var inputLine = line
            if (inputLine != LUA_HEADER_TO_SKIP) { // don't include this
                if (line.contains(LUA_OBJECT_START_TO_REPLACE)) {
                    inputLine = inputLine.replace(LUA_OBJECT_START_TO_REPLACE, LUA_OBJECT_START)
                }
                builder.append(inputLine)
            }
        }
        return LuaParser(StringReader(builder.toString())).parse() as HashMap<*, *>
    }

    private fun writeOutToFile(hashMap: HashMap<*, *>) {
        val outPutFileName =
                System.getProperty("user.home") +
                        File.separator +
                        "Desktop" +
                        File.separator +
                        OUTPUT_FILE_NAME

        val fileWriter = FileWriter(outPutFileName)
        val bufferedWriter = BufferedWriter(fileWriter)

        bufferedWriter.write(HTML_META_HEADER)
        writeOutStats(hashMap, bufferedWriter)
        writeOutHighlights(hashMap, bufferedWriter)
        bufferedWriter.newLine()
        bufferedWriter.close()
    }

    @Throws(IOException::class)
    private fun writeOutStats(input: HashMap<*, *>, bufferedWriter: BufferedWriter?) {
        if (input.containsKey(STATS)) {
            val stats = input[STATS] as HashMap<*, *>
            for ((key, value) in stats) {
                var authors: String?
                if (key == AUTHORS) {
                    authors = value as String
                    bufferedWriter!!.write("<h3>$authors</h3>")
                }
                if (key == TITLE) {
                    val title = value as String
                    bufferedWriter!!.write("<h1>$title</h1>")
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun writeOutHighlights(input: HashMap<*, *>, bufferedWriter: BufferedWriter) {
        if (input.containsKey(HIGHLIGHT)) {
            println("Highlights \n")

            // Get entries and sort by page number
            val highlights = input[HIGHLIGHT] as HashMap<String, Any>
            val sortedHighlights = TreeMap<String, Any>(Comparator { o1, o2 -> Integer.valueOf(o1).compareTo(Integer.valueOf(o2)) })
            sortedHighlights.putAll(highlights)

            val keys = ArrayList(sortedHighlights.keys)
            for (pageKey in keys) {
                if (highlights[pageKey] is List<*>) {
                    val highlightItems = highlights[pageKey] as ArrayList<HashMap<*, *>>
                    for (level1Item in highlightItems) {
                        for ((key) in level1Item) {
                            outputHighlight(level1Item as HashMap<String, HashMap<*, *>>, pageKey, key as String, bufferedWriter)
                        }
                    }

                } else if (highlights[pageKey] is HashMap<*, *>) {
                    // Get and sort sub highlights
                    val subHighlights = highlights[pageKey] as HashMap<String, HashMap<*, *>>
                    val sortedSubHighlights = TreeMap<String, Any>(Comparator { o1, o2 -> Integer.valueOf(o1).compareTo(Integer.valueOf(o2)) })
                    sortedSubHighlights.putAll(subHighlights)

                    val subKeys = ArrayList(sortedSubHighlights.keys)
                    for (subKey in subKeys) {
                        val subHighlights1 = sortedSubHighlights[subKey] as HashMap<String, String>
                        for ((key) in subHighlights1) {
                            if (key == TEXT) {
                                // TODO: Out put through same helper method
                                bufferedWriter.write("<span> Page: $pageKey</span></br>")
                                bufferedWriter.write("<span>" + subHighlights1[key] + "</span></br></br>")
                            }
                        }
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    @Suppress("CAST_NEVER_SUCCEEDS") // Yes it does
    private fun outputHighlight(hightlightItem: HashMap<String, HashMap<*, *>>, pageKey: String,
                                key: String, bufferedWriter: BufferedWriter) {
        if (key == TEXT) {
            bufferedWriter.write("<span> Page: $pageKey</span></br>")
            bufferedWriter.write("<span>" + hightlightItem[key] as String + "</span></br></br>")
        }
    }
}