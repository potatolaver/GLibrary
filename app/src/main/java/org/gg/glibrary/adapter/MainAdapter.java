package org.gg.glibrary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qmuiteam.qmui.nestedScroll.QMUIContinuousNestedBottomRecyclerView;
import com.qmuiteam.qmui.nestedScroll.QMUIContinuousNestedTopRecyclerView;

import org.gg.glibrary.R;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private WeakReference<Context> context;

    public MainAdapter(Context context) {
        this.context = new WeakReference<>(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
                view = inflater.inflate(R.layout.item_recyclerview1, parent, false);
                holder = new ViewHolder1(view);
                break;
            case 1:
                view = inflater.inflate(R.layout.item_recyclerview2, parent, false);
                holder = new ViewHolder2(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class ViewHolder1 extends RecyclerView.ViewHolder {

        QMUIContinuousNestedTopRecyclerView rv;

        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            rv = (QMUIContinuousNestedTopRecyclerView) itemView;
            rv.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            SubAdapter1 adapter = new SubAdapter1();
            rv.setAdapter(adapter);
        }
    }

    static class ViewHolder2 extends RecyclerView.ViewHolder {

        QMUIContinuousNestedBottomRecyclerView rv;

        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
            rv = (QMUIContinuousNestedBottomRecyclerView) itemView;
            rv.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            SubAdapter1 adapter1 = new SubAdapter1();
            rv.setAdapter(adapter1);
        }
    }
}
