package com.example.haum;

import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

public class MainActivity extends AppCompatActivity{
    private TextView textbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textbox = findViewById(R.id.output);
    }

    public void lights_button_handler(View view) {
        String[] args = {"lights"};
        SSHRunner runner = new SSHRunner();
        runner.execute(args);
    }

    public void lamp_button_handler(View view) {
        String[] args = {"lamp"};
        SSHRunner runner = new SSHRunner();
        runner.execute(args);
    }

    public void goodmorning_button_handler(View view) {
        String[] args = {"goodmorning"};
        SSHRunner runner = new SSHRunner();
        runner.execute(args);
    }

    public void rgb_button_handler(View view) {
        String[] args = {"rgb"};
        SSHRunner runner = new SSHRunner();
        runner.execute(args);
    }

    public void monitor_button_handler(View view) {
        String[] args = {"monitor"};
        SSHRunner runner = new SSHRunner();
        runner.execute(args);
    }

    public void goodnight_button_handler(View view) {
        String[] args = {"goodnight"};
        SSHRunner runner = new SSHRunner();
        runner.execute(args);
    }

    public void desktop_button_handler(View view) {
        String[] args = {"desktop"};
        SSHRunner runner = new SSHRunner();
        runner.execute(args);
    }

    private String getChannelOutput(Channel channel, InputStream in) throws IOException{

        byte[] buffer = new byte[1024];
        StringBuilder strBuilder = new StringBuilder();

        String line = "";
        while (true){
            while (in.available() > 0) {
                int i = in.read(buffer, 0, 1024);
                if (i < 0) {
                    break;
                }
                strBuilder.append(new String(buffer, 0, i));
                System.out.println(line);
            }

            if(line.contains("logout")){
                break;
            }

            if (channel.isClosed()){
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ee){}
        }

        return strBuilder.toString();
    }

    private class SSHRunner extends AsyncTask<String, String, String>{
        private String resp;
        private String command;
        private String host;
        private String user;
        private String password;
        private int port;

        @Override
        protected String doInBackground(String... params) {
            // translate first string arg from alias to executable command on host end
            switch(params[0]){
                case "lights":
                    command = "python /home/projects/smart_home/lights.py";
                    break;
                case "rgb":
                    command = "python /home/projects/smart_home/rgb.py"; //CHANGE THIS EVENTUALLY
                    break;
                case "monitor":
                    command = "python /home/projects/smart_home/monitor.py";
                    break;
                case "lamp":
                    command = "python /home/projects/smart_home/lamp.py";
                    break;
                case "goodmorning":
                    command = "python /home/projects/smart_home/goodmorning.py";
                    break;
                case "goodnight":
                    command = "python /home/projects/smart_home/goodnight.py";
                    break;
                case "desktop":
                    command = "python /home/projects/smart_home/pconoff.py";
                    break;
            }
            try{
                host = "192.168.0.69";
                user = "pi";
                password = "shattered519";
                port = 22;
                Log.d("SSH", "Connect params for " + params[0] + " command set.");

                JSch jsch = new JSch();
                Session session = jsch.getSession(user, host, port);
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.setPassword(password);
                session.connect();
                Log.d("SSH", "Session connected.");

                Channel channel = session.openChannel("exec");
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                channel.setOutputStream(baos);
                ((ChannelExec)channel).setCommand(command);
                channel.setInputStream(null);
                ((ChannelExec)channel).setErrStream(System.err);

                InputStream input = channel.getInputStream();
                channel.connect();
                Log.d("SSH", "Channel connected to machine " + host + " server with command: " + command);

                /*try {
                    InputStreamReader inputReader = new InputStreamReader(input);
                    BufferedReader bufferedReader = new BufferedReader(inputReader);
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        textbox.setText(line);
                        Log.d("SSH", "line");
                    }
                    bufferedReader.close();
                    inputReader.close();
                } catch(IOException ex){
                    Log.e("SSH","Something went wrong 1");
                    ex.printStackTrace();
                }*/
                //String result = new String(baos.toByteArray());

                String result = getChannelOutput(channel, input);
                textbox.setText(result);
                channel.disconnect();
                session.disconnect();


            } catch (JSchException ex) {
                Log.e("SSH","Something went wrong 2");
                ex.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";

        }
    }
}
