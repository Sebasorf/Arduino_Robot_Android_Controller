package com.example.personal.controladorderobotarduino;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class GUIMovimiento extends ActionBarActivity {
    private static final int voice_recognition_request_code = 1234;
    private ImageButton ibtn1, ibtn2, ibtn3, ibtn4, ibtn5, ibtn6, ibtn7;
    private TextView tv5;
    private int velocidad;
    private AlertDialog.Builder ad, ad1;
    private boolean presionado = false;
    private Conexion con1;
    private Socket sock;
    private DataOutputStream out;
    private DataInputStream in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gui_movimiento);
        Intent i = getIntent();
        con1 = (Conexion) i.getSerializableExtra("Conexion");
        ibtn1 = (ImageButton) findViewById(R.id.ibtn1);
        ibtn2 = (ImageButton) findViewById(R.id.ibtn2);
        ibtn3 = (ImageButton) findViewById(R.id.ibtn3);
        ibtn4 = (ImageButton) findViewById(R.id.ibtn4);
        ibtn5 = (ImageButton) findViewById(R.id.ibtn5);
        ibtn6 = (ImageButton) findViewById(R.id.ibtn6);
        ibtn7 = (ImageButton) findViewById(R.id.ibtn7);
        tv5 = (TextView) findViewById(R.id.tv5);

        inicializarBotones();

        sock = new Socket();
        probarConexion();

        ad = new AlertDialog.Builder(this);
        ad.setMessage("No se puede conectar al Arduino");
        ad.setTitle("Error");
        ad.setPositiveButton("Aceptar", null);
        ad.create();

        ad1 = new AlertDialog.Builder(this);
        ad1.setTitle("Error");
        ad1.setCancelable(false);
        ad1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                iniciarReconocimiento();
            }
        });
        ad1.create();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_menuPrincipal) {
            Intent intent = new Intent(GUIMovimiento.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        if (id == R.id.action_ejecutarVoz) {
            iniciarReconocimiento();
        }
        if (id == R.id.action_vistaBotones) {
            return true;
        }
        if (id == R.id.action_vistaComandosVoz) {
            Intent intent = new Intent(GUIMovimiento.this, GUIInstrucciones.class);
            startActivity(intent);
        }
        if (id == R.id.action_salir) {
            finish();
            System.exit(0);
        }
        return super.onOptionsItemSelected(item);
    }

    public void inicializarBotones() {
        //Botón naranja que debería hacer marcha atrás
        this.ibtn7.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(marchaAtras()){
                    if(!actualizarVelocidad())
                        ad.show();
                }
                else
                    ad.show();
            }
        });

        //Botón verde que debería acelerar el robot
        this.ibtn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acelerarRobot()) {
                    if (!actualizarVelocidad())
                        ad.show();
                }
                else
                    ad.show();
            }
        });

        //Botón rojo que debería detener el robot
        this.ibtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (detenerRobot()) {
                    if (!actualizarVelocidad())
                        ad.show();
                }
                else
                    ad.show();
            }
        });

        //Flecha para adelante que debe aumentar la velocidad
        ibtn1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    presionar();
                    new Thread(new Runnable() {
                        public void run() {
                            while (presionado == true) {
                                if (aumentarVelocidad()) {
                                    if (!actualizarVelocidad())
                                        ad.show();
                                } else ad.show();
                            }
                        }
                    }).start();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    soltar();
                }
                return true;
            }
        });

        //Flecha para atrás que debe disminuir la velocidad
        ibtn2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    presionar();
                    new Thread(new Runnable() {
                        public void run() {
                            while (presionado == true) {
                                if (disminuirVelocidad()) {
                                    if (!actualizarVelocidad())
                                        ad.show();
                                } else ad.show();
                            }
                        }
                    }).start();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    soltar();
                }
                return true;
            }
        });
    }

    public void presionar() {
        this.presionado = true;
    }

    public void soltar() {
        this.presionado = false;
    }

    public boolean probarConexion() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            sock.connect(new InetSocketAddress(con1.getIp(), con1.getPort()), 2000);
            out = new DataOutputStream(sock.getOutputStream());
            in = new DataInputStream(sock.getInputStream());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean finalizarConexion(Socket sock) {
        try {
            sock.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void iniciarReconocimiento() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Diga un comando para mover el robot");
        startActivityForResult(intent, voice_recognition_request_code);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String comando = "";
        if (requestCode == voice_recognition_request_code && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            comando = matches.get(0);
        } else {
            switch (resultCode) {
                case (RESULT_CANCELED):
                    comando = "Result Canceled";
                    break;
                case (RecognizerIntent.RESULT_AUDIO_ERROR):
                    comando = "Audio error";
                    break;
                case (RecognizerIntent.RESULT_CLIENT_ERROR):
                    comando = "Client side error";
                    break;
                case (RecognizerIntent.RESULT_NETWORK_ERROR):
                    comando = "Network error";
                    break;
                case (RecognizerIntent.RESULT_NO_MATCH):
                    comando = "No match error";
                    break;
                case (RecognizerIntent.RESULT_SERVER_ERROR):
                    comando = "Server side error";
                    break;
            }
        }
        //if(comando.equals("cancelar") || resultCode==RESULT_CANCELED)
        //inicializarBotones();
        //else
        if (comando.equals("acelerar")) {
            if (!acelerarRobot())
                ad.show();
            //iniciarReconocimiento();
        }
        else if (comando.equals("frenar")) {
            if (!detenerRobot())
                ad.show();
            //iniciarReconocimiento();
        }
        else if (comando.equals("marcha atras") || comando.equals("marcha atrás")) {
            if (!marchaAtras())
                ad.show();
            //**************debe enviar el comando para ir marcha para atras***********
            //iniciarReconocimiento();
            //else if(comando.equals("izquierda"))
            //iniciarReconocimiento();
            //else if(comando.equals("derecha"))
            //iniciarReconocimiento();
            //else if(comando.equals("aumentar velocidad"))
            //iniciarReconocimiento();
            //else if(comando.equals("disminuir velocidad"))
            //iniciarReconocimiento();
        }
        else {
            ad1.setMessage("No se reconoce el comando de voz \"" + comando + "\", intente de nuevo");
            ad1.show();
        }
    }

    public boolean aumentarVelocidad() {
        if (sock.isConnected()) {
            try {
                //out.write("a".getBytes(StandardCharsets.UTF_8));
                out.write('a');
                return true;
            } catch (IOException e) {
                return false;
            }
        } else
            return false;
    }

    public boolean disminuirVelocidad() {
        if (sock.isConnected()) {
            try {
                //out.write("d".getBytes(StandardCharsets.UTF_8));
                out.write('d');
                return true;
            } catch (IOException e) {
                return false;
            }
        } else
            return false;
    }

    public boolean acelerarRobot() {
        if (sock.isConnected()) {
            try {
                //out.write("b".getBytes(StandardCharsets.UTF_8));
                out.write('b');
                return true;
            } catch (IOException e) {
                return false;
            }
        } else
            return false;
    }

    public boolean detenerRobot() {
        if (sock.isConnected()) {
            try {
                //out.write("p".getBytes(StandardCharsets.UTF_8));
                out.write('p');
                return true;
            } catch (IOException e) {
                return false;
            }
        } else
            return false;
    }

    public boolean marchaAtras() {
        if (sock.isConnected()) {
            try {
                out.write('r');
                //out.write("r".getBytes(StandardCharsets.UTF_8));
                return true;
            } catch (IOException e) {
                return false;
            }
        } else
            return false;
    }

    public boolean actualizarVelocidad() {
        //******************* Codigo para recibir la velocidad*******************

        /*tv5.post(new Runnable() {
            public void run() {
                tv5.setText(Integer.toString(velocidad));
            }
        });
        try{
            velocidad = in.readInt();
        }   catch(IOException e){
            return false;
        }
        */SystemClock.sleep(50);
        return true;
    }
}