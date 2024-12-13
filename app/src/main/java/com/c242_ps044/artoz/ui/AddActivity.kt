package com.c242_ps044.artoz.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.c242_ps044.artoz.R
import com.c242_ps044.artoz.adapter.Option
import com.c242_ps044.artoz.adapter.OptionAdapter
import com.c242_ps044.artoz.data.remote.OcrRepository
import com.c242_ps044.artoz.data.remote.OcrService
import com.c242_ps044.artoz.data.remote.PreferenceManager
import com.c242_ps044.artoz.data.remote.RetrofitClient
import com.c242_ps044.artoz.data.remote.TransactionRepository
import com.c242_ps044.artoz.data.remote.TransactionRequest
import com.c242_ps044.artoz.data.remote.TransactionService
import com.c242_ps044.artoz.databinding.ActivityAddBinding
import com.c242_ps044.artoz.viewmodel.OcrViewModel
import com.c242_ps044.artoz.viewmodel.OcrViewModelFactory
import com.c242_ps044.artoz.viewmodel.TransactionViewModel
import com.c242_ps044.artoz.viewmodel.TransactionViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var ocrViewModel: OcrViewModel

    // Variabel untuk menyimpan deskripsi yang dipilih
    private var selectedDescription: String? = null

    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { processImageForOcr(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi ViewModel untuk transaksi
        val transactionService =
            RetrofitClient.provideMainService(this, TransactionService::class.java)
        val transactionRepository =
            TransactionRepository(transactionService, PreferenceManager(this))
        val transactionFactory = TransactionViewModelFactory(transactionRepository)
        transactionViewModel =
            ViewModelProvider(this, transactionFactory)[TransactionViewModel::class.java]

        // Inisialisasi ViewModel untuk OCR
        val ocrService = RetrofitClient.provideOcrService(this, OcrService::class.java)
        val ocrRepository = OcrRepository(ocrService)
        val ocrFactory = OcrViewModelFactory(ocrRepository)
        ocrViewModel = ViewModelProvider(this, ocrFactory)[OcrViewModel::class.java]

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.rbOutcome.isChecked = true
        setupOptions(expenseOptions)

        binding.rgType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbOutcome -> setupOptions(expenseOptions)
                R.id.rbIncome -> setupOptions(incomeOptions)
            }
        }

        binding.btnSave.setOnClickListener {
            val nominalText = binding.etAmount.text.toString()
            val nominal = nominalText.toDoubleOrNull()
            val type = if (binding.rbOutcome.isChecked) "expense" else "income"
            val description =
                selectedDescription ?: "Deskripsi tidak dipilih" // Gunakan pilihan atau default

            if (nominal != null) {
                val transactionRequest = TransactionRequest(nominal, type, description)
                transactionViewModel.storeTransaction(transactionRequest)
            } else {
                Toast.makeText(this, "Masukkan jumlah yang valid", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ibScan.setOnClickListener {
            openGallery()
        }

        binding.btnBack.setOnClickListener {
            finish() // Close the activity
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun setupOptions(options: List<Option>) {
        binding.rvOptions.layoutManager = GridLayoutManager(this, 4)
        binding.rvOptions.adapter = OptionAdapter(options) { selectedOption ->
            selectedDescription = selectedOption.label // Simpan deskripsi dari opsi yang dipilih
        }
    }

    private fun openGallery() {
        selectImageLauncher.launch("image/*")
    }

    private fun processImageForOcr(uri: Uri) {
        val tempFile = createTempFileFromUri(uri)
        tempFile?.let { file ->
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("image", file.name, requestBody)
            ocrViewModel.uploadImage(multipartBody)
        }
    }

    private fun createTempFileFromUri(uri: Uri): File? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("temp_image", ".jpg", cacheDir)
            inputStream?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal memproses file.", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun observeViewModel() {
        transactionViewModel.storeTransactionResult.observe(this) { transaction ->
            transaction?.let {
                Toast.makeText(this, "Transaksi berhasil disimpan!", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

        transactionViewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }

        ocrViewModel.ocrResult.observe(this) { response ->
            response?.let {
                binding.etAmount.setText(it.totalValues?.toDoubleOrNull()?.toString() ?: "0.0")
                Toast.makeText(this, "OCR Processed: ${it.totalValues}", Toast.LENGTH_SHORT).show()
            }
        }

        ocrViewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, "OCR Error: $it", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val expenseOptions = listOf(
        Option(R.drawable.baseline_fastfood_24, "Makan"),
        Option(R.drawable.baseline_nature_people_24, "Harian"),
        Option(R.drawable.baseline_traffic_24, "Lalu Lintas"),
        Option(R.drawable.baseline_people_24, "Sosial"),
        Option(R.drawable.baseline_card_giftcard_24, "Hadiah"),
        Option(R.drawable.baseline_signal_wifi_statusbar_connected_no_internet_4_24, "Kuota"),
        Option(R.drawable.shirt, "Pakaian"),
        Option(R.drawable.plane_inflight, "Rekreasi"),
        Option(R.drawable.sparkles, "Kecantikan"),
        Option(R.drawable.baseline_medication_24, "Medis"),
        Option(R.drawable.tax, "Pajak"),
        Option(R.drawable.school, "Pendidikan"),
        Option(R.drawable.baseline_family_restroom_24, "Keluarga"),
        Option(R.drawable.baseline_pets_24, "Peliharaan")
    )

    private val incomeOptions = listOf(
        Option(R.drawable.salary, "Upah"),
        Option(R.drawable.bonus, "Bonus"),
        Option(R.drawable.invest, "Investasi"),
        Option(R.drawable.parttime, "Part Time")
    )
}
