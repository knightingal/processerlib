package org.nanjing.knightingal.multdownloadprocess;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.nanjing.knightingal.processerlib.beans.CounterBean;
import org.nanjing.knightingal.processerlib.Services.DownloadService;
import org.nanjing.knightingal.processerlib.RefreshListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements RefreshListener {

    @Override
    public void doRefreshView(CounterBean counterBean) {
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putSerializable("counter", new CounterBean(4, 0, 0));
        msg.setData(b);

        refreshHandler.sendMessage(msg);

    }

    Handler refreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int info = msg.getData().getInt("msg");
            CounterBean counterBean = (CounterBean)msg.getData().getSerializable("counter");
            tv2.setText(" " + counterBean.getIndex());
        }
    };


    @OnClick({R.id.downloadBtn})
    public void btnClicked(View v) {
        startActivity(new Intent(this, SubActivity.class));
    }
    @OnClick({R.id.tempBtn})
    public void btnClicked2(View v) {
        startActivity(new Intent(this, TempActivity.class));
    }

    @Bind(R.id.tv2)
    public TextView tv2;
    private final static String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        startService(new Intent(this, DownloadService.class));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
        downloadService.removeListener();
        downloadService = null;
        unbindService(conn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, DownloadService.class), conn, BIND_AUTO_CREATE);
    }

    public DownloadService downloadService = null;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            downloadService = ((DownloadService.LocalBinder) service).getService();
            downloadService.setRefreshListener(null, MainActivity.this);
//            downloadService.refreshHandler = refreshHandler;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
        }
    };


}
