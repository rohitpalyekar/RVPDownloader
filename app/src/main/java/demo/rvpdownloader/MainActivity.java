package demo.rvpdownloader;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    Button start,stop;
   private String url="NOURL";
    EditText urltext;
   Networkcheck net;
    private BroadcastReceiver broadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start=findViewById(R.id.start);
        stop=findViewById(R.id.stop);
        urltext= findViewById(R.id.texturl);
    }

    @Override
    protected void onResume() {
        super.onResume();
        net=new Networkcheck(this);
        if(broadcastReceiver==null){
            broadcastReceiver=new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    start.setEnabled(true);
                    Toast.makeText(MainActivity.this, intent.getExtras().get("downloadstatus").toString(), Toast.LENGTH_SHORT).show();
                }
            };
            registerReceiver(broadcastReceiver,new IntentFilter("downloadingcontent"));
        }
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url=urltext.getText().toString();
                if(URLUtil.isValidUrl(url)) {
                    if (!runtime_permission()) {
                        enableprocess();
                    }
                }else if(!net.networkcheck()){
                    Toast.makeText(MainActivity.this, "NetWork Error", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Invalid URL", Toast.LENGTH_SHORT).show();

                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),DownloadService.class);
                stopService(i);
                start.setEnabled(true);
            }
        });

    }
    private void enableprocess() {
        start.setEnabled(false);
        File folder = new File(Environment.getExternalStorageDirectory() + "/RVPbox");

        if (!folder.exists()) {
            folder.mkdir();
        }
        Intent i=new Intent(MainActivity.this,DownloadService.class);
        i.putExtra("url", url);
        startService(i);

    }

    private Boolean runtime_permission() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
            return  true;
        }
        return  false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableprocess();
                } else {
                    Toast.makeText(MainActivity.this, "Internal Storage Permission Required", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
    @Override
    protected void onDestroy() { //To disable Memory Leaks
        super.onDestroy();
        if (broadcastReceiver!=null){
            unregisterReceiver(broadcastReceiver);
        }
    }
}
