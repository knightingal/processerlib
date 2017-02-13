/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nanjing.knightingal.multdownloadprocess;

import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.nanjing.knightingal.processerlib.TaskNotifier;
import org.nanjing.knightingal.processerlib.tasks.AbsTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Knightingal
 * @since v1.0
 */

public class DLImageTask extends AsyncTask<DLFilePathBean, Void, Integer> {

    private AbsTask parentTask;

    private static final String TAG = "DLImageTask";

    private TaskNotifier taskNotifier;

    public DLImageTask(AbsTask parentTask, TaskNotifier taskNotifier) {
        this.parentTask = parentTask;
        this.taskNotifier = taskNotifier;
    }

    private DLFilePathBean dlFilePathBean;

    @Override
    protected Integer doInBackground(DLFilePathBean... dlFilePathBeen) {
        this.dlFilePathBean = dlFilePathBeen[0];
        downloadUrl(dlFilePathBeen[0].src, dlFilePathBeen[0].dest);

        return 0;
    }

    private void downloadUrl(String src, File dest)  {
        Log.d(TAG, "start download " + src);
        Request request = new Request.Builder().url(src).build();
        try {
            byte[] bytes = new OkHttpClient().newCall(request).execute().body().bytes();
            FileOutputStream fileOutputStream = new FileOutputStream(dest, true);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "end download " + dest.getAbsolutePath());
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        taskNotifier.onTaskComplete(parentTask, dlFilePathBean.index);
    }
}
