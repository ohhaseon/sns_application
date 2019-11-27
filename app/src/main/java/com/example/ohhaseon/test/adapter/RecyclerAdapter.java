package com.example.ohhaseon.test.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ohhaseon.test.R;
import com.example.ohhaseon.test.item.RecyclerItem;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    ArrayList<RecyclerItem> mItems;

    public RecyclerAdapter(ArrayList<RecyclerItem> items){
        mItems = items;
    }


    // 새로운 뷰 홀더 생성
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view,parent,false);
        return new ItemViewHolder(view);
    }


    // View 의 내용을 해당 포지션의 데이터로 바꿉니다.
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.mNickTv.setText(mItems.get(position).getNick());
        holder.mCommentTv.setText(mItems.get(position).getComment());
        holder.mDateTv.setText(mItems.get(position).getDate());
    }

    // 데이터 셋의 크기를 리턴해줍니다.
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    // 커스텀 뷰홀더
// item layout 에 존재하는 위젯들을 바인딩합니다.
    class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView mNickTv;
        private TextView mCommentTv;
        private TextView mDateTv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mNickTv = (TextView) itemView.findViewById(R.id.person_id);
            mCommentTv = (TextView) itemView.findViewById(R.id.person_id1);
            mDateTv = (TextView) itemView.findViewById(R.id.itemDate);
        }
    }

}
