package com.example.wordhunt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI

/**
 * Trieda MainActivity je hlavnou aktivitou aplikácie.
 * Jej úlohou je inicializovať a spravovať navigačný kontrolér, ako aj konfiguráciu App Baru pre navigáciu v rámci fragmentov.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Nastavenie navigačného kontroléra a konfigurácie App Baru
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        // Nastavenie App Baru s navigačným kontrollerom
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
    }

    /**
     * Táto metóda je volaná, keď používateľ klikne na tlačidlo späť v pravom hornom rohu aplikácie (šipka).
     * Kontroluje, či je aktuálna destinácia endGameFragment, kde je navigácia späť zakázaná.
     * Ak je to endGameFragment, metóda vráti false, čím sa zakáže navigácia späť.
     * V opačnom prípade sa vykoná navigácia späť pomocou navigačného kontroléra.
     */
    override fun onSupportNavigateUp(): Boolean {
        val currentDestination = findNavController(R.id.nav_host_fragment).currentDestination
        // Skontroluje, či je aktuálna destinácia endGameFragment, kde je navigácia späť zakázaná
        if (currentDestination?.id == R.id.endGameFragment) {
            // Navigácia späť je zakázaná, takže sa vráti hodnota false
            return false
        }
        // Navigácia späť je povolená, vykoná sa navigácia pomocou navigačného kontrollera
        return findNavController(R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp()
    }


}
