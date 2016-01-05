package com.example.personal.controladorderobotarduino;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class MainActivity extends ActionBarActivity{

    private Button btn1, btn2;
    private EditText et1, et2;
    private Conexion con1;
    private AlertDialog.Builder ad, ad3;

    private Socket sock;
    private DataOutputStream out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1 = (Button)findViewById(R.id.btn1);
        btn2 = (Button)findViewById(R.id.btn2);
        et1 = (EditText) findViewById(R.id.et1);
        et2 = (EditText) findViewById(R.id.et2);
        inicializarBotones();
        sock=new Socket();

        ad = new AlertDialog.Builder(this);
        ad.setMessage("No se puede conectar al Arduino");
        ad.setTitle("Error");
        ad.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btn1.setClickable(true);
                btn2.setClickable(true);
            }
        });
        ad.create();

        ad3=new AlertDialog.Builder(this);
        ad3.setMessage("El dispositivo conectado no es compatible");
        ad3.setTitle("Error");
        ad3.setPositiveButton("Aceptar", null);
        ad3.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_salir) {
            finish();
            System.exit(0);
        }
        if (id == R.id.action_vistaBotones) {
            return true;
        }
        if (id == R.id.action_vistaComandosVoz) {
            return true;
        }
        if (id == R.id.action_ejecutarVoz) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void inicializarBotones(){
        //Botón confirmar
        btn2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Desactivo los botones para no apretarlos muchas veces en una excepción
                btn2.setClickable(false);
                btn1.setClickable(false);
                String ip = et1.getText().toString();
                String puerto = et2.getText().toString();
                    if(correspondeAIP()) {
                        if(ip.trim().length()==0)
                            if(puerto.trim().length()==0)
                                con1 = new Conexion();
                            else con1 = new Conexion(Integer.parseInt(puerto));
                        //Acá abajo el puerto no puede quedar vacio
                        else    con1 = new Conexion(ip, Integer.parseInt(puerto));
                        if (!probarConexion())
                            ad.show();
                        else if (verificarCompatibilidad())
                             {
                                Intent intent = new Intent(MainActivity.this, GUIMovimiento.class);
                                intent.putExtra("Conexion", con1);
                                startActivity(intent);
                                finish();
                             } else  ad3.show();
                    }
            }
        });
        //Botón cancelar
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
    }

    public boolean correspondeAIP(){
        //Debería validar una dirección ip
        return true;
    }

    public boolean probarConexion(){
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            sock.connect(new InetSocketAddress(con1.getIp(), con1.getPort()), 2000);
            out = new DataOutputStream(sock.getOutputStream());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /* Este es el original funcional
    *******
    public boolean verificarCompatibilidad(){
        try {
            String cadena="m";
            out.write(cadena.getBytes(StandardCharsets.UTF_8));
            //Acá debería recibir una U
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    */
    public boolean verificarCompatibilidad(){
        try {
            out.write('m');
            //Acá debería recibir una U
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
