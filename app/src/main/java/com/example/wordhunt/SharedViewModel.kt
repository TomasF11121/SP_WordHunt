package com.example.wordhunt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * SharedViewModel je ViewModel trieda, ktorá sa používa na zdieľanie dát medzi fragmentmi tzn. že keď hra skončí tak zavolá metódu addScore ktorá pridá záznam do recycleView v HodnotenieFragment.
 *
 */
class SharedViewModel : ViewModel() {
    val scoreList: MutableLiveData<List<Score>> = MutableLiveData()


    fun addScore(difficulty: String, score: Int) {
        val newScore = Score(difficulty, score)
        val updatedScoreList = scoreList.value?.toMutableList() ?: mutableListOf()
        updatedScoreList.add(newScore)
        scoreList.value = updatedScoreList
    }
}

