import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_blue_plus/flutter_blue_plus.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
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

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".

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
            ElevatedButton(onPressed: () {}, child: Text('Pair')),
            ElevatedButton(onPressed: () {}, child: Text('Stream'))
          ],
        ),
      ),
      appBar: AppBar(
        // TRY THIS: Try changing the color here to a specific color (to
        // Colors.amber, perhaps?) and trigger a hot reload to see the AppBar
        // change color while the other colors stay the same.
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        // Here we take the value from the MyHomePage object that was created by
        // the App.build method, and use it to set our appbar title.
        title: Text(widget.title),
      ),
      floatingActionButton: const FloatingActionButton(
        onPressed: connectRFID,
        tooltip: 'Increment',
        child: Icon(Icons.rss_feed),
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}

Future<void> connectRFID() async {
  MethodChannel channel = MethodChannel('custom_rfid');
  try {
    //channel.invokeMethod('getDataFromFlutter');
    channel.setMethodCallHandler(_handleMethod);
  } on PlatformException catch (e) {
    print("Exception:${e.message}");
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
  print("Connected Devices:${FlutterBluePlus.connectedDevices.toString()}");
  FlutterBluePlus.startScan(
    timeout: Duration(seconds: 10),
    androidUsesFineLocation: true,
  );

  FlutterBluePlus.scanResults.listen((results) {
    for (ScanResult r in results) {
      print('${r.device.name} found! rssi: ${r.rssi}');
    }
  });
}
