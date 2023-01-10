package com.example.memorygame.models

import android.util.Log
import com.example.memorygame.utils.HPIMAGES

class MemoryGame(private val boardSize: BoardSize){

    val cards: List<MemoryCard>
    var numPairsFound = 0

    private var numCardFlips = 0
    private var indexOfSingleSelectedCard: Int? = null

    init {
        val chosenImages = HPIMAGES.shuffled().take(boardSize.getNumPairs())
        val randomizedImages = (chosenImages + chosenImages).shuffled()
        cards = randomizedImages.map { MemoryCard(it) }
        Log.d("MemoryCards list", "cards: $cards")
    }


    fun flipCard(position: Int): Boolean {
        numCardFlips++
        val card = cards[position]
        var foundMatch = false
        // 1. durum: daha once cevrilen 0 kart > flip
        // 2. durum: daha once cevrilen 1 kart > flip + kontrol
        // 3. durum: daha once cevrilen 2 kart > hepsini cevir sonra isteneni cevir
        if (indexOfSingleSelectedCard == null){
            // 1.ve 3.durum
            restoreCards()
            indexOfSingleSelectedCard = position
        }
        else{
            // 2.durum
            foundMatch = checkForMatch(indexOfSingleSelectedCard!!, position)
            indexOfSingleSelectedCard = null


        }

        card.isFaceUp = !card.isFaceUp
        return foundMatch
    }

    private fun checkForMatch(position1: Int, position2: Int): Boolean {
        if(cards[position1].id != cards[position2].id){
            return false
        }
        cards[position1].isMatched = true
        cards[position2].isMatched = true
        numPairsFound++
        return true
    }

    private fun restoreCards() {
        for (card in cards){
            if (!card.isMatched){
                card.isFaceUp = false
            }
        }
    }

    fun haveWonGame(): Boolean {
        return numPairsFound == boardSize.getNumPairs()

    }

    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaceUp

    }

    fun getNumMoves(): Int {
        return numCardFlips / 2
    }
}