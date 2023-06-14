package com.example.wordhunt

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController

/**
 * Fragment Endgame následuje po skončení hry GameFragment ked hráčovi vyprší čas a hráč sa môže rozhodnúť pomocou dvoch tlačidiel Hraj Znova alebo Vrať sa do Hlavnej Obrazovky
 * ktoré ako názvy napovedajú tak Hraj Znova z resetuje hru od začiatku s tým istým nastavením ako si hráč vybral ked si volil obtiažnosť
 * a vrať sa do hlavnej Obrazovky sa hráč vráti do HomeFragment kde si može pozrieť tabulku svojích hier, začať hru znova alebo ukončiť aplikáciu.
 */
class EndGameFragment : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_end_game, container, false)
        val scoreTextView: TextView = view.findViewById(R.id.score_text)
        val messageTextView: TextView = view.findViewById(R.id.message_text)
        val restartButton: Button = view.findViewById(R.id.restart_button)
        val mainButton: Button = view.findViewById(R.id.main_button)


        scoreTextView.text = buildString {
        append("Tvoje skóre: ")
        append(GameData.score)
    }

        messageTextView.text = "Čas vypršal koniec hry"

        /**
         * Keď hrač klikne na HrajZnova tak sa nastaví skóre na 0 a scoreMultiplier sa nastaví na začiatočné ktoré je v danej obtiažnosti.
         */
        restartButton.setOnClickListener {
            findNavController().navigate(R.id.action_endGameFragment_to_gameFragment)
            GameData.score = 0
            GameData.scoreMultiplier = when (GameData.obtiaznost) {
                "Ľahká" -> 1.0
                "Stredná" -> 1.5
                "Ťažká" -> 2.0
                else -> 1.0 // Default case
            }
        }
        /**
         * Tento button hráča vráti do HomeFragment
         */
        mainButton.setOnClickListener {
            findNavController().navigate(R.id.action_endGameFragment_to_homeFragment)
        }

        return view
    }

}
