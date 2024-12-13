package com.c242_ps044.artoz

data class ListItem(
    val id: String,
    val type: String, // Pemasukan / Pengeluaran
    val description: String,
    val amount: Int
)
