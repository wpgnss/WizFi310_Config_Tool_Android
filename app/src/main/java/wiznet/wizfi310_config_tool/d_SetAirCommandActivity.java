package wiznet.wizfi310_config_tool;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Daniel on 2016-05-23.
 */
public class d_SetAirCommandActivity extends android.support.v4.app.Fragment {


    Context mContext;

    //shared preference
    SharedPreferences mPref;
    SharedPreferences.Editor editor;

    TextView txt_result;
    Button button_set;
    ProgressBar bar_process;

    String pref_ap_ssid;
    String pref_ap_pass;

    String pref_aircmd_ip = null;
    String pref_aircmd_port = null;

    String pref_server_ip;
    String pref_server_port;

    Boolean pref_ssl_enable;
    Boolean pref_datamode_enable;

    String string_ssl_enable;
    String string_datamode_enable;

    public d_SetAirCommandActivity(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.step4_set_aircmd_layout, null);

//        mPref = getActivity().getSharedPreferences("setting", 0);
//        editor= mPref.edit();

        // get data
        getPreferencesData();

        button_set = (Button) view.findViewById(R.id.btn_SETAIR);
        txt_result = (TextView) view.findViewById(R.id.txt_set_result);
        bar_process = (ProgressBar) view.findViewById(R.id.bar_process);
        bar_process.setMax(5);

        txt_result.setText("아래 정보를 WizFi310에 설정합니다.\n\n" + pref_ap_ssid + "\n" + pref_ap_pass);

        button_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get data
                getPreferencesData();

                makeString();
                //make Thread
                new Thread(new WizFi310Client()).start();
            }
        });

        return view;
    }



    private void getPreferencesData() {
        mPref = getActivity().getSharedPreferences("setting", 0);
        editor= mPref.edit();

        pref_ap_ssid = mPref.getString("ap_ssid", "not found");
        pref_ap_pass = mPref.getString("ap_pass", "not found");
        pref_aircmd_ip = mPref.getString("wizfi310_aircmd_ip", "192.168.12.1");
        pref_aircmd_port = mPref.getString("wizfi310_aircmd_port", "5000");

        pref_server_ip = mPref.getString("server_ip", "192.168.12.1");
        pref_server_port = mPref.getString("server_port", "5000");
        pref_ssl_enable = mPref.getBoolean("ssl_enable", false);
        pref_datamode_enable = mPref.getBoolean("datamode_enable", false);

        editor.commit();

    }
    private void makeString() {

        if (pref_ssl_enable) {
            string_ssl_enable = "TCS";
        }
        else {
            string_ssl_enable = "TCN";
        }

        if (pref_datamode_enable) {
            string_datamode_enable = "1";
        } else {
            string_datamode_enable = "0";
        }
    }

    class WizFi310Client implements Runnable {

        String incomingMsg;
        String ip = pref_aircmd_ip;
        String port_s = pref_aircmd_port;
        int port = Integer.parseInt(port_s);

        String msg_wset = String.format("WizFi310AirCmd:AT+WSET=0,%s\r", pref_ap_ssid);
        String msg_wsec = String.format("WizFi310AirCmd:AT+WSEC=0,,%s\r", pref_ap_pass);
        String msg_wnet = String.format("WizFi310AirCmd:AT+WNET=1\r");
        String msg_scon = String.format("WizFi310AirCmd:AT+SCON=S,%s,%s,%s,,%s\r", string_ssl_enable, pref_server_ip, pref_server_port, string_datamode_enable);
        String msg_mprof = String.format("WizFi310AirCmd:AT+MPROF=S\r");
        String msg_mreset = String.format("WizFi310AirCmd:AT+MRESET\r");

        String msg = String.format("WizFi310AirCmd:AT+WSET=0,%s\rAT+WSEC=0,,%s\rAT+WNET=1\rAT+SCON=S,%s,%s,%s,,%s\rAT+MPROF=S\r", pref_ap_ssid, pref_ap_pass, string_ssl_enable, pref_server_ip, pref_server_port, string_datamode_enable);




        @Override
        public void run() {
            Socket socket = null;

            try {
                int prog_cnt = 1;
                Thread.sleep(500);
                socket = new Socket(ip, port);

                BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
                OutputStreamWriter osw = new OutputStreamWriter(bos, "US-ASCII");
                PrintWriter writer = new PrintWriter(osw);

                BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(bis, "US-ASCII"));

//                Thread.sleep(500);
                writer.print(msg_wset);
                writer.flush();

                while(true){
                    incomingMsg = reader.readLine();
                    Log.d("RCV msg", incomingMsg);
                    if(incomingMsg.contains("[OK]") == true){
                        bar_process.setProgress(prog_cnt++);
                        break;
                    }
                }

//                Thread.sleep(500);
                writer.print(msg_wsec);
                writer.flush();

                while(true){
                    incomingMsg = reader.readLine();

                    Log.d("RCV msg", incomingMsg);
                    if(incomingMsg.contains("[OK]") == true){
                        bar_process.setProgress(prog_cnt++);
                        break;
                    }
                }

//                Thread.sleep(500);
                writer.print(msg_wnet);
                writer.flush();

                while(true){
                    incomingMsg = reader.readLine();
                    Log.d("RCV msg", incomingMsg);
                    if(incomingMsg.contains("[OK]") == true){
                        bar_process.setProgress(prog_cnt++);
                        break;
                    }
                }

                Thread.sleep(500);
                writer.print(msg_scon);
                writer.flush();

                while(true){
                    incomingMsg = reader.readLine();
                    Log.d("RCV msg", incomingMsg);
                    if(incomingMsg.contains("[OK]") == true){
                        bar_process.setProgress(prog_cnt++);
                        break;
                    }
                }

                Thread.sleep(500);
                writer.print(msg_mprof);
                writer.flush();

                while(true){
                    incomingMsg = reader.readLine();
                    Log.d("RCV msg", incomingMsg);
                    if(incomingMsg.contains("[OK]") == true){
                        bar_process.setProgress(prog_cnt++);
                        break;
                    }
                }

                Thread.sleep(1000);
                writer.print(msg_mreset);
                writer.flush();

            } catch (Exception e) {
                Log.e("SockError", e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
