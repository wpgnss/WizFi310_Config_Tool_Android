package wiznet.wizfi310_config_tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Daniel on 2016-05-23.
 */
public class a_SetWizFi310Activity extends android.support.v4.app.Fragment  {

   // public class SetWizFi310Activity extends Fragment {

        Context mContext;

        SharedPreferences mPref;
        SharedPreferences.Editor editor;

        Button button_Save;
        EditText WizFi310_SSID;
        EditText WizFi310_PASS;

        EditText WizFi310_AIRCMD_IP;
        EditText WizFi310_AIRCMD_PORT;

        EditText AP_SSID;
        EditText AP_PASS;
        EditText SERVER_IP;
        EditText SERVER_PORT;
        CheckBox SSL_ENABLE;
        CheckBox DATAMODE_ENABLE;

        public a_SetWizFi310Activity(Context context) {
            mContext = context;
        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            View view;
            view = inflater.inflate(R.layout.step1_setting_layout, null);

//        mPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

            WizFi310_SSID = (EditText) view.findViewById(R.id.txt_wizfi310_ssid);
            WizFi310_PASS = (EditText) view.findViewById(R.id.txt_wizfi310_pass);

            WizFi310_AIRCMD_IP = (EditText) view.findViewById(R.id.txt_aircmd_ip);
            WizFi310_AIRCMD_PORT = (EditText) view.findViewById(R.id.txt_aircmd_port);

            AP_SSID = (EditText) view.findViewById(R.id.txt_apname_ssid);
            AP_PASS = (EditText) view.findViewById(R.id.txt_apname_pass);

            SERVER_IP = (EditText) view.findViewById(R.id.txt_dest_ip);
            SERVER_PORT = (EditText) view.findViewById(R.id.txt_dest_port);
            SSL_ENABLE = (CheckBox) view.findViewById(R.id.checkbox_ssl_enable);
            DATAMODE_ENABLE = (CheckBox) view.findViewById(R.id.checkbox_datamode_enable);

            mPref = getActivity().getSharedPreferences("setting", 0);
            editor= mPref.edit();


            button_Save = (Button) view.findViewById(R.id.btn_config);

            button_Save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String wizfi310_ssid = WizFi310_SSID.getText().toString();
                    String wizfi310_pass = WizFi310_PASS.getText().toString();

                    String wizfi310_aircmd_ip = WizFi310_AIRCMD_IP.getText().toString();
                    String wizfi310_aircmd_port = WizFi310_AIRCMD_PORT.getText().toString();

                    String ap_ssid = AP_SSID.getText().toString();
                    String ap_pass = AP_PASS.getText().toString();

                    String server_ip = SERVER_IP.getText().toString();
                    String sever_port = SERVER_PORT.getText().toString();

                    Boolean ssl_enable = SSL_ENABLE.isChecked();
                    Boolean datamode_enable = DATAMODE_ENABLE.isChecked();

                    editor.putString("wizfi310_ssid_pre", wizfi310_ssid);
                    editor.putString("wizfi310_pass", wizfi310_pass);
                    editor.putString("wizfi310_aircmd_ip", wizfi310_aircmd_ip);
                    editor.putString("wizfi310_aircmd_port", wizfi310_aircmd_port);
                    editor.putString("ap_ssid", ap_ssid);
                    editor.putString("ap_pass", ap_pass);
                    editor.putString("server_ip", server_ip);
                    editor.putString("server_port", sever_port);
                    editor.putBoolean("ssl_enable", ssl_enable);
                    editor.putBoolean("datamode_enable", datamode_enable);
                    editor.commit();

                    Toast.makeText(getActivity().getApplicationContext(), "Save information", Toast.LENGTH_LONG).show();

                }
            });

            getPreferencesData();

            return view;
        }


        private void getPreferencesData() {

            WizFi310_SSID.setText(mPref.getString("wizfi310_ssid_pre", "WizFi310_AP_"));
            WizFi310_PASS.setText(mPref.getString("wizfi310_pass", "123456789"));
            WizFi310_AIRCMD_IP.setText(mPref.getString("wizfi310_aircmd_ip", "192.168.12.1"));
            WizFi310_AIRCMD_PORT.setText(mPref.getString("wizfi310_aircmd_port", "50001"));
            AP_SSID.setText(mPref.getString("ap_ssid", "iptime"));
            AP_PASS.setText(mPref.getString("ap_pass", "123456789"));

            SERVER_IP.setText(mPref.getString("server_ip", "192.168.1.2"));
            SERVER_PORT.setText(mPref.getString("server_port", "5000"));
            SSL_ENABLE.setChecked(mPref.getBoolean("ssl_enable", false));
            DATAMODE_ENABLE.setChecked(mPref.getBoolean("datamode_enable", false));

        }


}
