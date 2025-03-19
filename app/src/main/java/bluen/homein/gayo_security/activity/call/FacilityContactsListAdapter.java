package bluen.homein.gayo_security.activity.call;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import bluen.homein.gayo_security.R;
import bluen.homein.gayo_security.base.BaseRecyclerAdapter;
import bluen.homein.gayo_security.publicAdapter.PageNumberListAdapter;
import bluen.homein.gayo_security.rest.ResponseDataFormat;

public class FacilityContactsListAdapter extends BaseRecyclerAdapter<ResponseDataFormat.FacilityContactListBody.FacilityContactInfo, FacilityContactsListAdapter.ViewHolder> {
    private OnContactsClickListener listener;  // 콜백 인터페이스 변수

    public FacilityContactsListAdapter(Context context, int resource, OnContactsClickListener listener) {
        super(context, resource);
        this.listener = listener;
    }

    public interface OnContactsClickListener {
        void clickContacts(ResponseDataFormat.FacilityContactListBody.FacilityContactInfo facilityContactInfo);
    }

    @Override
    protected ViewHolder onCreateViewHolderBase(View view) {
        return new ViewHolder(view);
    }

    public interface OnPageNumberClickListener {
        void clickPageNumber(int pageNumber);
    }

    @Override
    protected void onBindViewHolderBase(ViewHolder holder, int position) {

        holder.tvFacilityName.setText(mData.get(position).getFacilityName());

        holder.layItem.setOnClickListener(view -> {
            listener.clickContacts(mData.get(position));
        });
    }

    // Recycler View Holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layItem;
        TextView tvFacilityName;

        ViewHolder(@NonNull View v) {
            super(v);
            layItem = v.findViewById(R.id.lay_item);
            tvFacilityName = v.findViewById(R.id.tv_facility_name);

        }
    }

}
