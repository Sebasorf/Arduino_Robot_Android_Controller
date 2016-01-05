package com.example.personal.controladorderobotarduino;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class GUIInstrucciones extends ActionBarActivity {
    private static final int voice_recognition_request_code = 1234;
    private TextView tv3, tv4;
    private int i;
    private AlertDialog.Builder ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gui_instrucciones);
        //tv3 = (TextView)findViewById(R.id.tv3);
        //tv4 = (TextView)findViewById(R.id.tv4);
        //tv4.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_menuPrincipal){
            Intent intent = new Intent(GUIInstrucciones.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        if (id == R.id.action_salir) {
            finish();
            System.exit(0);
        }
        if (id == R.id.action_vistaBotones) {
            finish();
        }
        if (id == R.id.action_vistaComandosVoz) {
            return true;
        }
        if (id == R.id.action_ejecutarVoz){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*public void inicializarBotones(){
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if(activities.size() != 0){
            ibtn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iniciarReconocimiento();
                }
            });
        }
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv3.setText("Cancelar, adelante, frenar, marcha atrás,\nderecha, izquierda, aumentar velocidad,\ndisminuir velocidad");
            }
        });
    } */
}
