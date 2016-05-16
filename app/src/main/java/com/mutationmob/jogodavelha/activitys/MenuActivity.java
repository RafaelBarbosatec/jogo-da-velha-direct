package com.mutationmob.jogodavelha.activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mutationmob.jogodavelha.Animations;
import com.mutationmob.jogodavelha.R;
import com.peak.salut.Salut;

public class MenuActivity extends AppCompatActivity {

    CountDownTimer tempoPageView;
    private LinearLayout ll_menu;
    private RelativeLayout rl_menu;
    private Button bt_criar,bt_buscar;
    private Animation animScalePop;
    private static final String PREF_NAME = "primeiroAcesso";
    private SharedPreferences sp;
    private boolean jaJogou = false;
    private ImageButton bt_help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        animScalePop = AnimationUtils.loadAnimation(this, R.anim.scale_pop);
        animScalePop.setDuration(500);
        sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        jaJogou = sp.getBoolean("primeiroAcesso",false);

        tempoPageView = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                setContentView(R.layout.activity_menu);
                setupView();
            }
        }.start();

    }

    private void dialogInstrucoes() {

    }

    private void setupView() {


        ll_menu = (LinearLayout)findViewById(R.id.ll_menu);
        rl_menu = (RelativeLayout)findViewById(R.id.rl_menu);
        bt_criar = (Button)findViewById(R.id.bt_criar);
        bt_buscar = (Button)findViewById(R.id.bt_buscar);
        bt_help = (ImageButton)findViewById(R.id.bt_help);
        bt_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInstrucoes();
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            bt_criar.startAnimation(animScalePop);
            bt_buscar.startAnimation(animScalePop);

        }

        if (!jaJogou){
            dialogInstrucoes();
        }

    }

    public void criarGame(View view){
        if(!Salut.isWiFiEnabled(getApplicationContext()))
        {
            Toast.makeText(getApplicationContext(), "Ative o WIFI primeiro!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent criar = new Intent(this,MainActivity.class);
        criar.putExtra("host",true);
        startActivity(criar);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("primeiroAcesso", false);
        editor.commit();
    }

    public void buscarGame(View view){
        if(!Salut.isWiFiEnabled(getApplicationContext()))
        {
            Toast.makeText(getApplicationContext(), "Please enable WiFi first.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent criar = new Intent(this,MainActivity.class);
        criar.putExtra("host",false);
        startActivity(criar);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("primeiroAcesso", false);
        editor.commit();
    }
}
