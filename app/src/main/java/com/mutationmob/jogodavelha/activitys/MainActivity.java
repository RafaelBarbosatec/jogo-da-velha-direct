package com.mutationmob.jogodavelha.activitys;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mutationmob.jogodavelha.model.Jogada;
import com.mutationmob.jogodavelha.views.JogoDaVelhaView;
import com.mutationmob.jogodavelha.R;
import com.peak.salut.Callbacks.SalutCallback;
import com.peak.salut.Callbacks.SalutDataCallback;
import com.peak.salut.Callbacks.SalutDeviceCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutDevice;
import com.peak.salut.SalutServiceData;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SalutDataCallback , View.OnClickListener, JogoDaVelhaView.JogoDaVelhaListener {


    public static final String TAG = "Jogo da Velha";
    public SalutDataReceiver dataReceiver;
    public SalutServiceData serviceData;
    public Salut network;
    public Button bt_reiniciar;
    private SalutDataCallback callback;
    private boolean isHost = false;
    private SalutDevice device;
    private boolean is_reset = false;
    private ListView listViewServers;
    private String[] mLIstServer ;
    private LinearLayout ll_listservers;
    private ProgressBar progressBar;
    private ImageView img_legenda1,img_legenda2;

    JogoDaVelhaView jogo;
    JsonAdapter<Jogada> jsonAdapter;
    TextView tv_progress,tv_information,placar_xis,placar_bola;
    int vitorias_xis = 0,vitorias_bola = 0;
    private int jogador_inicia = JogoDaVelhaView.XIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.game_loading);
        tv_progress =(TextView)findViewById(R.id.text_prgress);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        ll_listservers = (LinearLayout)findViewById(R.id.ll_listservers);



        listViewServers = (ListView)findViewById(R.id.listServers);
        listViewServers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialogSelectServer(position);
            }
        });
      //  hostingBtn = (Button) findViewById(R.id.hosting_button);
        //discoverBtn = (Button) findViewById(R.id.discover_services);
        //send_msg = (Button) findViewById(R.id.send_msg);

       // hostingBtn.setOnClickListener(this);
       // discoverBtn.setOnClickListener(this);
        //send_msg.setOnClickListener(this);
        dataReceiver = new SalutDataReceiver(this, this);
        Moshi moshi = new Moshi.Builder().build();
        jsonAdapter = moshi.adapter(Jogada.class);

        /*Populate the details for our awesome service. */
        serviceData = new SalutServiceData("JogoDaVelha", 50489,
                "HOST");

        network = new Salut(dataReceiver, serviceData, new SalutCallback() {
            @Override
            public void call() {
                // wiFiFailureDiag.show();
                // OR
                Log.e(TAG, "Sorry, but this device does not support WiFi Direct.");
            }
        });



        Intent intent = getIntent();
        if(intent!=null){
            Bundle bundle = intent.getExtras();
            if(bundle != null) {
                isHost=bundle.getBoolean("host");
            }
        }

        if (isHost){
            setupNetwork();
        }else{
            discoverServices();
        }

        /**/


    }

    private void initGame(){
        setContentView(R.layout.activity_main);
        img_legenda1 = (ImageView)findViewById(R.id.img_legenda);
        img_legenda2 = (ImageView)findViewById(R.id.img_legenda2);
        jogo = (JogoDaVelhaView) findViewById(R.id.jogoDaVelha);
        jogo.setListener(this);
        bt_reiniciar = (Button)findViewById(R.id.bt_reiniciar);
        tv_information = (TextView)findViewById(R.id.tv_information);
        placar_xis = (TextView)findViewById(R.id.placar_xis);
        placar_bola = (TextView)findViewById(R.id.placar_bola);
        bt_reiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(jogador_inicia == JogoDaVelhaView.XIS){
                    jogador_inicia = JogoDaVelhaView.BOLA;
                    tv_information.setText(R.string.oponente_vez);
                    jogo.setAnable(false);
                }else{
                    jogador_inicia = JogoDaVelhaView.XIS;
                    tv_information.setText("Sua Vez!");
                    jogo.setAnable(true);
                }
                is_reset = true;
                jogo.reiniciarJogo(jogador_inicia);
                Jogada j = new Jogada();
                j.reiniciar = true;
                sendMensage(device,j,false);
                bt_reiniciar.setVisibility(View.GONE);
            }
        });

        if (isHost) {
            tv_information.setText("Sua Vez!");
            img_legenda1.setImageResource(R.drawable.x_mark);
            img_legenda2.setImageResource(R.drawable.o_mark);
        }else{
            img_legenda1.setImageResource(R.drawable.o_mark);
            img_legenda2.setImageResource(R.drawable.x_mark);
            tv_information.setText("Espere a vez de seu oponente");
        }




    }

    private void setupNetwork()
    {
        if(!network.isRunningAsHost)
        {
            tv_progress.setText("Criando game...");
            network.startNetworkService(new SalutDeviceCallback() {
                @Override
                public void call(SalutDevice salutDevice) {
                    //Toast.makeText(getApplicationContext(), "Device: " + salutDevice.instanceName + " connected.", Toast.LENGTH_SHORT).show();
                    device = salutDevice;
                    Log.i(TAG, "Registrou com: " + device.instanceName);
                    initGame();
                    jogo.setAnable(true);
                }
            }, new SalutCallback() {
                @Override
                public void call() {
                    tv_progress.setText("Esperando segundo jogador...");
                }
            }, new SalutCallback() {
                @Override
                public void call() {
                    Toast.makeText(MainActivity.this,"Erro ao criar Game",Toast.LENGTH_SHORT).show();
                    MainActivity.this.finish();
                }
            });

            isHost = true;

            /*hostingBtn.setText("Stop Service");
            discoverBtn.setAlpha(0.5f);
            discoverBtn.setClickable(false);*/
        }
        /*else
        {
            network.stopNetworkService(false);
            hostingBtn.setText("Start Service");
            discoverBtn.setAlpha(1f);
            discoverBtn.setClickable(true);
            isHost = false;
        }*/
    }

    private void discoverServices()
    {
        if(!network.isRunningAsHost && !network.isDiscovering)
        {
            tv_progress.setText("Procurando game...");
            network.discoverWithTimeout(new SalutCallback() {
                @Override
                public void call() {
                    Toast.makeText(getApplicationContext(), "Device: " + network.foundDevices.get(0).instanceName + " found.", Toast.LENGTH_SHORT).show();
                    if(network.foundDevices.size() == 1) {
                        register(network.foundDevices.get(0));
                        Log.i(MainActivity.TAG, "Name servidor: " + network.foundDevices.get(0).serviceName);
                    }else {

                        ll_listservers.setVisibility(View.VISIBLE);
                        tv_progress.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        mLIstServer = new String[network.foundDevices.size()];
                        for (int i = 0; i < network.foundDevices.size(); i++) {
                            Log.i(MainActivity.TAG, "Devices Encontrados: " + network.foundDevices.get(i).deviceName);
                            mLIstServer[i] = network.foundDevices.get(i).deviceName.toString();
                        }

                        Log.i(MainActivity.TAG, "Tamanho lista: " + mLIstServer.length+" "+mLIstServer[0]);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                                android.R.layout.simple_list_item_1, android.R.id.text1, mLIstServer);
                        listViewServers.setAdapter(adapter);
                    }
                }
            }, new SalutCallback() {
                @Override
                public void call() {
                    Toast.makeText(MainActivity.this,"Não foi possivel encontrar game, Tente novamente",Toast.LENGTH_SHORT).show();
                    MainActivity.this.finish();
                }
            }, 10000);
           /* discoverBtn.setText("Stop Discovery");
            hostingBtn.setAlpha(0.5f);
            hostingBtn.setClickable(false);*/
        }
        /*else
        {
            network.stopServiceDiscovery(true);
            discoverBtn.setText("Discover Services");
            hostingBtn.setAlpha(1f);
            hostingBtn.setClickable(false);
        }*/
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }*/

    /*Create a callback where we will actually process the data.*/
    @Override
    public void onDataReceived(Object data) {
        //Data Is Received
        Log.d(TAG, "Received network data.");
        Log.i(TAG,"Receive: "+((String) data).substring(1,data.toString().length()-1).replace("\\",""));
        try {
            Jogada j= jsonAdapter.fromJson(((String) data).substring(1,data.toString().length()-1).replace("\\",""));
            Log.d(TAG, "Object: "+j.coluna);

            if(j.reiniciar){

                if(jogador_inicia == JogoDaVelhaView.XIS){
                    jogador_inicia = JogoDaVelhaView.BOLA;
                    tv_information.setText("Sua Vez!");
                    jogo.setAnable(true);
                }else{
                    jogador_inicia = JogoDaVelhaView.XIS;
                    tv_information.setText("Espere a vez de seu oponente");
                    jogo.setAnable(false);
                }
                jogo.reiniciarJogo(jogador_inicia);

            }else {
                tv_information.setText("Sua Vez!");
                jogo.setJogada(j.linha, j.coluna);
                jogo.setAnable(true);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }




       /* try
        {
            Jogada newMenssagem = LoganSquare.parse(String.valueOf((Jogada)data), Jogada.class);
            Log.d(TAG, newMenssagem.description);  //See you on the other side!
            Toast.makeText(this, newMenssagem.description,Toast.LENGTH_SHORT).show();
            //Do other stuff with data.
        }
        catch (IOException ex)
        {
            Log.e(TAG, "Failed to parse network data.");
        }*/
        //Toast.makeText(this,o.toString(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

        if(!Salut.isWiFiEnabled(getApplicationContext()))
        {
            Toast.makeText(getApplicationContext(), "Please enable WiFi first.", Toast.LENGTH_SHORT).show();
            return;
        }

        /*if(v.getId() == R.id.hosting_button)
        {
            setupNetwork();
        }
        else if(v.getId() == R.id.discover_services)
        {
            discoverServices();
        }*/

        /*if(v.getId() == R.id.send_msg){
            if(isHost){
                sendMensage(device,false);
            }else
            sendMensage(null,true);
        }*/
    }

    private void register(final SalutDevice possibleHost){

        ll_listservers.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);
        tv_progress.setVisibility(View.VISIBLE);

        tv_progress.setText("Conectando-se a "+possibleHost.deviceName);

        Log.d(TAG, "Iniciou registro");
        network.registerWithHost(possibleHost, new SalutCallback() {
            @Override
            public void call() {
                Log.d(TAG, "We're now registered.");
                initGame();
                jogo.setVisibility(View.VISIBLE);
                jogo.setAnable(false);

            }
        }, new SalutCallback() {
            @Override
            public void call() {
                Toast.makeText(MainActivity.this,"Falha ao Registrar, ou fecharam o jogo",Toast.LENGTH_SHORT).show();
                finish();
                Log.d(TAG, "We failed to register.");
            }
        });

    }

    private void sendMensage(SalutDevice deviceToSendTo,Jogada jogada, boolean toHost) {

        String json = jsonAdapter.toJson(jogada);
        Log.i(TAG,"Json: "+json);

        if (toHost) {
            network.sendToHost(json, new SalutCallback() {
                @Override
                public void call() {
                    Log.e(TAG, "Oh no! The data failed to send.");
                }
            });
        } else {
            network.sendToDevice(deviceToSendTo,json, new SalutCallback() {
                @Override
                public void call() {
                    Log.e(TAG, "Oh no! The data failed to send.");
                }
            });
        }
        if(!is_reset)
        tv_information.setText("Espere a vez de seu oponente");

        is_reset = false;

    }

        @Override
        public void onDestroy () {
            super.onDestroy();

            if (isHost) {
                network.stopNetworkService(true);
//                network.cancelConnecting();

            }
            else {
                network.unregisterClient(false);
            }

        }


    @Override
    public void fimDeJogo(int vencedor) {
        Log.i(MainActivity.TAG,"Vencedor: "+vencedor);
        switch (vencedor) {
            case JogoDaVelhaView.XIS:
               if (isHost){
                   tv_information.setText("Você venceu!");
               }else{
                   tv_information.setText("Você perdeu!");
               }
                vitorias_xis++;
                placar_xis.setText(vitorias_xis+"");
                break;
            case JogoDaVelhaView.BOLA:
                if (isHost){
                    tv_information.setText("Você perdeu!");
                }else{
                    tv_information.setText("Você venceu!");
                }
                vitorias_bola++;
                placar_bola.setText(vitorias_bola+"");
                break;
            default:
                tv_information.setText("EMPATOU");
        }
        jogo.setAnable(false);

        if (isHost){
            bt_reiniciar.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void LocalJogada(int linha, int coluna) {
        Log.i(TAG,"Linha: "+linha+",Coluna: "+coluna);
        Jogada j = new Jogada();
        j.linha = linha;
        j.coluna = coluna;
        j.reiniciar = false;

        if(isHost){
            j.jogador = JogoDaVelhaView.XIS;
            sendMensage(device,j,false);
        }else {
            j.jogador = JogoDaVelhaView.BOLA;
            sendMensage(null,j, true);
        }

        jogo.setAnable(false);
    }

    public void dialogSelectServer(final int position){
        AlertDialog.Builder dialalogServers = new AlertDialog.Builder(MainActivity.this);
        dialalogServers.setCancelable(false);
        dialalogServers.setTitle("Atenção!");
        dialalogServers.setMessage("Deseja-se conectar com "+network.foundDevices.get(position).deviceName+" ?");
        dialalogServers.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                register(network.foundDevices.get(position));
            }
        });

        dialalogServers.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialalogServers.show();
    }
}
