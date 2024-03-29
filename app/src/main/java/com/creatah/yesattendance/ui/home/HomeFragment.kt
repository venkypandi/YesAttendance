package com.creatah.yesattendance.ui.home

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.creatah.yesattendance.BuildConfig
import com.creatah.yesattendance.R
import com.creatah.yesattendance.data.model.ResponseModel
import com.creatah.yesattendance.databinding.FragmentHomeBinding
import com.creatah.yesattendance.databinding.LayoutPrintDialogBinding
import com.creatah.yesattendance.databinding.LayoutQrErrorBinding
import com.creatah.yesattendance.ui.scanner.ScannerViewModel
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraPermissionSetting: ActivityResultLauncher<Intent>
    private var bFlag = false
    private var cFlag = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.btnAddMember.setOnClickListener {
            val directions = HomeFragmentDirections.actionHomeFragmentToSearchFragment()
            findNavController().navigate(directions)
        }
        binding.btnScan.setOnClickListener {

//            requestCameraPermission.launch(
//                Manifest.permission.CAMERA
//            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.CAMERA
                    )
                )
            } else {
                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.CAMERA
                    )
                )
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                requestBluetooth.launch(enableBtIntent)
            }

//            Log.d("printer", "onCreateView: ${BluetoothPrintersConnections().list}")
//
//            val printer =
//                EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 48f, 32)
//            printer.printFormattedText(
//                "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
//                    printer,
//                    ResourcesCompat.getDrawableForDensity(
//                        resources,
//                        R.drawable.yes_logo,
//                        DisplayMetrics.DENSITY_MEDIUM,
//                        null
//                    )
//                ) + "</img>\n" +
//                        "[L]\n" +
//                        "[C]<u><font size='big'>Yes Connect</font></u>\n" +
//                        "[L]\n" +
//                        "[C]================================\n" +
//                        "[L]\n" +
//                        "[L]<b>Venkatesh Pandian</b>\n"
//
//            )
        }

        cameraPermissionSetting =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            }

        return binding.root
    }

    private var requestBluetooth =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val directions = HomeFragmentDirections.actionHomeFragmentToScannerFragment()
                findNavController().navigate(directions)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Allow bluetooth permission to print receipt",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            permissions.entries.forEach {

                if (it.key == Manifest.permission.CAMERA) {
                    cFlag = it.value
                    if (!cFlag) {
                        requestPermissions(it.key)
                        Toast.makeText(
                            requireContext(),
                            "Camera Permission Required",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                if (it.key == Manifest.permission.BLUETOOTH_SCAN) {
                    bFlag = it.value
                    if (!bFlag) {
                        requestPermissions(it.key)
                        Toast.makeText(
                            requireContext(),
                            "Allow bluetooth permission to print receipt",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            if (cFlag && bFlag) {
                val directions = HomeFragmentDirections.actionHomeFragmentToScannerFragment()
                findNavController().navigate(directions)
            }
        }


    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->

            if (granted) {

            } else {
                requestPermissions(Manifest.permission.CAMERA)
            }

        }

    private fun requestPermissions(permission: String) {
        val displayRational = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            permission
        )
        if (!displayRational) {

            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts(
                "package",
                BuildConfig.APPLICATION_ID, null
            )
            intent.data = uri
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            cameraPermissionSetting.launch(intent)
        } else {
            requestMultiplePermissions.launch(
                arrayOf(
                    permission
                )
            )
        }
    }


}