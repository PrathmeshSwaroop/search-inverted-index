package search

import search.SearchApp.Display.MESSAGE_1
import search.SearchApp.Display.MESSAGE_3
import search.SearchApp.Display.MESSAGE_4
import search.SearchApp.Display.MESSAGE_5
import search.SearchApp.Display.MESSAGE_6
import search.SearchApp.Display.MESSAGE_7
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object SearchApp {
    private val indexedData: MutableMap<String, MutableList<Int>> = HashMap()
    private val records: ArrayList<Person> = ArrayList()
    val actions: Array<(scanner: Scanner) -> Unit> = Array<(scanner: Scanner) -> Unit>(3) {
        when (it) {
            Action.SHOW -> ({ _ ->
                showListOfRecords()
            })
            Action.SEARCH -> ({ scanner ->
                searchAPerson(scanner)
            })
            else -> ({ _ ->
                println(MESSAGE_3)
            })
        }
    }

    private fun searchAPerson(scanner: Scanner) {
        println(MESSAGE_7)
        val searchStrategy = scanner.nextLine()
        println(MESSAGE_1)
        scanner.nextLine()
                .toLowerCase()
                .split(" ")
                .let { queries ->
                    searchAction(queries, searchStrategy)
                }
    }

    private fun searchAction(queries: List<String>, searchStrategy: String) {
        when (searchStrategy) {
            SearchStrategy.ALL -> searchAllQueries(queries)
            SearchStrategy.ANY -> searchAnyQuery(queries)
            SearchStrategy.NONE -> searchNoneFromQuery(queries)
            else -> println(MESSAGE_5)
        }
        println()
    }

    private fun searchAllQueries(queries: List<String>) {
        val count = queries.filter { indexedData.containsKey(it) }.size
        if (count == queries.size) {
            indexedData[queries[0]]?.run {
                var finalSearch = this
                for (flag in 1..queries.lastIndex) {
                    val mutableList = indexedData[queries[flag]]
                    mutableList?.let { list ->
                        finalSearch = finalSearch.filter {
                            list.contains(it)
                        }.toMutableList()
                    }
                }
                if (finalSearch.isNotEmpty()) {
                    println(MESSAGE_6.format(finalSearch.size))
                    finalSearch.forEach {
                        println(records[it])
                    }
                } else {
                    println(Display.MESSAGE_2)
                }
            }
        } else {
            println(Display.MESSAGE_2)
        }
    }

    private fun searchAnyQuery(queries: List<String>) {
        val filteredQueries = queries.filter { indexedData.containsKey(it) }
        if (filteredQueries.isNotEmpty()) {
            val finalSearch = indexedData[filteredQueries[0]]!!
            for (flag in 1..filteredQueries.lastIndex) {
                indexedData[filteredQueries[flag]]?.forEach { index ->
                    if (!finalSearch.contains(index)) {
                        finalSearch.add(index)
                    }
                }
            }
            if (finalSearch.isNotEmpty()) {
                println(MESSAGE_6.format(finalSearch.size))
                finalSearch.forEach { index ->
                    println(records[index])
                }
            } else {
                println(Display.MESSAGE_2)
            }
        } else {
            println(Display.MESSAGE_2)
        }
    }

    private fun searchNoneFromQuery(queries: List<String>) {

        val finalSearch = ArrayList<Int>()

        for (entry in indexedData) {
            entry.value.forEach {
                if (!finalSearch.contains(it)) {
                    finalSearch.add(it)
                }
            }
        }
        for (query in queries) {
            indexedData[query]?.forEach { index ->
                finalSearch.remove(index)
            }
        }
        if (finalSearch.isNotEmpty()) {
            println(MESSAGE_6.format(finalSearch.size))
            finalSearch.forEach { index ->
                println(records[index])
            }
        } else {
            println(Display.MESSAGE_2)
        }

    }

    fun loadData(file: File) {
        Scanner(file).use { fs ->
            val recordList = ArrayList<Person>()
            var index = 0
            while (fs.hasNext()) {
                val input = fs.nextLine().split(" ")
                input.forEach {
                    it.toLowerCase().run {
                        if (indexedData.containsKey(this)) {
                            indexedData[this]?.add(index)
                        } else {
                            val list = ArrayList<Int>()
                            list.add(index)
                            indexedData[this] = list
                        }
                    }
                }
                recordList.add(when {
                    input.size > 2 -> Person(input[0], input[1], input[2])
                    input.size > 1 -> Person(input[0], input[1])
                    else -> Person(input[0])
                })
                index++
            }
            this.records.addAll(recordList)
        }
    }

    private fun showListOfRecords() {
        println(MESSAGE_4)
        this.records.forEach {
            println(it)
        }
    }

    object Action {
        val EXIT = 0
        val SEARCH = 1
        val SHOW = 2
    }

    object SearchStrategy {
        val ANY = "ANY"
        val ALL = "ALL"
        val NONE = "NONE"
    }

    object Display {
        const val MENU = "=== Menu ===\n" +
                "1. Find a person\n" +
                "2. Print all people\n" +
                "0. Exit\n"
        const val MESSAGE_1 = "Enter a name or email to search all suitable people."
        const val MESSAGE_2 = "No matching people found."
        const val MESSAGE_3 = "Bye!"
        const val MESSAGE_4 = "=== List of people ==="
        const val MESSAGE_5 = "Incorrect option! Try again."
        const val MESSAGE_6 = "%s persons found:"
        const val MESSAGE_7 = "Select a matching strategy: ALL, ANY, NONE"
    }
}
