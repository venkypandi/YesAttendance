package com.creatah.yesattendance.ui.search

import android.app.AlertDialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import com.creatah.yesattendance.R
import com.creatah.yesattendance.data.model.Member
import com.creatah.yesattendance.data.model.ResponseModel
import com.creatah.yesattendance.databinding.FragmentSearchBinding
import com.creatah.yesattendance.databinding.LayoutPrintDialogBinding
import com.creatah.yesattendance.databinding.LayoutQrErrorBinding
import com.creatah.yesattendance.ui.adapter.SearchListAdapter
import com.creatah.yesattendance.ui.scanner.ScannerViewModel
import com.creatah.yesattendance.utils.Status
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var printDialog: AlertDialog? = null
    private var dialogBinding: LayoutPrintDialogBinding? = null
    private var errorDialog: AlertDialog? = null
    private var errorDialogBinding: LayoutQrErrorBinding? = null
    private val scannerViewModel: ScannerViewModel by viewModels()
    private lateinit var adapter: SearchListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        createDialog()
        createErrorDialog()
        setObserver()
        scannerViewModel.getMemberList()
        adapter = SearchListAdapter { member: Member ->  listItemClicked(member) }
        return binding.root
    }

    private fun listItemClicked(data: Member) {
        data.qrValue?.let { scannerViewModel.getMemberData(it) }
    }

    private fun setObserver() {
        scannerViewModel.memberList.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it.status) {
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    Status.SUCCESS -> {
                        Log.d( "CallSuccess: ", it.data.toString())
                        if(!it.data?.error!!) {
                            val data = it.data
                            setMemberListAdapter(data.memberList)
                        } else {
                            errorDialogBinding?.tvErrorMessage?.text = "Data not found"
                            errorDialog?.show()
                        }
                        binding.progressBar.visibility = View.GONE

                    }
                    Status.ERROR -> {
                        it.message?.let { it1 ->
                            Log.d("Call2: ", it1)
                            errorDialogBinding?.tvErrorMessage?.text = it1
                            errorDialog?.show()
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
        scannerViewModel.responseValue.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it.status) {
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    Status.SUCCESS -> {
                        Log.d( "CallSuccess: ", it.data.toString())
                        if(!it.data?.error!!) {
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
                                    printReceipt(data)
                                }
                            }
                            printDialog?.show()
                        } else {
                            it.data.message?.let { it1 -> Log.d("Call1: ", it1) }
                            errorDialogBinding?.tvErrorMessage?.text = it.data.message ?: "Member not found"
                            errorDialog?.show()
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                    Status.ERROR -> {
                        it.message?.let { it1 ->
                            Log.d("Call2: ", it1)
                            errorDialogBinding?.tvErrorMessage?.text = it1
                            errorDialog?.show()
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setMemberListAdapter(memberList: List<Member>?) {
        binding.rvList.adapter = adapter
        if (memberList != null) {
            adapter.setMemberList(memberList)
        }
        if (adapter.itemCount <= 0) {
            errorDialogBinding?.tvErrorMessage?.text = "No list found"
            errorDialog?.show()
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
    }

    private fun createErrorDialog() {
        errorDialogBinding = LayoutQrErrorBinding.inflate(
            LayoutInflater.from(requireActivity())
        )
        errorDialog = AlertDialog.Builder(requireActivity(), R.style.dialog_style)
            .setView(errorDialogBinding?.root)
            .create()
    }

    private fun printReceipt(data: ResponseModel){
        val printer =
            EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 48f, 32)
        printer.printFormattedText(
            "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
                printer,
                ResourcesCompat.getDrawableForDensity(
                    resources,
                    R.drawable.yes_logo,
                    DisplayMetrics.DENSITY_MEDIUM,
                    null
                )
            ) + "</img>\n" +
                    "[L]\n" +
                    "[C]-------------------------\n" +
                    "[L]\n" +
                    "[C]<b>NAME</b>:${data.memberName}\n"+
                    "[C]<b>TOKEN NO.:</b>:${data.tokenNumber}\n"+
                    "[C]<b>TIME:</b>:${data.dateTime}\n"+
                    "[L]\n" +
                    "[C]-------------------------\n" +
                    "[C]*** Thank You ***\n"

        )
    }


}