package com.exemplo.relogio;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Relógio digital simples para Wear OS.
 * Atualiza a hora a cada segundo usando Handler.
 */
public class MainActivity extends Activity {

    private TextView horaView;
    private TextView segundosView;
    private TextView dataView;

    private final SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat formatoSegundos = new SimpleDateFormat(":ss", Locale.getDefault());
    private final SimpleDateFormat formatoData = new SimpleDateFormat("EEE, dd MMM", new Locale("pt", "BR"));

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable atualizador = new Runnable() {
        @Override
        public void run() {
            atualizarHora();
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        horaView = findViewById(R.id.hora);
        segundosView = findViewById(R.id.segundos);
        dataView = findViewById(R.id.data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(atualizador);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(atualizador);
    }

    private void atualizarHora() {
        Date agora = new Date();
        horaView.setText(formatoHora.format(agora));
        segundosView.setText(formatoSegundos.format(agora));
        dataView.setText(formatoData.format(agora));
    }
}
