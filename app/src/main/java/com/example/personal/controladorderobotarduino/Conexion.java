package com.example.personal.controladorderobotarduino;

import android.os.StrictMode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;

import java.nio.charset.StandardCharsets;

/**
 * Created by Personal on 12/05/2015.
 */
public class Conexion implements Serializable{
    private String ip;
    private int port;

    private int velocidad;

    public Conexion(String ip, int port){
        this.ip=ip;
        this.port=port;
        this.velocidad=0;
    }
    public Conexion(String ip){
        this.ip=ip;
        this.port=8888;
        this.velocidad=0;
    }
    public Conexion(){
        //this.ip="192.168.1.52";
        this.ip="192.168.43.127";
        this.port=8888;
        this.velocidad=0;
    }
    public Conexion(int port){
        this.ip="192.168.1.52";
        this.port=port;
        this.velocidad=0;
    }

    public String getIp(){
        return this.ip;
    }

    public int getPort(){
        return this.port;
    }




}
