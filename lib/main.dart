import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_blue_plus/flutter_blue_plus.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});
  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ElevatedButton(onPressed: () {}, child: const Text('Pair')),
            ElevatedButton(onPressed: () {}, child: const Text('Stream'))
          ],
        ),
      ),
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.title),
      ),
      floatingActionButton: const FloatingActionButton(
        onPressed: connectRFID,
        tooltip: 'Increment',
        child: Icon(Icons.rss_feed),
      ),
    );
  }
}

Future<void> connectRFID() async {
  MethodChannel channel = const MethodChannel('custom_rfid');
  try {
    //channel.invokeMethod('getDataFromFlutter');
    channel.setMethodCallHandler(_handleMethod);
  } on PlatformException catch (e) {
    log("Exception:${e.message}");
  }
}

Future<dynamic> _handleMethod(MethodCall call) async {
  switch (call.method) {
    case 'getRFIDFromNative':
      log("RFID DATA in Flutter:${call.arguments}");

      break;
    default:
      throw MissingPluginException('notImplemented');
  }
}

Future<void> startScanning() async {
  log("Connected Devices:${FlutterBluePlus.connectedDevices.toString()}");
  FlutterBluePlus.startScan(
    timeout: const Duration(seconds: 10),
    androidUsesFineLocation: true,
  );

  FlutterBluePlus.scanResults.listen((results) {
    for (ScanResult r in results) {
      log('${r.device.name} found! rssi: ${r.rssi}');
    }
  });
}
