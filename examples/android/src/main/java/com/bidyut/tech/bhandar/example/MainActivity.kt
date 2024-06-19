package com.bidyut.tech.bhandar.example

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bidyut.tech.bhandar.ReadResult
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        MainViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        val txtIp = findViewById<TextView>(R.id.txtIp)
        lifecycleScope.launch {
            viewModel.ipResult.collect { ipResult ->
                txtIp.text = when (ipResult) {
                    is ReadResult.Loading -> "Loading..."
                    is ReadResult.Data -> "IP: ${ipResult.data}"
                    is ReadResult.Error -> "Error: ${ipResult.errorMessage}"
                }
            }
        }

        findViewById<Button>(R.id.btnCacheOnly).setOnClickListener {
            viewModel.mode.value = MainViewModel.FetchMode.CACHE_ONLY
        }
        findViewById<Button>(R.id.btnCacheWithFallback).setOnClickListener {
            viewModel.mode.value = MainViewModel.FetchMode.CACHE_WITH_FALLBACK
        }
        findViewById<Button>(R.id.btnCacheWithRefresh).setOnClickListener {
            viewModel.mode.value = MainViewModel.FetchMode.CACHE_REFRESH
        }
        findViewById<Button>(R.id.btnNetworkWithCacheFallback).setOnClickListener {
            viewModel.mode.value = MainViewModel.FetchMode.FRESH
        }
    }
}
