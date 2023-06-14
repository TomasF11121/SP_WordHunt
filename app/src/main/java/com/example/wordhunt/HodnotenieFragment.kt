package com.example.wordhunt

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Trieda reprezentujúca skóre s informáciami o obtiažnosti a skóre
data class Score(val difficulty: String, val score: Int)

/**
 * Fragment HodnotenieFragment slúži na zobrazenie skóre v RecyclerView.
 * Využíva ViewModel pre získanie zoznamu skóre z gameFragmentu a ScoreAdapter pre zobrazenie jednotlivých skór ktoré hráč dosiahol.
 */
class HodnotenieFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var scoreAdapter: ScoreAdapter
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_hodnotenie, container, false)

        // Inicializácia RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView)
        scoreAdapter = ScoreAdapter()
        recyclerView.adapter = scoreAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Inicializácia ViewModelu a priradenie observera pre sledovanie zmeny skóre ked nastane zmena tak sa upraví na aktuálny stav tzn. že hráč ukončil danú hru.
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        viewModel.scoreList.observe(viewLifecycleOwner, { scores ->
            scoreAdapter.setScores(scores)
        })

        return view
    }

    // Adapter pre zobrazenie skóre v RecyclerView
    inner class ScoreAdapter : RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {
        private var scores: List<Score> = emptyList()

        // Vytvorenie ViewHoldera pre jednotlivý prvok skóre
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_score, parent, false)
            return ScoreViewHolder(view)
        }

        // Viazanie dát pre konkrétny prvok skóre
        override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
            val score = scores[position]
            holder.bind(score)
        }

        // Získanie počtu prvkov skóre
        override fun getItemCount(): Int {
            return scores.size
        }

        // Nastavenie nových skór a aktualizácia RecyclerView
        @SuppressLint("NotifyDataSetChanged")
        fun setScores(scoreList: List<Score>) {
            scores = scoreList
            notifyDataSetChanged()
        }

        /**
         * Táto časť kódu definuje vnorenú triedu ScoreViewHolder,
         * ktorá slúži na reprezentáciu a správu jedného prvku v RecyclerView adaptéri.
         * Je to viewHolder pre jednotlivý prvok skóre.
         */
        inner class ScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val difficultyTextView: TextView = itemView.findViewById(R.id.difficultyTextView)
            private val scoreTextView: TextView = itemView.findViewById(R.id.scoreTextView)

            // Aktualizuje datá skóre s prvkami rozloženia na aktualnu podobu
            fun bind(score: Score) {
                difficultyTextView.text = score.difficulty
                scoreTextView.text = score.score.toString()
            }
        }
    }
}
