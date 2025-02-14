package bluen.homein.gayo_security.base;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerAdapter<E, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected List<E> mData;

    protected Context mContext;
    protected int mResource;

    protected abstract VH onCreateViewHolderBase(View view);

    protected abstract void onBindViewHolderBase(VH holder, int position);

    public BaseRecyclerAdapter(Context context, int resource) {
        super();

        mData = new ArrayList<>();

        mContext = context;
        mResource = resource;
    }

    public void addItems(List<E> items) {
        mData.addAll(items);

        notifyDataSetChanged();
    }

    public void setItems(List<E> items) {
        mData.clear();
        mData.addAll(items);

        notifyDataSetChanged();
    }

    public void clearItems() {
        mData.clear();

        notifyDataSetChanged();
    }

    public void onlyClearItems() {
        mData.clear();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mResource, parent, false);

        return onCreateViewHolderBase(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        onBindViewHolderBase(holder, position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}


