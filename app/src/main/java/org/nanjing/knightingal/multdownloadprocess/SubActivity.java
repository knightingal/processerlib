package org.nanjing.knightingal.multdownloadprocess;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.nanjing.knightingal.processerlib.beans.CounterBean;
import org.nanjing.knightingal.processerlib.Services.DownloadService;
import org.nanjing.knightingal.processerlib.RefreshListener;
import org.nanjing.knightingal.processerlib.tasks.SleepTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SubActivity extends AppCompatActivity implements RefreshListener {
    private static final List<String> TYPE_LIST = new ArrayList<>();

    private static final String TAG = "SubActivity";

    static {
        TYPE_LIST.add(TAG);
    }

    @Override
    public void doRefreshView(CounterBean counterBean) {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putSerializable("data", counterBean);
        msg.setData(data);
        refreshHandler.sendMessage(msg);
    }

    @Override
    protected void onPause() {
        super.onPause();
        downloadService.removeListener();
        downloadService = null;
        unbindService(conn);
    }

    private void refreshListItem(CounterBean counterBean) {
        SubAdapter.ViewHolder viewHolder = ((SubAdapter.ViewHolder)list11.findViewHolderForAdapterPosition(counterBean.getIndex()));
        if (viewHolder == null) {
            return;
        }

        subAdapter.counterBeanList.set(counterBean.getIndex(), counterBean);

        viewHolder.procTv.setText(String.valueOf(counterBean.getCurr()) + "/" + String.valueOf(counterBean.getMax()));
        viewHolder.processBar.setPercent(counterBean.getCurr() * 100 / counterBean.getMax());
        viewHolder.processBar.invalidate();



    }

    Handler refreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CounterBean counterBean = (CounterBean) msg.getData().getSerializable("data");
            refreshListItem(counterBean);
        }
    };



    private DownloadService downloadService = null;

    @Bind(R.id.list11)
    public RecyclerView list11;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            downloadService = ((DownloadService.LocalBinder) service).getService();

            downloadService.setRefreshListener(TYPE_LIST, SubActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, DownloadService.class), conn, BIND_AUTO_CREATE);
    }


    private RecyclerView.LayoutManager mLayoutManager;
    private SubAdapter subAdapter;
    public void asyncStartDownload(int index) {

//        DLAlbumTask dlAlbumTask = new DLAlbumTask(this);
//        dlAlbumTask.setTaskNotifier(downloadService);

        SleepTask sleepTask = new SleepTask(downloadService);
        downloadService.asyncStartDownload(sleepTask, index);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        ButterKnife.bind(this);

        list11.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        list11.setLayoutManager(mLayoutManager);

        subAdapter = new SubAdapter(this);
//        subAdapter.setDataArray(dataArray);
        list11.setAdapter(subAdapter);



    }
}
