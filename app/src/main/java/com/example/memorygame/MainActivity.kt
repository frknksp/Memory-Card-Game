package com.example.memorygame

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memorygame.models.BoardSize
import com.example.memorygame.models.MemoryGame
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }
    private lateinit var clRoot: ConstraintLayout
    private lateinit var memoryGame: MemoryGame
    private lateinit var adapter: MemoryBoardAdapter

    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView
    private lateinit var tvNumTimer: TextView

    private lateinit var mediaPlayer: MediaPlayer


    private var boardSize: BoardSize = BoardSize.EASY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clRoot = findViewById(R.id.clRoot)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)
        tvNumTimer = findViewById(R.id.tvNumTimer)

//        setupBoard()
        showNewSizeDialog()
        mediaPlayer = MediaPlayer.create(this, R.raw.prologuew)
        mediaPlayer.start()
        mediaPlayer.isLooping = true


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        menu?.findItem(R.id.ic_musicoff)?.setOnMenuItemClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.start()
            }
            true
        }
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.mi_refresh -> {
                if (memoryGame.getNumMoves() > 0 && !memoryGame.haveWonGame()){
                    showAlertDialog("Quit your current game ?", null, View.OnClickListener {
                        setupBoard()
                    })
                }else{
                    setupBoard()
                }
                return  true
            }
            R.id.mi_new_size -> {
                showNewSizeDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showNewSizeDialog() {
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        when(boardSize) {
            BoardSize.EASY -> radioGroupSize.check(R.id.rbEasy)
            BoardSize.MEDIUM -> radioGroupSize.check(R.id.rbMedium)
            BoardSize.HARD -> radioGroupSize.check(R.id.rbHard)
        }
        showAlertDialog("Choose new size", boardSizeView, View.OnClickListener {
            boardSize = when (radioGroupSize.checkedRadioButtonId){
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                else -> BoardSize.HARD
            }
            setupBoard()

        })
    }

    private fun showAlertDialog(title: String, view: View?, positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK") { _,_ ->
                positiveClickListener.onClick(null)
            }.show()
    }


    @SuppressLint("SetTextI18n")
    
    val timer = object : CountDownTimer(46000, 1000) {

        override fun onTick(millisUntilFinished: Long) {
            tvNumTimer.text = "Time: ${millisUntilFinished / 1000}"
            if(memoryGame.haveWonGame()){
                cancel()
            }
        }


        override fun onFinish() {
            tvNumTimer.text = "Time OVER!"
            cancel()
            if(!memoryGame.haveWonGame() ){
                showAlertDialog("Time over, play again ?", null, View.OnClickListener {
//                    setupBoard()
                    showNewSizeDialog()
                })
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupBoard() {
        timer.start()

        when(boardSize) {
            BoardSize.EASY -> {
                tvNumMoves.text = "Easy:\n2 x 2"
                tvNumPairs.text = "Pairs:\n0 / 2"
            }
            BoardSize.MEDIUM -> {
                tvNumMoves.text = "Medium:\n6 x 3"
                tvNumPairs.text = "Pairs:\n0 / 9"
            }
            BoardSize.HARD -> {
                tvNumMoves.text = "Hard:\n6 x 4"
                tvNumPairs.text = "Pairs:\n0 / 12"
            }
        }
        memoryGame = MemoryGame(boardSize)
        adapter = MemoryBoardAdapter(this, boardSize, memoryGame.cards, object: MemoryBoardAdapter.CardClickListener {
            override fun onCardClicked(position: Int) {
                updateGameWithFlip(position)
            }

        })
        rvBoard.adapter = adapter
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())

    }

    private fun updateGameWithFlip(position: Int) {
        if (memoryGame.haveWonGame()){
             Snackbar.make(clRoot, "Zaten Kazandınız", Snackbar.LENGTH_LONG).show()
            return
        }
        if (memoryGame.isCardFaceUp(position)){
            Snackbar.make(clRoot, "Kart zaten seçildi", Snackbar.LENGTH_SHORT).show()
            return
        }
        // flip card
        if(memoryGame.flipCard(position)){
            Log.i(TAG, "Bulunan eş sayisi: ${memoryGame.numPairsFound}")
            tvNumPairs.text = "Pairs: \n${memoryGame.numPairsFound} / ${boardSize.getNumPairs()}"
            if (memoryGame.haveWonGame()){
                Snackbar.make(clRoot, "Congratulations, You won! ",Snackbar.LENGTH_LONG).show()
            }
        }
        tvNumMoves.text = "Moves: \n${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()

    }
}