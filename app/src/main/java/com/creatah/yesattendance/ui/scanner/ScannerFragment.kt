package com.creatah.yesattendance.ui.scanner

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.creatah.yesattendance.R
import com.creatah.yesattendance.databinding.FragmentScannerBinding
import com.creatah.yesattendance.databinding.LayoutPrintDialogBinding
import com.creatah.yesattendance.databinding.LayoutQrErrorBinding
import com.creatah.yesattendance.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScannerFragment : Fragment() {

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var codeScanner: CodeScanner
    private var printDialog: AlertDialog? = null
    private var dialogBinding: LayoutPrintDialogBinding? = null
    private val scannerViewModel: ScannerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        createDialog()
        codeScanner = CodeScanner(requireActivity(), binding.scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            lifecycleScope.launch {
                if (it.text.length == 10 && TextUtils.isDigitsOnly(it.text)) {
                    scannerViewModel.getMemberData(it.text)
                } else {
                    showErrorDialog(getString(R.string.qr_error_message))
                }
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            Toast.makeText(requireActivity(), it.message, Toast.LENGTH_LONG).show()
        }
        return binding.root
    }

    private fun showErrorDialog(message: String) {
        val errorDialogBinding = LayoutQrErrorBinding.inflate(
            LayoutInflater.from(requireActivity())
        )
        val errorDialog = AlertDialog.Builder(requireActivity(), R.style.dialog_style)
            .setView(errorDialogBinding.root)
            .create()

        errorDialogBinding.tvErrorMessage.text = message
        errorDialog.setOnDismissListener {
            codeScanner.startPreview()
        }
        errorDialog.show()
    }

    override fun onStart() {
        super.onStart()
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        callResponseObserver()
    }

    override fun onResume() {
        super.onResume()
        checkDialogDismiss()
    }

    private fun checkDialogDismiss() {
        printDialog?.let {
            if (!it.isShowing) {
                codeScanner.startPreview()
            }
        }
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onDestroyView() {
        codeScanner.releaseResources()
        super.onDestroyView()
    }

    private fun callResponseObserver() {
        scannerViewModel.responseValue.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it.status) {
                    Status.LOADING -> {
                        codeScanner.releaseResources()
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    Status.SUCCESS -> {
                        val data = it.data
                        dialogBinding?.let { dialog ->
                            dialog.tvNameText.text = data?.memberName ?: "-"
                            dialog.tvTokenText.text = (data?.tokenNumber ?: "-").toString()
                            dialog.tvTimeText.text = data?.dateTime ?: "-"

                            dialog.btnClose.setOnClickListener {
                                printDialog?.dismiss()
                            }
                            dialog.btnPrint.setOnClickListener {
                                printDialog?.dismiss()
                            }
                        }
                        binding.progressBar.visibility = View.GONE
                        printDialog?.show()
                    }
                    Status.ERROR -> {
                        it.message?.let { it1 -> showErrorDialog(it1) }
                    }
                }
            }
        }
    }

    private fun createDialog() {
        dialogBinding = LayoutPrintDialogBinding.inflate(
            LayoutInflater.from(requireActivity())
        )
        printDialog = AlertDialog.Builder(requireActivity(), R.style.dialog_style)
            .setView(dialogBinding?.root)
            .setCancelable(false)
            .create()
        printDialog?.setOnDismissListener {
            codeScanner.startPreview()
        }
    }

}