package br.com.coderfox.mercadopagopluginforandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import static android.app.Activity.RESULT_CANCELED;

/** MercadopagopluginforandroidPlugin */
public class MercadopagopluginforandroidPlugin implements MethodCallHandler {
  /** Plugin registration. */

  private MethodChannel channel;
  private Registrar registrar;
  private static final int REQUEST_CODE = 1000;

  private MercadopagopluginforandroidPlugin(MethodChannel channel, final Registrar registrar) {
    this.registrar = registrar;
    this.channel = channel;
  }

  /** Plugin registration. */
  public static void registerWith(final Registrar registrar) {
    MethodChannel channel = new MethodChannel(registrar.messenger(), "mercadopago");
    channel.setMethodCallHandler(new MercadopagoPlugin(channel, registrar));
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    switch (call.method) {
      case "startPayment":
        this.startPayment(call, result);
        break;
      default:
        result.notImplemented();
        break;
    }
  }

  private void startPayment(MethodCall call, final Result result) {
    final String public_key = call.argument("public_key").toString();
    final String preference = call.argument("preference_id").toString();
    new MercadoPagoCheckout.Builder(public_key, preference)
            .build()
            .startPayment(registrar.activeContext(), REQUEST_CODE);

    registrar.addActivityResultListener(new PluginRegistry.ActivityResultListener() {
      @Override
      public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
          if (resultCode == MercadoPagoCheckout.PAYMENT_RESULT_CODE) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
              String payment = JsonUtil.getInstance().toJson(bundle.get("EXTRA_PAYMENT_RESULT"));
              result.success(payment);
            }
          } else if (resultCode == RESULT_CANCELED) {
            if (data != null && data.getStringExtra("mercadoPagoError") != null) {
              MercadoPagoError error = JsonUtil.getInstance().fromJson(data.getStringExtra("mercadoPagoError"), MercadoPagoError.class);
              result.error(error.getMessage(), error.getErrorDetail(), error.getApiException());
            }
          }
        }
        return false;
      }
    });

  }
}
