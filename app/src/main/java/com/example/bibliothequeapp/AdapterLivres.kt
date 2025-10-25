package com.example.bibliothequeapp

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Used for loading images from URL

class AdapterLivres(private val livresList: MutableList<Livre>) :
    RecyclerView.Adapter<AdapterLivres.LivreViewHolder>() {

    // 4. Inner class to hold the views (like a container for livre_item.xml elements)
    class LivreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.titleTextView)
        val price: TextView = itemView.findViewById(R.id.priceTextView)
        val image: ImageView = itemView.findViewById(R.id.bookImageView)
        val available: CheckBox = itemView.findViewById(R.id.availableCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LivreViewHolder {
        // Inflate the custom layout (livre_item.xml)
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.livre_item, parent, false)
        return LivreViewHolder(view)
    }

    override fun onBindViewHolder(holder: LivreViewHolder, position: Int) {
        val currentLivre = livresList[position]

        // Put the data into the item views
        holder.title.text = currentLivre.titre
        holder.price.text = "Prix: ${String.format("%.2f", currentLivre.prix)} DH"
        holder.available.isChecked = currentLivre.disponible

        // Load image using Glide library (requires setup in build.gradle)
        Glide.with(holder.itemView.context)
            .load(currentLivre.imageUrl)
            .placeholder(R.drawable.ic_book_placeholder) // Use a temporary image if loading fails
            .into(holder.image)

        // 5. Set click listener to show dialog
        holder.itemView.setOnClickListener {
            showLivreDetailsDialog(holder.itemView.context, currentLivre)
        }
    }

    override fun getItemCount() = livresList.size

    // Function for adding a new book (called from MainActivity)
    fun addBook(livre: Livre) {
        livresList.add(0, livre) // Add to the top
        notifyItemInserted(0) // Tell the RecyclerView to refresh the first item
    }

    // 5. Function to display the book details in a dialog
    private fun showLivreDetailsDialog(context: Context, livre: Livre) {
        // Prepare the availability status with the required color
        val availabilityText = TextView(context).apply {
            text = if (livre.disponible) {
                setTextColor(Color.GREEN) // Green text for Disponible
                "Disponibilité : Disponible"
            } else {
                setTextColor(Color.RED) // Red text for Non disponible
                "Disponibilité : Non disponible"
            }
            textSize = 16f
            setPadding(40, 20, 40, 20) // Add padding for better look
        }

        val message = "Titre: ${livre.titre}\n" +
                "Prix: ${String.format("%.2f", livre.prix)} DH"

        AlertDialog.Builder(context)
            .setTitle("Détails du Livre")
            .setMessage(message)
            .setView(availabilityText)
            .setPositiveButton("OK", null)
            .show()
    }
}