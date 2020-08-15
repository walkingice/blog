package org.zeroxlab.recyclerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        RecyclerView list = (RecyclerView) findViewById(R.id.main_list);
        MyAdapter adapter = new MyAdapter();
        assignRandomNumber(adapter);
        list.setAdapter(adapter);
//        list.setLayoutManager(new LinearLayoutManager(this));
//        list.setLayoutManager(new GridLayoutManager(this, 2));
        list.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
    }

    private void assignRandomNumber (MyAdapter adapter) {
        for (int i = 0; i < 60; i++) {
            adapter.addData(new Integer((int)(Math.random() * 90) + 100));
        }
    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ArrayList<Integer> iList;

        MyAdapter() {
            iList = new ArrayList<>();
        }

        public void addData(Integer size) {
            iList.add(size);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Button view = new Button(parent.getContext());
            ViewGroup.LayoutParams params = new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(params);

            return new DummyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Integer size = iList.get(position);
            Log.d("Foo", "size:" + size);
            Button btn = (Button) holder.itemView;
            btn.setText("size: " + size);
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            params.width = size;
            params.height = size;
            holder.itemView.setLayoutParams(params);
        }

        @Override
        public int getItemCount() {
            return iList.size();
        }
    }

    class DummyViewHolder extends RecyclerView.ViewHolder {
        public DummyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
