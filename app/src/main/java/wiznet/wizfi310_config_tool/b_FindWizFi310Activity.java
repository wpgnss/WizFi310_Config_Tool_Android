package wiznet.wizfi310_config_tool;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Daniel on 2016-05-23.
 */
public class b_FindWizFi310Activity extends android.support.v4.app.Fragment {

    Context mContext;

    //shared preference
    SharedPreferences mPref;
    SharedPreferences.Editor editor;

    // wifi config
    WifiManager current_wifi_object = null;
    List<ScanResult> mScanResult; // ScanResult List
    TextView textStatus;
    int scanCount = 0;
    String wizfi310_ssid = null;


    TextView txt_result;
    Button button_find;

    String pref_ssid;
    String pref_pass;

    public b_FindWizFi310Activity(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.step2_find_wizfi310_layout, null);

//        mPref = getActivity().getSharedPreferences("setting", 0);
//        editor= mPref.edit();

        //wifi
        current_wifi_object = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        //wifi enable
        if (!current_wifi_object.isWifiEnabled())
            current_wifi_object.setWifiEnabled(true);

        // get data
        getPreferencesData();

        button_find = (Button) view.findViewById(R.id.btn_FIND);
        txt_result = (TextView) view.findViewById(R.id.txt_find_result);

        txt_result.setText("설정된 SSID로 시작하는 AP를 찾습니다.\n\n");
        //txt_result.setText("%s 로 시작하는 WizFi310 SSID를 찾습니다.",pref_ssid );
        button_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getPreferencesData();

                initWIFIScan();
                Toast.makeText(getActivity().getApplicationContext(), "SCAN( " + pref_ssid + " )", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }



    private void getPreferencesData() {

        mPref = getActivity().getSharedPreferences("setting", 0);
        editor= mPref.edit();

        pref_ssid = mPref.getString("wizfi310_ssid_pre", "not found");
        pref_pass = mPref.getString("wizfi310_pass", "not found");

        editor.commit();
    }


    public void getWIFIScanResult() {

        mScanResult = current_wifi_object.getScanResults(); // ScanResult
        String ssid;

        for (int i = 0; i < mScanResult.size(); i++) {
            ScanResult result = mScanResult.get(i);
            String module_ssid = pref_ssid;

            ssid = result.SSID;
//            String bssid = result.BSSID;
//            int channelwidth = result.channelWidth;
//            txt_result.setText(ssid + " / " + bssid + " / " + channelwidth );

            if (ssid.startsWith(module_ssid)) {

                wizfi310_ssid = ssid;
                //save preference
                editor.putString("wizfi310_ssid", wizfi310_ssid);
//                editor.putBoolean("find_result", true);
                editor.commit();
                /////
                Toast.makeText(getActivity().getApplicationContext(), "FOUND ( " + ssid + " )", Toast.LENGTH_SHORT).show();


                try{
                    getActivity().unregisterReceiver(mReceiver); // stop WIFISCan
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
        if( scanCount++ > 5 ){
            Toast.makeText(getActivity().getApplicationContext(), "NOT FOUND", Toast.LENGTH_SHORT).show();

            try{
                getActivity().unregisterReceiver(mReceiver); // stop WIFISCan
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public void initWIFIScan() {
        // init WIFISCAN
        scanCount = 0;

//        editor.putBoolean("find_result", false);
        try{
            final IntentFilter filter = new IntentFilter(
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            getActivity().registerReceiver(mReceiver, filter);
            current_wifi_object.startScan();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                getWIFIScanResult(); // get WIFISCanResult
                current_wifi_object.startScan(); // for refresh
            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                getActivity().sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
            }
        }
    };

}
