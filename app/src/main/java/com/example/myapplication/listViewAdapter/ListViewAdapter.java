package com.example.myapplication.listViewAdapter;

import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.ClickEvent;
import com.example.myapplication.LocalDatabase.MyDatabase;
import com.example.myapplication.LocalDatabase.PeopleVote;
import com.example.myapplication.R;
import com.example.myapplication.TimeOutEvent;
import com.example.myapplication.VotedEvent;
import com.example.myapplication.account_management.HomePageFragment;
import com.example.myapplication.view_place.PlaceDetailActivity;
import com.example.myapplication.vote_management.PeopleVoteHelper;
import com.example.myapplication.vote_management.RemotePeopleVoteHelper;
import com.example.myapplication.vote_management.RemoteVoteDataHelper;
import com.example.myapplication.vote_management.VoteDataHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/*
implements Filterable
 */

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ViewHolder>  {


    private List<ListViewItem> viewItemsList;
    private List<ListViewItem> backViewItemsList;

    PeopleVoteHelper localPeopleVoteHelper;

    VoteDataHelper localVoteHelper;

    ViewHolder curHolder;

    // MyFilter mFilter;


    /*
    @Override
    public Filter getFilter() {
        if (mFilter ==null){
            mFilter = new MyFilter();
        }
        return mFilter;
    }

    //我们需要定义一个过滤器的类来定义过滤规则
    class MyFilter extends Filter{
        //我们在performFiltering(CharSequence charSequence)这个方法中定义过滤规则
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults result = new FilterResults();
            List<ListViewItem> list ;
            if (TextUtils.isEmpty(charSequence)) {//当过滤的关键字为空的时候，我们则显示所有的数据
                list  = backViewItemsList;
            } else {//否则把符合条件的数据对象添加到集合中
                list = new ArrayList<>();
                for (ListViewItem item:backViewItemsList){
                    if (item.getPlaceName().contains(charSequence)|| item.getPlaceAddress().contains(charSequence)){

                        list.add(item);
                    }

                }
            }
            result.values = list; //将得到的集合保存到FilterResults的value变量中
            result.count = list.size();//将集合的大小保存到FilterResults的count变量中

            return result;
        }




        //在publishResults方法中告诉适配器更新界面
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            viewItemsList = (List<ListViewItem>)filterResults.values;

            if (filterResults.count>0){
                notifyDataSetChanged();//通知数据发生了改变

            }else {
                notifyDataSetInvalidated();//通知数据失效

            }
        }


    }

     */


    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView placeNameView;
        TextView placeAddressView;
        TextView placeTypeView;
        TextView oneReasonView;
        ImageView img1View;
        ImageView img2View;
        ImageView img3View;
        TextView voteButton;



        public ViewHolder(View view) {
            super(view);
            itemView = view;
            placeNameView = (TextView) view.findViewById(R.id.place_name);
            placeAddressView = (TextView) view.findViewById(R.id.address);
            placeTypeView = (TextView) view.findViewById(R.id.place_type);
            oneReasonView = (TextView) view.findViewById(R.id.one_reason);
            img1View = (ImageView) view.findViewById(R.id.img1);
            img2View = (ImageView) view.findViewById(R.id.img2);
            img3View = (ImageView) view.findViewById(R.id.img3);

            voteButton = (TextView) view.findViewById(R.id.tv_vote);
        }
    }

    public ListViewAdapter(List<ListViewItem> viewsList) {
        viewItemsList = viewsList;
        backViewItemsList = new ArrayList<>(viewsList);

        EventBus.getDefault().register(this);
    }

    public void setData(List<ListViewItem> viewsList) {
        viewItemsList = viewsList;
        backViewItemsList = new ArrayList<>(viewsList);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_list_card, parent, false);
        ViewHolder holder = new ViewHolder(view);

        localPeopleVoteHelper = new RemotePeopleVoteHelper();

        localVoteHelper = new RemoteVoteDataHelper();



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAbsoluteAdapterPosition();
                ListViewItem item = viewItemsList.get(position);

                PlaceDetailActivity.clickStart(holder.itemView.getContext(), item.getPlaceName(), item.getPlaceAddress(), item.getPlaceType() == null ? null : item.getPlaceType().toString(), item.getIfVote());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListViewItem item = viewItemsList.get(position);
        holder.placeNameView.setText(item.getPlaceName());
        holder.placeAddressView.setText(item.getPlaceAddress());
        String placeType;

        curHolder = holder;

        if (item.getPlaceType() != null) {
            placeType = item.getPlaceType().toString();
            holder.placeTypeView.setText("· " + placeType.substring(0, 1) + placeType.substring(1).toLowerCase());
        } else {
            holder.placeTypeView.setText("");
        }

        holder.oneReasonView.setText(item.getRecentReason());
        holder.img1View.setImageBitmap(item.getImageId1());
        holder.img2View.setImageBitmap(item.getImageId2());
        holder.img3View.setImageBitmap(item.getImageId3());

        if (!item.getIfVote()) {
            holder.voteButton.setVisibility(View.GONE);
        } else {
            localPeopleVoteHelper.getVotePeople(HomePageFragment.curGroupNumber, item.getPlaceName(),
                            item.getPlaceAddress(), HomePageFragment.currentVote.voteStartTime, HomePageFragment.curUser)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<PeopleVote>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(PeopleVote peopleVote) {

                            localVoteHelper.getNumberOfNotesOneGroupOneTime(HomePageFragment.curGroupNumber,HomePageFragment.currentVote.voteStartTime,
                                            item.getPlaceName(),
                                            item.getPlaceAddress())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<Integer>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(Integer integer) {
                                            holder.voteButton.setBackgroundResource(R.drawable.grey_background);

                                            holder.voteButton.setText(integer + " voted");
                                            holder.voteButton.setTextColor(Color.parseColor("#333232"));

                                            holder.voteButton.setClickable(false);
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            holder.voteButton.setBackgroundResource(R.drawable.grey_background);

                                            holder.voteButton.setText(0 + " voted");
                                            holder.voteButton.setTextColor(Color.parseColor("#333232"));

                                            holder.voteButton.setClickable(false);
                                        }
                                    });

                        }

                        @Override
                        public void onError(Throwable e) {

                            holder.voteButton.setOnClickListener(v -> {
                                ProgressDialog progressDialog = new ProgressDialog(v.getContext());
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.setMessage("Please wait...");
                                progressDialog.show();
                                localPeopleVoteHelper.insertOnePeopleVote(HomePageFragment.curGroupNumber, HomePageFragment.curUser, item.getPlaceName(),
                                                item.getPlaceAddress(), "true", HomePageFragment.currentVote.voteStartTime)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new CompletableObserver() {
                                            @Override
                                            public void onSubscribe(Disposable d) {

                                            }

                                            @Override
                                            public void onComplete() {
                                                localVoteHelper.updateTheVotes(HomePageFragment.curGroupNumber, item.getPlaceName(),
                                                                item.getPlaceAddress(), HomePageFragment.currentVote.voteStartTime)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(new CompletableObserver() {
                                                            @Override
                                                            public void onSubscribe(Disposable d) {

                                                            }

                                                            @Override
                                                            public void onComplete() {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(v.getContext(), "Voted successfully", Toast.LENGTH_SHORT).show();

                                                                localVoteHelper.getNumberOfNotesOneGroupOneTime(HomePageFragment.curGroupNumber, HomePageFragment.currentVote.voteStartTime,
                                                                                item.getPlaceName(),
                                                                                item.getPlaceAddress())
                                                                        .subscribeOn(Schedulers.io())
                                                                        .observeOn(AndroidSchedulers.mainThread())
                                                                        .subscribe(new SingleObserver<Integer>() {
                                                                            @Override
                                                                            public void onSubscribe(Disposable d) {

                                                                            }

                                                                            @Override
                                                                            public void onSuccess(Integer integer) {
                                                                                holder.voteButton.setBackgroundResource(R.drawable.grey_background);

                                                                                holder.voteButton.setText(integer + " voted");
                                                                                holder.voteButton.setTextColor(Color.parseColor("#333232"));

                                                                                holder.voteButton.setClickable(false);
//                                                                                EventBus.getDefault().post(new VotedEvent());
                                                                            }

                                                                            @Override
                                                                            public void onError(Throwable e) {
                                                                                holder.voteButton.setBackgroundResource(R.drawable.grey_background);

                                                                                holder.voteButton.setText(0 + " voted");
                                                                                holder.voteButton.setTextColor(Color.parseColor("#333232"));

                                                                                holder.voteButton.setClickable(false);
                                                                            }
                                                                        });
                                                            }

                                                            @Override
                                                            public void onError(Throwable e) {
                                                                progressDialog.dismiss();
                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(v.getContext(), "You have voted for this place previously", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            });

                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return viewItemsList.size();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TimeOutEvent event) {
        notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVotedEvent(VotedEvent event) {
        notifyDataSetChanged();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void filter(String text) {
        viewItemsList.clear();
        if(text.isEmpty()){
            viewItemsList.addAll(backViewItemsList);

        } else  {
            text = text.toLowerCase();
            for(ListViewItem item: backViewItemsList){
                if(item.getPlaceName().toLowerCase().contains(text) ||
                        item.getPlaceAddress().toLowerCase().contains(text)){
                    viewItemsList.add(item);
                }
            }


        }

        notifyDataSetChanged();
    }


}
