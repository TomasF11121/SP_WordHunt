package com.example.wordhunt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

/**
 * Fragment ObtiaznostFragment slúži na výber obtiažnosti hry pred začatím hry.
 * Obsahuje tlačidlá pre výber ľahkej, strednej a ťažkej obtiažnosti.
 */
class ObtiaznostFragment : Fragment() {
    private lateinit var easyButton: Button
    private lateinit var mediumButton: Button
    private lateinit var hardButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_obtiaznost, container, false)

        // Inicializácia tlačidiel
        easyButton = view.findViewById(R.id.easy_button)
        mediumButton = view.findViewById(R.id.medium_button)
        hardButton = view.findViewById(R.id.hard_button)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Nastavenie onClickListenera pre tlačidlo "Ľahká"
        easyButton.setOnClickListener {
            // Nastavenie obtiažnosti na "Ľahká", skóre na 0 a multiplier na 1.0
            GameData.obtiaznost = "Ľahká"
            GameData.scoreMultiplier = 1.0
            GameData.score = 0
            // Prechod na GameFragment pomocou navigačného kontrollera
            findNavController().navigate(R.id.action_obtiaznostFragment_to_gameFragment)
        }

        // Nastavenie onClickListenera pre tlačidlo "Stredná"
        mediumButton.setOnClickListener {
            // Nastavenie obtiažnosti na Stredná, skóre na 0 a multiplier na 1.5
            GameData.obtiaznost = "Stredná"
            GameData.scoreMultiplier = 1.5
            GameData.score = 0
            // Prechod na GameFragment pomocou navigačného kontrollera
            findNavController().navigate(R.id.action_obtiaznostFragment_to_gameFragment)
        }

        // Nastavenie onClickListenera pre tlačidlo "Ťažká"
        hardButton.setOnClickListener {
            // Nastavenie obtiažnosti na "Ťažká", skóre na 0 a multiplier na 2.0
            GameData.obtiaznost = "Ťažká"
            GameData.scoreMultiplier = 2.0
            GameData.score = 0
            // Prechod na GameFragment pomocou navigačného kontrollera
            findNavController().navigate(R.id.action_obtiaznostFragment_to_gameFragment)
        }
    }
}
