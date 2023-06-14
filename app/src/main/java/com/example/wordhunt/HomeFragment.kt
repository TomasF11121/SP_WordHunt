package com.example.wordhunt

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

/**
 * Fragment HomeFragment slúži ako domovská obrazovka hry.
 * Obsahuje tlačidlá pre spustenie hry, zobrazenie hodnotení a ukončenie aplikácie.
 */
class HomeFragment : Fragment() {

    private lateinit var titleTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate layout pre tento fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inicializácia TextView pre názov
        titleTextView = view.findViewById(R.id.title_textview)

        // Inicializácia tlačidiel
        val playButton: Button = view.findViewById(R.id.play_button)
        val ratingButton: Button = view.findViewById(R.id.rating_button)
        val exitButton: Button = view.findViewById(R.id.exit_button)

        // Nastavenie onClickListenera pre tlačidlo "Offline"
        playButton.setOnClickListener {
            startGame()
        }

        // Nastavenie onClickListenera pre tlačidlo "Hodnotenia"
        ratingButton.setOnClickListener {
            showRatings()
        }

        // Nastavenie onClickListenera pre tlačidlo "Ukončiť"
        exitButton.setOnClickListener {
            requireActivity().finish()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Spustenie animácie nad textom
        animateText()
    }

    /**
     * Metóda animateText() slúži na animáciu textu Word Hunt.
     * Používa rôzne animačné efekty, ako rotáciu, škálovanie, posunutie a zmena farby pozadia.
     */
    private fun animateText() {
        val rotateAnimator = ObjectAnimator.ofFloat(titleTextView, "rotation", 0f, 360f)
        // Nastavenie trvania rotácie
        rotateAnimator.duration = 2000
        // Nastavenie nekonečného opakovania rotácie
        rotateAnimator.repeatCount = ObjectAnimator.INFINITE

        // Inicializácia ďalších animácií pre škálovanie, posunutie a zmenu farby pozadia daného textu
        val scaleAnimatorX = ObjectAnimator.ofFloat(titleTextView, "scaleX", 1f, 1.5f)
        // Nastavenie nekonečného opakovania a spätného škálovania pre scaleX animátor
        scaleAnimatorX.repeatCount = ObjectAnimator.INFINITE
        scaleAnimatorX.repeatMode = ObjectAnimator.REVERSE
        scaleAnimatorX.duration = 1000

        // Podobne ako scaleAnimatorX
        val scaleAnimatorY = ObjectAnimator.ofFloat(titleTextView, "scaleY", 1f, 1.5f)
        scaleAnimatorY.repeatCount = ObjectAnimator.INFINITE
        scaleAnimatorY.repeatMode = ObjectAnimator.REVERSE
        scaleAnimatorY.duration = 1000

        // Podobne ako scaleAnimatorX
        val translateAnimatorX = ObjectAnimator.ofFloat(titleTextView, "translationX", 0f, 200f)
        translateAnimatorX.repeatCount = ObjectAnimator.INFINITE
        translateAnimatorX.repeatMode = ObjectAnimator.REVERSE
        translateAnimatorX.duration = 1000

        // Podobne ako scaleAnimatorX
        val translateAnimatorY = ObjectAnimator.ofFloat(titleTextView, "translationY", 0f, 200f)
        translateAnimatorY.repeatCount = ObjectAnimator.INFINITE
        translateAnimatorY.repeatMode = ObjectAnimator.REVERSE
        translateAnimatorY.duration = 1000

        // Podobne ako scaleAnimatorX, ale mení farbu pozadia
        val backgroundColorAnimator =
            ObjectAnimator.ofArgb(titleTextView, "backgroundColor", Color.RED, Color.BLUE)
        backgroundColorAnimator.repeatCount = ObjectAnimator.INFINITE
        backgroundColorAnimator.repeatMode = ObjectAnimator.REVERSE
        backgroundColorAnimator.duration = 1000

        // Vytvorenie a spustenie animačného setu, ktorý spája všetky animácie
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            rotateAnimator,
            scaleAnimatorX,
            scaleAnimatorY,
            translateAnimatorX,
            translateAnimatorY,
            backgroundColorAnimator
        )
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.start()
    }

    /**
     * Metóda startOfflineGame() slúži na spustenie offline hry.
     * Používa navigačný kontroller na prechod na fragment s výberom obtiažnosťí.
     */
    private fun startGame() {
        findNavController().navigate(R.id.action_homeFragment_to_obtiaznostFragment)
    }

    /**
     * Metóda showRatings() slúži na zobrazenie hodnotení.
     * Používa navigačný kontroller na prechod na fragment s hodnoteniami.
     */
    private fun showRatings() {
        findNavController().navigate(R.id.action_homeFragment_to_HodnotenieFragment)
    }
}
