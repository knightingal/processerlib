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

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.nanjing.knightingal.processerlib.beans.CounterBean;
import org.nanjing.knightingal.processerlib.view.ProcessBar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Knightingal
 * @since v1.0
 */

public class SubAdapter extends RecyclerView.Adapter<SubAdapter.ViewHolder>{

    private final static String TAG = "SubAdapter";


    private SubActivity subActivity;


    public SubAdapter(SubActivity subActivity) {
        super();
        this.subActivity = subActivity;
        counterBeanList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            counterBeanList.add(new CounterBean(i, 0, 0));
        }
    }

    public List<CounterBean> counterBeanList;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CounterBean counterBean = counterBeanList.get(position);
        holder.itemTv.setText(String.valueOf(counterBean.getIndex()) + " - ");
        holder.procTv.setText(String.valueOf(counterBean.getCurr()) + "/" + String.valueOf(counterBean.getMax()));
        holder.index = position;
        holder.localPosition = position;
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "click item " + position);
//                subActivity.asyncStartDownload(position);
//                Log.d(TAG, "click end");
//            }
//        });
        if (counterBean.getMax() > 0) {
            holder.processBar.setPercent(counterBean.getCurr() * 100 / counterBean.getMax());
            if (counterBean.getCurr() > 0 && counterBean.getCurr() < counterBean.getMax()) {
                holder.processBar.setVisibility(View.VISIBLE);
            } else {
                holder.processBar.setVisibility(View.INVISIBLE);
            }

        } else {
            holder.processBar.setPercent(0);
            holder.processBar.setVisibility(View.INVISIBLE);
        }
        holder.processBar.invalidate();
    }

    @Override
    public int getItemCount() {
        return counterBeanList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView itemTv;
        public TextView procTv;
        public int localPosition;

        public int index;
        public ProcessBar processBar;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setTag(this);
            itemTv = (TextView)itemView.findViewById(R.id.item_tv);
            procTv = (TextView)itemView.findViewById(R.id.proc_tv);
            processBar = (ProcessBar)itemView.findViewById(R.id.process_bar);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            processBar.setVisibility(View.VISIBLE);
            Log.d(TAG, "click item " + index);
            subActivity.asyncStartDownload(index);
            Log.d(TAG, "click end");
        }
    }
}
