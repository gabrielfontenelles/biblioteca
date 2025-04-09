package com.example.biblioteca

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.biblioteca.databinding.FragmentStatsBinding
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StatsFragment : Fragment() {
    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        loadStats()
    }

    private fun loadStats() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("books")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val books = snapshot.toObjects(BookModel::class.java)

                    // Atualizar total de livros
                    binding.tvTotalLivros.text = books.size.toString()

                    // Processar dados para os gráficos
                    updateGenreChart(books)
                    updateAuthorChart(books)
                }
        }
    }

    private fun updateGenreChart(books: List<BookModel>) {
        // Agrupar livros por gênero
        val genreCount = books.groupingBy { it.genero }.eachCount()

        // Criar entries para o gráfico
        val entries = genreCount.map { (genre, count) ->
            PieEntry(count.toFloat(), genre)
        }

        // Configurar o dataset
        val dataSet = PieDataSet(entries, "Gêneros").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }

        // Configurar o gráfico
        binding.chartGeneros.apply {
            data = PieData(dataSet)
            description.isEnabled = false
            legend.isEnabled = true
            setEntryLabelColor(Color.BLACK)
            animateY(1000)
            invalidate()
        }
    }

    private fun updateAuthorChart(books: List<BookModel>) {
        // Agrupar livros por autor
        val authorCount = books.groupingBy { it.autor }.eachCount()
            .toList()
            .sortedByDescending { it.second }
            .take(5) // Top 5 autores

        // Criar entries para o gráfico
        val entries = authorCount.mapIndexed { index, (author, count) ->
            BarEntry(index.toFloat(), count.toFloat())
        }

        // Configurar o dataset
        val dataSet = BarDataSet(entries, "Autores").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }

        // Configurar o gráfico
        binding.chartAutores.apply {
            data = BarData(dataSet)
            description.isEnabled = false
            legend.isEnabled = true
            xAxis.valueFormatter = IndexAxisValueFormatter(authorCount.map { it.first })
            xAxis.labelRotationAngle = 45f
            animateY(1000)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}