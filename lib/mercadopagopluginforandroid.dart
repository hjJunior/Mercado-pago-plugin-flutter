import 'dart:async';
import 'dart:convert';
import 'package:flutter/services.dart';

class Mercadopago {
  static const MethodChannel _channel = const MethodChannel('mercadopago');

  static Future<Map<String, dynamic>> startPayment(String publicKey, String preferenceId) async {
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("public_key", () => publicKey);
    args.putIfAbsent("preference_id", () => preferenceId);
    return json.decode(await _channel.invokeMethod('startPayment', args));
  }
}