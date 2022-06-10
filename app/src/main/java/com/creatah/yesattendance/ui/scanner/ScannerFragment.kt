package com.creatah.yesattendance.ui.scanner

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.creatah.yesattendance.databinding.FragmentScannerBinding
import kotlinx.coroutines.launch

class ScannerFragment : Fragment() {

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var codeScanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        codeScanner = CodeScanner(requireActivity(), binding.scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            lifecycleScope.launch {
                Toast.makeText(requireActivity(), it.text, Toast.LENGTH_LONG).show()
                Log.d("Scan Result: ", it.text)
                requireActivity().onBackPressed()
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            Toast.makeText(requireActivity(), it.message, Toast.LENGTH_LONG).show()
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

}