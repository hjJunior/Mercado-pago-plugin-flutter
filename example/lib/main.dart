import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:mercadopagopluginforandroid/mercadopagopluginforandroid.dart';

void main() => runApp(new MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _boughtID = 'Start payment method';
  static const String PUBLIC_KEY = "YOUR_PUBLIC_KEY";
  static const String CHECKOUT_PREFERENCE_ID = "CHECKOUT_PREFENCE_ID";

  @override
  void initState() {
    super.initState();
  }

  Future<void> initPlatformState() async {
    String text;
    try {
      Map<String, dynamic> result = await Mercadopago.startPayment(PUBLIC_KEY, CHECKOUT_PREFERENCE_ID);
      text = "Bought ID: -> " + result["id"].toString();
    } on PlatformException {
      text = 'Failed to do the payment.';
    }

    if (!mounted) return;

    setState(() {
      _boughtID = text;
    });
  }

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new Scaffold(
        appBar: new AppBar(
          title: const Text('Mercado Pago'),
        ),
        body: InkWell(
          onTap: () {
            initPlatformState();
          },
          child: new Center(
            child: new Text(_boughtID),
          ),
        ),
      ),
    );
  }
}