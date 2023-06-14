package com.example.wordhunt

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import java.util.*

/**
 * Tento fragment je zodpovedný za spracovanie hry.
 * v tomto fragmente sa zobrazuje hracia plocha s mriežkou písmen, zoznam vybraných slov, obtiažnosť hry, skóre a časovač.
 * Hra končí keď uplinie časovač. Časovač je nastavení podľa obtiažnosťi : ľahká 3 min. Stredná a Ťažká 2 min.
 */
class GameFragment : Fragment() {
    // Deklarácia premenných
    private lateinit var difficultyText: TextView
    private lateinit var scoreText: TextView
    private lateinit var scoreMultiplierText: TextView
    private lateinit var gridContainer: GridLayout
    private lateinit var gridLetters: Array<Array<String>>
    private lateinit var wordListTextView: TextView
    private lateinit var selectedWords: List<String>
    private lateinit var timerText: TextView
    private lateinit var timer: CountDownTimer
    private var gridSize = 0
    private var timeRemaining = 0L
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_game, container, false)
        difficultyText = view.findViewById(R.id.difficulty_text)
        scoreText = view.findViewById(R.id.score_text)
        scoreMultiplierText = view.findViewById(R.id.score_multiplier_text)
        gridContainer = view.findViewById(R.id.gridLayout)
        wordListTextView = view.findViewById(R.id.word_list_text)
        timerText = view.findViewById(R.id.timer_text)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Zobrazenie informácií o obtiažnosti, skóre a násobku skóre a času ktorý zbýva
        super.onViewCreated(view, savedInstanceState)
        val difficulty = GameData.obtiaznost
        difficultyText.text = difficulty
        scoreText.text = GameData.score.toString()
        scoreMultiplierText.text = GameData.scoreMultiplier.toString() + "x"

        // Nastaví čas podľa obtiažnosťi
        val timerDuration = when (difficulty) {
            "Ľahká" -> 3 * 60 * 1000L // 3 minúty v milisekundách.
            "Stredná" -> 2 * 75 * 1000L // 2.5 minúty v milisekundách.
            "Ťažká" -> 2 * 60 * 1000L // 2 minúty v milisekundách.
            else -> 0L
        }
        startTimer(timerDuration)

        //Nastaví velkosť hry podľa obtiažnosťi. Tzn. "Ľahká 6 | Stredná 7 | Ťažká 8
        gridSize = when (difficulty) {
            "Ľahká" -> 6
            "Stredná" -> 7
            "Ťažká" -> 8
            else -> 3
        }
        gridLetters = Array(gridSize) { Array(gridSize) { "" } }
        // Tu sa zavolajú metódy na vytvorenie gridu a vytvorenia listu velkosť podľa obtiažnosťi zo 60 slov
        // Nasledné sa to tabulky vložia slová a náhodné písmena.
        createGrid()
        initializeWordList()
        placeWordsInGrid()
        enableWordSelection()
    }

    /**
     * Tu sa spustí metodá startTimer kde sa začne odpočet nášho timera.
     * Keď nastane  na časovači nula tak sa do sharedViewModel pošle správa addScore s ktorou bude pracovať HodnotenieFragment aby bol aktualizovaný keď hra skončí
     * Následne sa presunieme do EndGameFragmenu
     */
    private fun startTimer(duration: Long) {
        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                timerText.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                // Koniec timera časovač dosiahol 0 pošle sa správa do sharedViewModela a navigujeme sa do endGameFragmentu
                timerText.text = "00:00"
                sharedViewModel.addScore(GameData.obtiaznost, GameData.score)
                findNavController().navigate(R.id.action_gameFragment_to_endGameFragment)
            }
        }.start()
    }
    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    /**
     * Metóda initializeWordList() funguje na princípe že si podľa obtiažnosťi vyberie zo zonamu slov náhodné slová a vloží ich do wordListu
     */
    private fun initializeWordList() {
        val words = arrayOf(
            "SWIRL", "SHADE", "BRAVE", "TOAST", "RAZOR", "BUNNY", "HAPPY",
            "SNORE", "CLOUD", "FRESH", "AMBER", "EAGLE", "JOLLY", "CLICK",
            "OCEAN", "LAUGH", "TIGER", "MUNCH", "SHOUT", "SPARK", "FLUTE",
            "GREET", "PETAL", "PRISM", "SALSA", "ZEBRA", "FAIRY",
            "JEWEL", "SPICE", "TWEET", "DIZZY", "CREEK", "MAGMA", "CHIRP",
            "DREAM", "KARMA", "OLIVE", "RIVER", "SWOOP", "TASTY", "MAGIC",
            "FUZZY", "MAPLE", "BUBBLE", "CORAL", "HONEY", "MELON", "PEBBLE",
            "SHADOW", "SPROUT", "THUMP", "VELVET", "WHISK"
        )
        val wordCount = when (GameData.obtiaznost) {
            "Ľahká" -> 2
            "Stredná" -> 3
            "Ťažká" -> 4
            else -> 3
        }

        words.shuffle() // Zamieša slová a vyberie podľa množstva ktore je určené podľa obtiažnosťi

        selectedWords = words.take(wordCount).map { it.uppercase() } // uloží vybrané slová ktoré sú uppercase
        val wordListText = selectedWords.joinToString("\n")

        // Nastaví daný text
        wordListTextView.text = wordListText
    }

    private fun createGrid() {
        // Vyčistí grid
        gridContainer.removeAllViews()

        val gridSize = gridLetters.size

        // Vypočíta to velkosť každeho gridu na možnej ploch telefóna
        // Spolupráca s touto stránkou https://denofdevelopers.com/get-screen-sizes-of-the-android-device/
        val screenWidth = resources.displayMetrics.widthPixels
        val cellSize = screenWidth / gridSize

        // Vytvoríme grid cells a vložíme ich do kontajnera
        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                val cellTextView = TextView(requireContext())
                val layoutParams = GridLayout.LayoutParams().apply {
                    width = cellSize
                    height = cellSize
                    columnSpec = GridLayout.spec(j)
                    rowSpec = GridLayout.spec(i)
                }
                cellTextView.layoutParams = layoutParams
                cellTextView.text = ""  // Inicializuje grid cell s prázdnym textom
                cellTextView.textSize = 36f  // Nastavíme velkosť písmena
                cellTextView.gravity = Gravity.CENTER  // zarovná ho do stredu bunky
                cellTextView.setTextColor(Color.BLACK) // nastaví požadovanú barvu
                cellTextView.setBackgroundResource(R.drawable.grid_cell_background)  // Nastavíme pozadie danej bunky
                gridContainer.addView(cellTextView)
            }
        }
    }


    /**
     * Metóda PlaceWordsInGrid()slúži na vloženie daných slov ktoré boli vybrané vo initializeWordList() do daného tabulky
     * Môže ich tam vložiť 8 rôznymi smermi hore,dole vlavo, vpravo, diagonalne hore a pod
     */
    private fun placeWordsInGrid() {
        // zobrané slová ktoré boli vybrané v initializeWordList()
        val words = selectedWords
        val wordsToPlace = words.toMutableList()
        val gridSize = gridLetters.size
        val random = Random()
        // Smery
        val directions = arrayOf(
            intArrayOf(1, 0),      // Hore
            intArrayOf(0, -1),     // vlavo
            intArrayOf(0, 1),      // vpravo
            intArrayOf(-1, -1),    // diagonalne hore vlavo
            intArrayOf(-1, 1),     // diagonalne hore vpravo
            intArrayOf(1, -1),     // diagonalne dole vlavo
            intArrayOf(1, 1)       // diagonalne dole vpravo
        )
        // pokial nie su všetky slová dané von tak rob toto
        while (wordsToPlace.isNotEmpty()) {
            val word = wordsToPlace.random()
            val wordLength = word.length
            val maxAttempts = gridSize * gridSize // Maximalny počet pokusov aj keby to nemalo nastať
            var isValidPlacement = false  // Indikátor, či je umiestnenie slova platné
            var attempts = 0

            while (!isValidPlacement && attempts < maxAttempts) {
                val randomRow = random.nextInt(gridSize)
                val randomCol = random.nextInt(gridSize)
                val wordLetters = Array(wordLength) { "" }

                val shuffledDirections = directions.toList().shuffled(random).toTypedArray()
                val directionCount = directions.size
                var directionIndex = 0 // Index aktuálneho smeru

                var i = 0
                var isPlacementValid = true

                while (i < wordLength && isPlacementValid) {
                    val direction = shuffledDirections[directionIndex]

                    val row = randomRow + direction[0] * i // Riadok v mriežke pre aktuálne písmeno
                    val col = randomCol + direction[1] * i // Stĺpec v mriežke pre aktuálne písmeno

                    if (row < 0 || row >= gridSize || col < 0 || col >= gridSize ||
                        (gridLetters[row][col].isNotEmpty() && gridLetters[row][col] != word[i].toString())
                    ) {
                        // Umiestnenie je neplatné, resetuje sa proces umiestňovania pre celé slovo
                        directionIndex = (directionIndex + 1) % directionCount
                        i = 0
                        isPlacementValid = false
                        wordLetters.fill("") // Vynuluje sa pole pre písmená slova
                    } else {
                        wordLetters[i] = word[i].toString() // Uloží aktuálne písmeno do poľa písmen slova
                        i++
                    }

                    if (i == wordLength) {
                        // Cele slovo bolo vložené úspešne
                        isValidPlacement = true

                        // Umiestni slovo do mriežky
                        for (j in 0 until wordLength) {
                            val r = randomRow + direction[0] * j
                            val c = randomCol + direction[1] * j

                            gridLetters[r][c] = wordLetters[j] // Nastaví aktuálne písmeno v mriežke
                            val cellTextView = gridContainer.getChildAt(r * gridSize + c) as TextView
                            cellTextView.text = wordLetters[j] // Nastaví aktuálne písmeno v zobrazení mriežky
                        }

                        wordsToPlace.remove(word) // Odstráni slovo zo zoznamu
                    }
                }

                attempts++ // pokus ++
            }
        }

        // Zaplní ostatné prázdne grid cells náhodnými písmenami
        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                if (gridLetters[i][j].isEmpty()) {
                    val randomLetter = getRandomLetter()
                    gridLetters[i][j] = randomLetter
                    val cellTextView = gridContainer.getChildAt(i * gridSize + j) as TextView
                    cellTextView.text = randomLetter
                }
            }
        }


    }

    /**
     * Metóda getRandomLetter vráti nahodné slovo zo zoznamu
     */
    private fun getRandomLetter(): String {
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" // abeceda
        return alphabet.random().toString()
    }


    @SuppressLint("ClickableViewAccessibility")
    /**
     * Metóda enableWordSelection() je zodpovedná za povolenie výberu slov hráčom použítím prsta
     * Funguje na princípe že používateľ potiahne prsť po bunkách a výtvara tím slovo ak našiel dané slovo tak sa zvýrazní (zelenou farbou)
     * Ak nie tak sa vráti do pôvodného stavu ako neoznačená bunka.
     */
    private fun enableWordSelection() {
        val selectedCells = mutableListOf<TextView>() // Zoznam grid cells ktoré sú vybrané prstom používatela
        val foundWordCells = mutableListOf<TextView>() // Zoznam grid cells pre nájdené slová

        gridContainer.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val cellTextView = findCellTextView(event.x, event.y) // Nájde textové zobrazenie bunky na základe súradníc dotyku
                    cellTextView?.let {
                        it.setTextColor(Color.WHITE) // Nastaví farbu textu na bielu
                        it.setBackgroundResource(R.drawable.selected_cell_background) // Nastaví pozadie bunky na vybrané (modrá barva)
                        selectedCells.add(it) // Pridá bunku do zoznamu vybraných buniek
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val cellTextView = findCellTextView(event.x, event.y) // Nájde textové zobrazenie bunky na základe súradníc dotyku
                    cellTextView?.let {
                        if (!selectedCells.contains(it)) {
                            it.setTextColor(Color.WHITE) // Nastaví farbu textu na bielu
                            it.setBackgroundResource(R.drawable.selected_cell_background) // Nastaví pozadie bunky na vybrané (modrá barva)
                            selectedCells.add(it) // Pridá bunku do zoznamu vybraných buniek
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    val selectedWord = selectedCells.joinToString("") { it.text.toString() } // Zozbiera vybrané písmená a vytvorí z nich slovo
                    val wordListText = wordListTextView.text.toString() // Získa text zobrazenia zoznamu slov

                    if (selectedWord.isNotEmpty() && wordListText.contains(selectedWord)) {
                        val words = wordListText.split("\n") // Rozdelí text zoznamu slov na jednotlivé slová
                        if (words.contains(selectedWord)) {
                            val updatedWords = words.toMutableList() // Vytvorí zoznam slov ako zmeniteľný zoznam
                            updatedWords.remove(selectedWord) // Odstráni vybrané slovo zo zoznamu
                            val updatedWordListText = updatedWords.joinToString("\n") // Vytvorí text zobrazenia aktualizovaného zoznamu slov
                            wordListTextView.text = updatedWordListText // Nastaví text zobrazenia aktualizovaného zoznamu slov

                            val wordScore = 100 * GameData.scoreMultiplier // Vypočíta skóre pre slovo na základe násobiteľa skóre
                            GameData.score += wordScore.toInt() // Pripočíta skóre k celkovému skóre
                            scoreText.text = GameData.score.toString() // Aktualizuje zobrazenie skóre

                            // Tu sa spracovavajú bunky v selected cells keď tam je dané slovo tak sa zfarbia na zeleno a budú pridané do tabulky najdených
                            for (cell in selectedCells) {
                                cell.setTextColor(Color.BLACK) // Nastaví farbu textu na čiernu
                                if (!foundWordCells.contains(cell)) {
                                    cell.setBackgroundResource(R.drawable.found_word_background) // Nastaví pozadie bunky na pozadie pre nájdené slovo
                                    foundWordCells.add(cell) // Pridá bunku do zoznamu buniek pre nájdené slová
                                }
                            }
                        }
                    }

                    // Tu sa spracujú vybrané bunky ked hráče nenašiel dané slovo
                    for (cell in selectedCells) {
                        if (!foundWordCells.contains(cell)) {
                            cell.setTextColor(Color.BLACK) // Nastaví farbu textu na čiernu
                            cell.setBackgroundResource(R.drawable.grid_cell_background) // Nastaví pozadie bunky na pôvodné pozadie
                        } else {
                            cell.setTextColor(Color.BLACK) // Nastaví farbu textu na čiernu
                            cell.setBackgroundResource(R.drawable.found_word_background) // Nastaví pozadie bunky na pozadie pre nájdené slovo
                        }
                    }
                    selectedCells.clear() // Vyčistí zoznam vybraných buniek

                    // Skontroluje, či boli všetky slová nájdené
                    if (wordListTextView.text.isEmpty()) {
                        clearGridAndPlaceWords() // Zavolá sa metodá ktorá vyčisti plochu a vytvorí novú mriežku aj s novými slovami
                    }
                }
            }
            true
        }
    }

    /**
     * Metóda ktorá služí na vyčistí obrazovku a upraví čas podľa obtiažnosťi aj multiplier  a spustí upraveny časovač
     * nastaví nové údaje a vytvorí novy grid aj s ostatnými nastaveniamy hry
     */
    private fun clearGridAndPlaceWords() {
        // Vyčistí údaje v tabuľke
        gridLetters = Array(gridSize) { Array(gridSize) { "" } }

        // vyčistí views
        for (i in 0 until gridContainer.childCount) {
            val cellTextView = gridContainer.getChildAt(i) as TextView
            cellTextView.text = ""
        }

        // čas ktorý sa prída po najdený všetkych slov
        val timeToAdd = when (GameData.obtiaznost) {
            "Ľahká" -> 20 * 1000L // 20s
            "Stredná" -> 40 * 1000L // 40s
            "Ťažká" -> 60 * 1000L // 60s
            else -> 0L // Default
        }
        // Multiplier ktorý sa pridá podľa obtiažnosťi
        val newMultiplier = when(GameData.obtiaznost) {
            "Ľahká" -> GameData.scoreMultiplier + 0.1
            "Stredná" -> GameData.scoreMultiplier + 0.3
            "Ťažká" -> GameData.scoreMultiplier + 0.3
            else -> GameData.scoreMultiplier + 0.1// Default case
        }
        GameData.scoreMultiplier = newMultiplier // nastaví sa nový scoreMultiplier
        scoreMultiplierText.text = String.format("%.2fx", GameData.scoreMultiplier) // formátovaný na 2 desatinné miesta
        timer.cancel() // zastaví sa timer
        timeRemaining = timeRemaining.plus(timeToAdd) // prida si čas
        startTimer(timeRemaining) // spustí sa znova
        // Tu sa zavolajú znova metódy na vytvorenie grid tabulky
        createGrid()
        initializeWordList()
        placeWordsInGrid()
    }

    /**
     * Metóda findCellTextView slúži na vyhľadanie textového zobrazenia bunky v mriežke písmen na základe súradníc dotyku
     */
    private fun findCellTextView(x: Float, y: Float): TextView? {
        val buffer = -40 // Ako buffer slúži hodnota -40, veľkosť bufferu môže byť upravená podľa potreby . Ako sa toto číslo mení tak sú grid cells citlivé na dotyk
        for (i in 0 until gridContainer.childCount) {
            val childView = gridContainer.getChildAt(i) // Získa potomka mriežky na základe indexu
            if (childView is TextView) { // Skontroluje, či je potomok textovým zobrazením
                if (x >= childView.left - buffer && x <= childView.right + buffer &&
                    y >= childView.top - buffer && y <= childView.bottom + buffer
                ) {
                    return childView // Vráti textové zobrazenie bunky, ak suradnice sa nachádzajú v blízkosti tejto bunky
                }
            }
        }
        return null // Ak sa žiadne textové zobrazenie bunky nezhoduje s danými súradnicami, vráti null
    }

}
