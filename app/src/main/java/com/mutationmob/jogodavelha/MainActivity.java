package com.mutationmob.jogodavelha;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity implements SalutDataCallback , View.OnClickListener, JogoDaVelhaView.JogoDaVelhaListener{


    public static final String TAG = "Jogo da Velha";
    public SalutDataReceiver dataReceiver;
    public SalutServiceData serviceData;
    public Salut network;
    public Button hostingBtn;
    public Button discoverBtn;
    public Button send_msg;
    SalutDataCallback callback;
    private boolean isHost = false;
    private SalutDevice device;

    JogoDaVelhaView jogo;
    JsonAdapter<Jogada> jsonAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hostingBtn = (Button) findViewById(R.id.hosting_button);
        discoverBtn = (Button) findViewById(R.id.discover_services);
        //send_msg = (Button) findViewById(R.id.send_msg);

        hostingBtn.setOnClickListener(this);
        discoverBtn.setOnClickListener(this);
        //send_msg.setOnClickListener(this);
        dataReceiver = new SalutDataReceiver(this, this);


        /*Populate the details for our awesome service. */
        serviceData = new SalutServiceData("JogoDaVelha", 60606,
                "HOST");

        network = new Salut(dataReceiver, serviceData, new SalutCallback() {
            @Override
            public void call() {
                // wiFiFailureDiag.show();
                // OR
                Log.e(TAG, "Sorry, but this device does not support WiFi Direct.");
            }
        });
        Moshi moshi = new Moshi.Builder().build();
        jsonAdapter = moshi.adapter(Jogada.class);



        /*(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jogo.reiniciarJogo();
            }
        });*/

        jogo = (JogoDaVelhaView) findViewById(R.id.jogoDaVelha);
        jogo.setListener(this);

        jogo.setVisibility(View.INVISIBLE);



    }

    private void setupNetwork()
    {
        if(!network.isRunningAsHost)
        {
            network.startNetworkService(new SalutDeviceCallback() {
                @Override
                public void call(SalutDevice salutDevice) {
                    Toast.makeText(getApplicationContext(), "Device: " + salutDevice.instanceName + " connected.", Toast.LENGTH_SHORT).show();
                    device = salutDevice;
                    Log.i(TAG,"Registoru com: "+device.instanceName);
                    jogo.setVisibility(View.VISIBLE);
                    jogo.setAnable(true);
                }
            });
            isHost = true;

            hostingBtn.setText("Stop Service");
            discoverBtn.setAlpha(0.5f);
            discoverBtn.setClickable(false);
        }
        else
        {
            network.stopNetworkService(false);
            hostingBtn.setText("Start Service");
            discoverBtn.setAlpha(1f);
            discoverBtn.setClickable(true);
            isHost = false;
        }
    }

    private void discoverServices()
    {
        if(!network.isRunningAsHost && !network.isDiscovering)
        {
            network.discoverNetworkServices(new SalutCallback() {
                @Override
                public void call() {
                    Toast.makeText(getApplicationContext(), "Device: " + network.foundDevices.get(0).instanceName + " found.", Toast.LENGTH_SHORT).show();
                    register(network.foundDevices.get(0));
                }
            }, true);
            discoverBtn.setText("Stop Discovery");
            hostingBtn.setAlpha(0.5f);
            hostingBtn.setClickable(false);
        }
        else
        {
            network.stopServiceDiscovery(true);
            discoverBtn.setText("Discover Services");
            hostingBtn.setAlpha(1f);
            hostingBtn.setClickable(false);
        }
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
        Log.i(TAG,"Receive: "+data.toString());
        try {
            Jogada j= jsonAdapter.fromJson(data.toString());

            jogo.setJogada(j.linha,j.coluna);

            jogo.setAnable(true);
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

        if(v.getId() == R.id.hosting_button)
        {
            setupNetwork();
        }
        else if(v.getId() == R.id.discover_services)
        {
            discoverServices();
        }

        /*if(v.getId() == R.id.send_msg){
            if(isHost){
                sendMensage(device,false);
            }else
            sendMensage(null,true);
        }*/
    }

    private void register(final SalutDevice possibleHost){
        Log.d(TAG, "Iniciou registro");
        network.registerWithHost(possibleHost, new SalutCallback() {
            @Override
            public void call() {
                Log.d(TAG, "We're now registered.");
                jogo.setVisibility(View.VISIBLE);
                jogo.setAnable(false);

            }
        }, new SalutCallback() {
            @Override
            public void call() {
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
    }

        @Override
        public void onDestroy () {
            super.onDestroy();

            if (isHost)
                network.stopNetworkService(true);
            else
                network.unregisterClient(true);

        }


    @Override
    public void fimDeJogo(int vencedor) {
        String mensagem;
        switch (vencedor) {
            case JogoDaVelhaView.XIS:
                mensagem = "X venceu!";
                break;
            case JogoDaVelhaView.BOLA:
                mensagem = "O venceu!";
                break;
            default:
                mensagem = "Empatou!";
        }
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
        jogo.setAnable(false);
    }

    @Override
    public void LocalJogada(int linha, int coluna) {
        Log.i(TAG,"Linha: "+linha+",Coluna: "+coluna);
        Jogada j = new Jogada();
        j.linha = linha;
        j.coluna = coluna;

        if(isHost){
            j.jogador = JogoDaVelhaView.XIS;
            sendMensage(device,j,false);
        }else {
            j.jogador = JogoDaVelhaView.BOLA;
            sendMensage(null,j, true);
        }

        jogo.setAnable(false);
    }
}
