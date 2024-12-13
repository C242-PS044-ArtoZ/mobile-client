package com.c242_ps044.artoz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.c242_ps044.artoz.ListItem
import com.c242_ps044.artoz.R
import java.text.NumberFormat
import java.util.Locale

class ListAdapter(
    private val items: List<ListItem>,
    private val onDeleteClick: (ListItem) -> Unit // Callback untuk menangani klik tombol hapus
) : RecyclerView.Adapter<ListAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvType: TextView = itemView.findViewById(R.id.tv_type)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete) // Tombol hapus
        val ivTypeIcon: ImageView = itemView.findViewById(R.id.iv_type_icon) // Ikon jenis transaksi
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = items[position]
        holder.tvType.text = item.type
        holder.tvDescription.text = item.description

        // Format angka dengan Locale Indonesia
        val formattedAmount = NumberFormat.getNumberInstance(Locale("in", "ID")).format(item.amount)
        holder.tvAmount.text = "Rp. $formattedAmount"

        // Set ikon jenis transaksi
        val iconRes = if (item.type == "income") {
            R.drawable.income // Drawable untuk income
        } else {
            R.drawable.outcome // Drawable untuk outcome
        }
        holder.ivTypeIcon.setImageResource(iconRes)

        // Set listener pada tombol hapus
        holder.btnDelete.setOnClickListener {
            onDeleteClick(item) // Panggil callback untuk menghapus item
        }
    }

    override fun getItemCount() = items.size
}
