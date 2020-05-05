package search

import java.io.File
import java.util.*


const val DATA_ARG = "--data"


fun main(args: Array<String>) {
    if (args.contains(DATA_ARG)) {
        File(args[1]).run {
            if (exists() && isFile) {
                SearchApp.loadData(this)
            }
        }
    }
    Scanner(System.`in`).use { scanner ->
        applicationLoop@ while (true) {
            println(SearchApp.Display.MENU)
            val inputString = scanner.nextLine()
            val action = inputString.toInt()
            if (action !in SearchApp.actions.indices) {
                println(SearchApp.Display.MESSAGE_5)
            } else {
                SearchApp.actions[action].invoke(scanner)
                if (action == SearchApp.Action.EXIT) {
                    break@applicationLoop
                }
            }
        }
    }

}

