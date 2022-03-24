import java.io.File
import kotlin.random.Random

var term = ""
var definition = ""
var cards: MutableList<Card> = mutableListOf()
var exitStatus = false
var  log: String = ""
var exportByExit = false
var exportByExitFileName = ""

fun main(args: Array<String>) {
    for (i in 0.. args.size - 2) {
        if (args[i] == "-import") {
            importByStart(args[i + 1])
        }
        if (args[i] == "-export") {
            exportByStart(args[i + 1])
        }
    }



    while (!exitStatus ) {
        printlnWithLog("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
        when (readLineWithLog()) {
            "add" -> addIsSelected()
            "remove" -> removeIsSelected()
            "import" -> importIsSelected()
            "export" -> exportIsSelected()
            "ask" -> askIsSelected()
            "exit" -> exitIsSelected()
            "log" -> logIsSelected()
            "hardest card" -> hardestCardIsSelected()
            "reset stats" -> resetStatsIsSelected()
        }
    }
}

fun exportByStart(fileName: String) {
    exportByExit = true
    exportByExitFileName = fileName
}

fun importByStart(fileName: String) {

    var cardsFromFile: MutableList<Card> = mutableListOf()
    //printlnWithLog("File name:")
    //val fileName = readLineWithLog()
    val file = File(fileName)
    if (file.exists()) { // checks if file exists
        val lines = file.readText()
        val cardsNumberLoaded = cardsCounting(lines)
        cardsFromFile = getCardsFromFile(lines)
        printlnWithLog("$cardsNumberLoaded cards have been loaded.")

        for (i in 0 until cardsFromFile.size) {
            for ( j in 0 until cards.size)
                if (cardsFromFile[i].term == cards[j].term)  cards[j] = cardsFromFile[i]
        }
        cards += cardsFromFile
        cards = cards.toSet().toMutableList()

    } //else printlnWithLog("File not found.")
}

fun printlnWithLog(s: String) {
    println(s)
    log += s +"\n"
}

fun resetStatsIsSelected() {
    for (i in 0 until cards.size) {
        cards[i].errors = 0
    }
    printlnWithLog("Card statistics have been reset.")
}

fun hardestCardIsSelected() {
    var maxErrors = 0
    var errorCardIndex = 0
    var maximumTimes = 0
    var messageForSeveralMaxTimes = ""      //The hardest cards are "term_1", "term_2"
    for (i in 0 until cards.size) {
        if (maxErrors < cards[i].errors) {
            messageForSeveralMaxTimes = ""
            maximumTimes = 1
            maxErrors = cards[i].errors
            errorCardIndex = i
        }
        if (maxErrors != 0 && maxErrors == cards[i].errors){
            maximumTimes ++
            messageForSeveralMaxTimes += "\"${cards[i].term}\","
        }

    }
    if (maxErrors == 0)printlnWithLog("There are no cards with errors.")
    if (maximumTimes == 2) {
        printlnWithLog("The hardest card is \"${cards[errorCardIndex].term}\". You have $maxErrors errors answering it.")
    }
    if (maximumTimes > 2) {
        printlnWithLog("The hardest cards are $messageForSeveralMaxTimes. You have $maxErrors errors answering them.")

    }
}

fun logIsSelected() {
    printlnWithLog("File name:")
    val fileName = readLineWithLog()
    printlnWithLog("The log has been saved.")
    val textForFile = log
    File(fileName).writeText(textForFile)
}

fun importIsSelected() {
    var cardsFromFile: MutableList<Card> = mutableListOf()
    printlnWithLog("File name:")
    val fileName = readLineWithLog()
    val file = File(fileName)
    if (file.exists()) { // checks if file exists
        val lines = file.readText()
        val cardsNumberLoaded = cardsCounting(lines)
        cardsFromFile = getCardsFromFile(lines)
        printlnWithLog("$cardsNumberLoaded cards have been loaded.")

        for (i in 0 until cardsFromFile.size) {
            for ( j in 0 until cards.size)
                if (cardsFromFile[i].term == cards[j].term)  cards[j] = cardsFromFile[i]
        }
        cards += cardsFromFile
        cards = cards.toSet().toMutableList()

    } else printlnWithLog("File not found.")
}

fun readLineWithLog(): String {
    val s = readLine()!!
    log += s +"\n"
    return s
}

fun getCardsFromFile(lines: String): MutableList<Card> {
    var termFromFile = ""
    var definitionFromFile = ""
    var errorsFromFile = "0"
    var cardFromFile = Card(termFromFile, definitionFromFile , errorsFromFile.toInt())
    var indexOfCardFromFile = 0
    var firstIndexForTerm = 0
    var lastIndexForTerm: Int
    var firstIndexForDefinition = 0
    var lastIndexForDefinition: Int
    var firstIndexForErrors = 0
    var lastIndexForErrors = 0
    var cardsFromFile: MutableList<Card> = mutableListOf()
    for (i in lines.indices - 3) { // //errors=4)
        if (lines[i] == 'r' &&
            lines[i + 1] == 'o' &&
            lines[i + 2] == 'r' &&
            lines[i + 3] == 's' &&
            lines[i + 4] == '='
        ) {
            firstIndexForErrors = i + 5
        }
        if (lines[i] == ')') {
            lastIndexForErrors = i - 1
            for (j in firstIndexForErrors..lastIndexForErrors) {
                errorsFromFile += lines[j]
            }
            cardsFromFile[indexOfCardFromFile - 1].errors = errorsFromFile.toInt()
            errorsFromFile = ""
        }

        if (lines[i] == 't' &&
            lines[i + 1] == 'e' &&
            lines[i + 2] == 'r' &&
            lines[i + 3] == 'm' &&
            lines[i + 4] == '='
        ) {
            firstIndexForTerm = i + 5
        }
        if (lines[i] == ',' &&
            lines[i + 1] == ' ' &&
            lines[i + 2] == 'd' &&
            lines[i + 3] == 'e' &&
            lines[i + 4] == 'f'
        ) {
            lastIndexForTerm = i - 1
            for (j in firstIndexForTerm..lastIndexForTerm) {
                termFromFile+= lines[j]
            }
            cardsFromFile += cardFromFile
            indexOfCardFromFile++
            cardsFromFile[indexOfCardFromFile - 1].term = termFromFile
            termFromFile = ""
        }

        if (lines[i] == 't' &&
            lines[i + 1] == 'i' &&
            lines[i + 2] == 'o' &&
            lines[i + 3] == 'n' &&
            lines[i + 4] == '='
        ) {
            firstIndexForDefinition = i + 5
        }

        if (lines[i] == ',' &&
            lines[i + 1] == ' ' &&
            lines[i + 2] == 'e' &&
            lines[i + 3] == 'r' &&
            lines[i + 4] == 'r'
        ) {
            lastIndexForDefinition = i - 1
            for (j in firstIndexForDefinition..lastIndexForDefinition) {
                definitionFromFile+= lines[j]
            }
            cardFromFile.definition = definitionFromFile
            cardsFromFile[indexOfCardFromFile - 1] = cardFromFile
            cardFromFile = Card("","")
            definitionFromFile = ""
        }
    }
    return cardsFromFile
}

fun cardsCounting(lines: String): Int { //Card(term=
    var result = 0
    for (i in lines.indices - 4){
        if (lines[i] =='t' &&
            lines[i+1] == 'e' &&
            lines[i+2] == 'r' &&
            lines[i+3] == 'm' &&
            lines[i+4] == '=') result++
    }
    return result
}

fun exportIsSelected() {
    printlnWithLog("File name:")
    val textForFile = cards.toString()
    val fileName = readLineWithLog()
    File(fileName).writeText(textForFile)
    printlnWithLog("${cards.size} cards have been saved")
}

fun askIsSelected() {
    printlnWithLog("How many times to ask?")
    var timesToAsk = readLineWithLog()!!.toInt()
    if (timesToAsk > cards.size) timesToAsk = cards.size
    for (i in 0 until timesToAsk) {
        val j =  Random.nextInt(cards.size)
        statusAnswerGetter(j, cards, cards.size)
    }
    printlnWithLog("")
}

fun removeIsSelected() {
    printlnWithLog("Which card?")
    val cardForDelete = readLineWithLog()
    for (i in 0 until cards.size) {
        if (cardForDelete == cards[i]?.term) {
            cards.remove(cards[i])
            printlnWithLog("The card has been removed.")
            return
        }
    }
    printlnWithLog("Can't remove \"$cardForDelete\": there is no such card.")
}

fun addIsSelected() {
    for (i in 0 until cards.size + 1) {
        printlnWithLog("The card:")
        term = readLineWithLog()
        if (termAlreadyExists(term, cards, cards.size)) break
        printlnWithLog("The definition of the card:")
        definition = readLineWithLog()
        if (definitionAlreadyExists(definition, cards, cards.size)) break
        val card = Card(term, definition , 0)
        cards.add(i, card)
        printlnWithLog("The pair (\"${cards[i].term}\":\"${cards[i].definition}\") has been added.\n")
        break
    }

}

fun exitIsSelected() {
    if (exportByExit) {
        exportByExit = false
        val textForFile = cards.toString()
        val fileName = exportByExitFileName
        File(fileName).writeText(textForFile)
        printlnWithLog("${cards.size} cards have been saved")
    }

    printlnWithLog("Bye bye!")
    exitStatus = true
}

fun statusAnswerGetter(i: Int, cards: MutableList<Card>, cardsNumber: Int): Int {
    printlnWithLog("Print the definition of \"${cards[i]?.term}\"")
    var result = i
    val answer = readLineWithLog()
    if (answer == cards[i]?.definition) {
        printlnWithLog("Correct!")
    } else {for (j in 0 until cardsNumber){
        if (answer == cards[j]?.definition) {
            printlnWithLog("Wrong. The right answer is \"${cards[i].definition}\"," +
                    " but your definition is correct for \"${cards[j].term}\".")
            cards[i].errors ++
            result = j
        }
    }
        if(i == result) {
            printlnWithLog("Wrong. The right answer is \"${cards[i].definition}\"")
            cards[i].errors ++
        }
    }
    return result
}

fun definitionAlreadyExists(definition: String, cards: MutableList<Card>, cardsNumber: Int): Boolean {
    for (i in 0 until cardsNumber){
        if (definition == cards[i].definition){
            printlnWithLog("The definition \"${cards[i].definition}\" already exists.\n")
            return true
        }
    }
    return false
}

fun termAlreadyExists(term: String, cards: MutableList<Card>, cardsNumber: Int): Boolean {
    for (i in 0 until cardsNumber){
        if (term == cards[i]?.term){
            println("The card \"${cards[i]?.term}\" already exists.\n")
            return true
        }
    }
    return false
}

data class Card (
    var term: String = "",
    var definition: String = "",
    var errors: Int = 0
)