package com.example.bibliothequeapp

import android.os.Bundle
import android.webkit.URLUtil
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate // Import for theme switching
import android.content.Context // Import for SharedPreferences
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.NumberFormatException

class MainActivity : AppCompatActivity() {

    private lateinit var adapterLivres: AdapterLivres
    // The list that holds all our books
    private val livresList = mutableListOf<Livre>()

    // Variables for the input form fields
    private lateinit var titreInput: EditText
    private lateinit var prixInput: EditText
    private lateinit var urlInput: EditText
    private lateinit var availableCheckbox: CheckBox
    private lateinit var addButton: Button
    // Variable for Dark Mode Switch
    private lateinit var darkModeSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Load the saved theme preference before calling super.onCreate()
        val sharedPrefs = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("isDarkMode", false) // false is default (Light Mode)
        applyTheme(isDarkMode)


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Dummy data for initial display (L'Etranger, 1984, Le Petit Prince)
        livresList.add(Livre("L'Etranger", 150.0, "https://m.media-amazon.com/images/I/410291e60QL.jpg", true))
        livresList.add(Livre("1984", 180.0, "https://m.media-amazon.com/images/I/41y1Y-c7wGL.jpg", false))
        livresList.add(Livre("Le Petit Prince", 120.0, "https://m.media-amazon.com/images/I/51r26f1x4iL.jpg", true))


        // 1. Initialize all UI components from activity_main.xml
        titreInput = findViewById(R.id.titreInput)
        prixInput = findViewById(R.id.prixInput)
        urlInput = findViewById(R.id.urlInput)
        availableCheckbox = findViewById(R.id.disponibleCheckbox)
        addButton = findViewById(R.id.addButton)

        // Initialize and set the state of the Dark Mode switch
        darkModeSwitch = findViewById(R.id.darkModeSwitch)
        darkModeSwitch.isChecked = isDarkMode // Set the switch state based on the applied theme

        // Add its listener for dark/light mode toggle
        darkModeSwitch.setOnCheckedChangeListener { _, checked ->
            applyTheme(checked)
            // Save preference
            sharedPrefs.edit().putBoolean("isDarkMode", checked).apply()
            // Recreate the activity for theme change to take effect immediately
            recreate()
        }


        // 2. Setup RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        // We want the list to scroll vertically
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Create our adapter and link it to the list
        adapterLivres = AdapterLivres(livresList)
        recyclerView.adapter = adapterLivres

        // 7. Handle 'Ajouter' button click
        addButton.setOnClickListener {
            addNewLivre()
        }
    }

    // Theme application function
    private fun applyTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun addNewLivre() {
        // Get text from inputs and clean up extra spaces
        val titre = titreInput.text.toString().trim()
        val prixText = prixInput.text.toString().trim()
        val imageUrl = urlInput.text.toString().trim()
        val isDisponible = availableCheckbox.isChecked

        // 7. Check 1: All fields must not be empty
        if (titre.isEmpty() || prixText.isEmpty() || imageUrl.isEmpty()) {
            Toast.makeText(this, "Les trois zones de saisie ne doivent pas être vides.", Toast.LENGTH_LONG).show()
            return
        }

        // Try to convert the price text to a number (Double)
        val prix: Double
        try {
            prix = prixText.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Le prix doit être un nombre valide.", Toast.LENGTH_LONG).show()
            return
        }

        // 7. Check 2: Price must be greater than 0
        if (prix <= 0) {
            Toast.makeText(this, "Le prix doit être supérieur à 0 DH.", Toast.LENGTH_LONG).show()
            return
        }

        // 6. Check 3: Image URL must be valid
        if (!URLUtil.isValidUrl(imageUrl)) {
            Toast.makeText(this, "L'URL de l'image n'est pas valide.", Toast.LENGTH_LONG).show()
            return
        }

        // If all checks pass, create the new book object
        val newLivre = Livre(titre, prix, imageUrl, isDisponible)

        // Add it to the list and refresh the display
        adapterLivres.addBook(newLivre)

        // Clear the input fields for the next entry
        titreInput.text.clear()
        prixInput.text.clear()
        urlInput.text.clear()
        availableCheckbox.isChecked = false
        Toast.makeText(this, "Livre ajouté avec succès!", Toast.LENGTH_SHORT).show()
    }
}