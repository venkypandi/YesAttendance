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
    private var errorDialog: AlertDialog? = null
    private var errorDialogBinding: LayoutQrErrorBinding? = null
    private val scannerViewModel: ScannerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        createDialog()
        createErrorDialog()
        callResponseObserver()
        codeScanner = CodeScanner(requireActivity(), binding.scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            lifecycleScope.launch {
                if (it.text.length == 10 && TextUtils.isDigitsOnly(it.text)) {
                    scannerViewModel.getMemberData(it.text)
                } else {
                    errorDialogBinding?.tvErrorMessage?.text = getString(R.string.qr_error_message)
                    errorDialog?.show()
                }
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            errorDialogBinding?.tvErrorMessage?.text = it.message
            errorDialog?.show()
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
        checkDialogDismiss()
    }

    private fun checkDialogDismiss() {
        printDialog?.let { pDialog ->
            errorDialog?.let { eDialog ->
                if (!pDialog.isShowing && !eDialog.isShowing) {
                    codeScanner.startPreview()
                }
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
                        if (it.data != null) {
                            val data = it.data
                            dialogBinding?.let { dialog ->
                                dialog.tvNameText.text = data.memberName ?: "-"
                                dialog.tvTokenText.text = (data.tokenNumber ?: "-").toString()
                                dialog.tvTimeText.text = data.dateTime ?: "-"

                                dialog.btnClose.setOnClickListener {
                                    printDialog?.dismiss()
                                }
                                dialog.btnPrint.setOnClickListener {
                                    printDialog?.dismiss()
                                }
                            }
                            printDialog?.show()
                        } else {
                            errorDialogBinding?.tvErrorMessage?.text = getString(R.string.member_not_found)
                            errorDialog?.show()
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                    Status.ERROR -> {
                        it.message?.let { it1 ->
                            errorDialogBinding?.tvErrorMessage?.text = it1
                            errorDialog?.show()
                        }
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

    private fun createErrorDialog() {
        errorDialogBinding = LayoutQrErrorBinding.inflate(
            LayoutInflater.from(requireActivity())
        )
        errorDialog = AlertDialog.Builder(requireActivity(), R.style.dialog_style)
            .setView(errorDialogBinding?.root)
            .create()

        errorDialog?.setOnDismissListener {
            codeScanner.startPreview()
        }
    }

}