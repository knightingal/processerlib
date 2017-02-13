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

import org.nanjing.knightingal.processerlib.RefreshListener;
import org.nanjing.knightingal.processerlib.Services.DownloadService;
import org.nanjing.knightingal.processerlib.beans.CounterBean;
import org.nanjing.knightingal.processerlib.view.ProcessBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TempActivity extends AppCompatActivity implements RefreshListener{
    private static final List<String> TYPE_LIST = new ArrayList<>();

    private static final String TAG = "TempActivity";

    static {
        TYPE_LIST.add("SubActivity");
    }

    @Bind(R.id.pb1)
    public ProcessBar pb1;
    @Bind(R.id.pb2)
    public ProcessBar pb2;
    @Bind(R.id.pb3)
    public ProcessBar pb3;
    @Bind(R.id.pb4)
    public ProcessBar pb4;
    @Bind(R.id.pb5)
    public ProcessBar pb5;

    List<ProcessBar> processBarList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        ButterKnife.bind(this);

        processBarList.add(pb1);
        processBarList.add(pb2);
        processBarList.add(pb3);
        processBarList.add(pb4);
        processBarList.add(pb5);
    }

    @Override
    public void doRefreshView(CounterBean counterBean) {

        Message msg = new Message();
        Bundle data = new Bundle();
        data.putSerializable("data", counterBean);
        msg.setData(data);
        refreshHandler.sendMessage(msg);
    }
    Handler refreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CounterBean counterBean = (CounterBean) msg.getData().getSerializable("data");
            refreshListItem(counterBean);
        }
    };
    private void refreshListItem(CounterBean counterBean) {
        int index = counterBean.getIndex();
        ProcessBar processBar = processBarList.get(index);
        processBar.setPercent(counterBean.getCurr() * 100 / counterBean.getMax());
        processBar.invalidate();



    }
    private DownloadService downloadService = null;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            downloadService = ((DownloadService.LocalBinder) service).getService();

            downloadService.setRefreshListener(TYPE_LIST, TempActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
        }
    };
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
}
