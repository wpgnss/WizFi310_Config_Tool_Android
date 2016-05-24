package wiznet.wizfi310_config_tool;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Daniel on 2016-05-23.
 */
public class c_JoinWizFi310Activity extends android.support.v4.app.Fragment {


    Context mContext;
    //shared preference
    SharedPreferences mPref;
    SharedPreferences.Editor editor;

    // wifi config
    WifiManager current_wifi_object = null;
    WifiConfiguration wifi_conf = null;

    TextView txt_result;
    Button button_join;

    String pref_ssid;
    String pref_pass;
//    boolean pref_result;
    // int netid;

    public c_JoinWizFi310Activity(Context context) { mContext = context; }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.step3_join_wizfi310_layout, null);

//        mPref = getActivity().getSharedPreferences("setting", 0);
//        editor= mPref.edit();

        //wifi
        current_wifi_object = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        wifi_conf = new WifiConfiguration();

        // get data
        getPreferencesData();

        button_join = (Button) view.findViewById(R.id.btn_JOIN);
        txt_result = (TextView) view.findViewById(R.id.txt_join_result);

        txt_result.setText("WizFi310 AP 에 접속을 시도합니다.\n\n" + pref_ssid);

        button_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get data
                getPreferencesData();

                int netid = set_wifi_config();
                join(netid);
                Toast.makeText(getActivity().getApplicationContext(), "Join WizFi310 AP ( " + pref_ssid + " )", Toast.LENGTH_LONG).show();

            }
        });


        return view;
    }

    public int set_wifi_config(){

        CharSequence password;
        password = pref_pass;
        String ssid = pref_ssid;
        WifiConfiguration wifi_conf = new WifiConfiguration();

        wifi_conf.SSID = String.format("\"%s\"", ssid);
        wifi_conf.preSharedKey = String.format("\"%s\"", password);
        current_wifi_object.disconnect();

        int id = current_wifi_object.addNetwork(wifi_conf);

        return id;
    }

    public void join(int netid) {

        current_wifi_object.disconnect();
        current_wifi_object.enableNetwork(netid, true);
        current_wifi_object.reconnect();

    }


    private void getPreferencesData() {

        mPref = getActivity().getSharedPreferences("setting", 0);
        editor= mPref.edit();

        pref_ssid = mPref.getString("wizfi310_ssid", "not found");
        pref_pass = mPref.getString("wizfi310_pass", "not found");
//        pref_result = mPref.getBoolean("find_result", false);

        editor.commit();
    }

}
