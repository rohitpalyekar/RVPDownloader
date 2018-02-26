package demo.rvpdownloader;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.webkit.URLUtil;

import java.io.FileOutputStream;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by sony on 26-02-2018.
 */

public class DownloadService extends Service {
    Disposable disposable;
    String value="NOURL";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!=null) {
            value = intent.getStringExtra("url");
            ondownload();
        }else{
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }
    

    private void ondownload() {
        if(URLUtil.isValidUrl(value)) {
            Observable<String> fetchdata = Observable.create(new ObservableOnSubscribe<String>() {
                @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String data = downloadFileSync(value);
                emitter.onNext(data);
                emitter.onComplete();
            }
        });

            disposable = fetchdata.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            Notify(s);
                        }
                    });
        }else{
            stopSelf();
        }
    }

    public String downloadFileSync(String downloadUrl) throws Exception {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(downloadUrl).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
         return "Fail";
        }
        FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/RVPbox/" + URLUtil.guessFileName(downloadUrl, null, null) );
        fos.write(response.body().bytes());
        fos.close();
            return "Success";

    }

    public void Notify(String data){
        String filename="Download "+URLUtil.guessFileName(value,null,null)+" "+data;

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.leonardkatana)
                        .setContentTitle("RVPbox")
                        .setContentText(filename);
        int NOTIFICATION_ID = 12345;

        Intent targetIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, builder.build());

        Intent i= new Intent("downloadingcontent"); //Service Broadcastname for filter
        i.putExtra("downloadstatus",filename);
        sendBroadcast(i);

        stopSelf();
    }

    @Override
    public void onDestroy() {//To disable Memory Leaks
        if (disposable!=null&&!disposable.isDisposed()) {
            disposable.dispose();
        }
        super.onDestroy();

    }
}
