package com.example.refid_test

import io.flutter.embedding.android.FlutterActivity

import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
class MainActivity: FlutterActivity() {

    private val CHANNEL = "custom_rfid"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "getDataFromFlutter") {
                getNativeData(flutterEngine) // Your method to fetch native data
//                if (data != null) {
//                    result.success(data)
//                } else {
//                    result.error("UNAVAILABLE", "Data not available.", null)
//                }
            } else if(call.method=="getRFIDFromNative"){
                result.success(call.arguments)
            }
        }
    }

    private fun getNativeData(flutterEngine: FlutterEngine){
        // Implement your native code logic here
        RfidOperations(this,flutterEngine,CHANNEL);

    }
}
