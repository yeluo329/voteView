package com.pushpendra.vote;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class VoteAdapter extends RecyclerView.Adapter<VoteAdapter.VoteHolder> {
    private List<VoteInfo.optionInfo> voteInfoList = new ArrayList<>();
    private VoteHolder.CallBacK callBacK;
    private int myOption;

    public void setMyOption(int myOption) {
        this.myOption = myOption;
    }

    public void update(List<VoteInfo.optionInfo> list) {
        voteInfoList.clear();
        voteInfoList.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VoteHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.vote_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VoteHolder holder, int position) {
        holder.update(voteInfoList.get(position), callBacK, position, myOption);
    }

    @Override
    public void onBindViewHolder(@NonNull VoteHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            for (Object payload : payloads) {
                if (payload instanceof VoteLoad) {
                    holder.startDefaultAnimation(position, ((VoteLoad) payload).optionList);
                }
            }
        }
    }

    public void setCallBacK(VoteHolder.CallBacK callBacK) {
        this.callBacK = callBacK;
    }

    public void refreshPartItem(int position, List<VoteInfo.optionInfo> optionList) {
        notifyItemChanged(position, new VoteLoad(optionList));
    }

    private static class VoteLoad {
        public List<VoteInfo.optionInfo> optionList = new ArrayList<>();

        public VoteLoad(List<VoteInfo.optionInfo> optionList) {
            this.optionList.clear();
            this.optionList.addAll(optionList);
        }
    }


    @Override
    public int getItemCount() {
        return voteInfoList.size();
    }

    public static class VoteHolder extends RecyclerView.ViewHolder {
        private final TextView voteTv;
        private final TextView progressTv;
        private final ConstraintLayout voteLay;
        private Animation animation;
        private ValueAnimator valueAnimator;
        private final View chooseView;
        private final TextView chooseVoteTv;
        private int x;

        public VoteHolder(@NonNull View itemView) {
            super(itemView);
            voteTv = itemView.findViewById(R.id.vote_tv);
            progressTv = itemView.findViewById(R.id.progress_tv);
            voteLay = itemView.findViewById(R.id.vote_lay);
            chooseView = itemView.findViewById(R.id.choose_view);
            chooseVoteTv = itemView.findViewById(R.id.choose_vote_tv);
        }

        public void update(VoteInfo.optionInfo optionInfo, CallBacK callBacK, int position, int myOption) {
            voteTv.setText(optionInfo.getName());
            chooseVoteTv.setText(optionInfo.getName());
            chooseVoteTv.setVisibility(View.GONE);
            voteTv.post(() -> {
                int[] location = new int[2];
                voteTv.getLocationOnScreen(location);
                x = location[0];
            });
            if (myOption != 0) {
                chooseVoteTv.setVisibility(View.VISIBLE);
                progressTv.setVisibility(View.VISIBLE);
                chooseView.setVisibility(View.VISIBLE);
                voteTv.setVisibility(View.GONE);
                String progress = (int) (intToDouble(optionInfo.getRate()) * 100) + "%";
                progressTv.setText(progress);
                voteLay.setBackgroundResource(R.drawable.layer_dedee0);
                voteLay.setOnClickListener(v ->
                        Toast.makeText(chooseView.getContext(), "不可重复投票", Toast.LENGTH_SHORT).show()
                );
                if (optionInfo.getId() != myOption) {
                    voteLay.post(() -> {
                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) chooseView.getLayoutParams();
                        layoutParams.width = (int) (voteLay.getWidth() * intToDouble(optionInfo.getRate()));
                        chooseView.setLayoutParams(layoutParams);
                        if (optionInfo.getRate() == 0) {
                            chooseView.setBackgroundResource(R.drawable.shape_ffffff_corner8);
                        } else {
                            chooseView.setBackgroundResource(R.drawable.shape_e6e7ec_corner8);
                        }
                        progressTv.setTextColor(Color.parseColor("#333333"));
                        chooseVoteTv.setTextColor(Color.parseColor("#333333"));
                    });
                } else {
                    voteLay.post(() -> {
                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) chooseView.getLayoutParams();
                        layoutParams.width = (int) (voteLay.getWidth() * intToDouble(optionInfo.getRate()));
                        voteTv.setVisibility(View.GONE);
                        chooseVoteTv.setVisibility(View.VISIBLE);
                        chooseView.setLayoutParams(layoutParams);
                        voteLay.setBackgroundResource(R.drawable.layer_e0ffee);
                        chooseView.setBackgroundResource(R.drawable.shape_e0ffee_corner8);
                        chooseVoteTv.setTextColor(Color.parseColor("#30D395"));
                        progressTv.setTextColor(Color.parseColor("#30D395"));
                    });
                }
            } else {
                voteTv.setVisibility(View.VISIBLE);
                chooseVoteTv.setVisibility(View.GONE);
                progressTv.setVisibility(View.GONE);
                chooseVoteTv.setVisibility(View.GONE);
                chooseView.setVisibility(View.GONE);
                voteLay.setBackgroundResource(R.drawable.layer_dedee0);
                voteLay.setOnClickListener(v -> {
                    List<VoteInfo.optionInfo> voteInfoList = new ArrayList<>();
                    voteInfoList.add(new VoteInfo.optionInfo("一直在一起", 2000, 1));
                    voteInfoList.add(new VoteInfo.optionInfo("从未在一起", 2000, 2));
                    voteInfoList.add(new VoteInfo.optionInfo("在一起然后分手", 6000, 3));
                    VoteInfo voteInfo = new VoteInfo(1, voteInfoList);
                    initAnimator(voteLay.getWidth(), intToDouble(voteInfoList.get(position).getRate()));
                    startChooseAnimation();
                    if (callBacK != null) {
                        callBacK.click(position, voteInfo);
                    }
                    voteLay.setOnClickListener(null);
                });
            }
        }

        public void startDefaultAnimation(int position, List<VoteInfo.optionInfo> options) {
            voteLay.setOnClickListener(null);
            startAnimationTv();
            chooseView.setVisibility(View.VISIBLE);
            progressTv.setVisibility(View.VISIBLE);
            voteLay.setBackgroundResource(R.drawable.layer_dedee0);
            chooseView.setBackgroundResource(R.drawable.shape_e6e7ec_corner8);
            progressTv.post(() -> progressTv.setTextColor(Color.parseColor("#333333")));
            initAnimator(voteLay.getWidth(), intToDouble(options.get(position).getRate()));
            if (valueAnimator != null && options.get(position).getRate() != 0)
                valueAnimator.start();
            else
                chooseView.setBackgroundResource(R.drawable.shape_ffffff_corner8);
        }

        public void startChooseAnimation() {
            voteLay.setBackgroundResource(R.drawable.layer_e0ffee);
            chooseView.setVisibility(View.VISIBLE);
            progressTv.setVisibility(View.VISIBLE);
            voteTv.setTextColor(Color.parseColor("#30D395"));
            startAnimationTv();
            if (valueAnimator != null)
                valueAnimator.start();
        }

        private void startAnimationTv() {
            animation = new TranslateAnimation(Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, -(x - 96), Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f);
            animation.setDuration(700);
            animation.setRepeatCount(0);
            animation.setInterpolator(new DecelerateInterpolator());
            animation.setFillAfter(true);
            voteTv.startAnimation(animation);
        }


        private void initAnimator(int width, double count) {
            String progress = (int) (count * 100) + "%";
            progressTv.setText(progress);
            valueAnimator = ObjectAnimator.ofFloat(0, (float) (width * count));
            valueAnimator.setDuration(700);
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(animation -> {
                float currentValue = (float) animation.getAnimatedValue();
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) chooseView.getLayoutParams();
                layoutParams.width = (int) (currentValue);
                chooseView.setLayoutParams(layoutParams);
            });
        }

        public interface CallBacK {
            void click(int position, VoteInfo voteInfo);
        }

        public float intToDouble(int rate) {
            float d = (float) rate / 10000;
            return (float) Math.round(d * 100) / 100;
        }

    }
}
