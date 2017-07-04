package io.github.nfdz.tomatito.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.nfdz.tomatito.R;
import io.github.nfdz.tomatito.data.Contract;
import io.github.nfdz.tomatito.data.FinishedPomodoro;

public class PomodoroAdapter extends
        RecyclerView.Adapter<PomodoroAdapter.PomodoroHolder> {

    private final static String ERROR_POSITION_INVALID = "Item position is out of adapter's range.";
    private final static String ERROR_POSITION_NULL = "Item position is in adapter's range but it is empty.";

    public interface AdapterOnClickHandler {
        void onClick(long id, FinishedPomodoro pomodoro);
        void onLongClick(long id, FinishedPomodoro pomodoro);
    }

    private Context mContext;
    private AdapterOnClickHandler mHandler;

    private Cursor mCursor;

    public PomodoroAdapter(@NonNull Context context, AdapterOnClickHandler handler) {
        mContext = context;
        mHandler = handler;
    }

    public void swapCursor(Cursor cursor) {
        if (mCursor != null) mCursor.close();
        mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public PomodoroHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.pomodoro_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParent = false;
        View view = inflater.inflate(layoutId, parent, shouldAttachToParent);
        return new PomodoroHolder(view);
    }

    @Override
    public void onBindViewHolder(PomodoroHolder holder, int position) {
        FinishedPomodoro pomodoro = getItem(position);
        holder.name.setText(pomodoro.name);
        holder.progress.setText(pomodoro.getOverallProgress());
        holder.details.setText(pomodoro.getEndTimeDate());
    }

    public FinishedPomodoro getItem(int position) {
        if (position < 0 || position >= getItemCount()) {
            throw new IllegalArgumentException(ERROR_POSITION_INVALID);
        } else if (mCursor.moveToPosition(position)) {
            return new FinishedPomodoro(mCursor);
        } else {
            throw new IllegalArgumentException(ERROR_POSITION_NULL);
        }
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    public class PomodoroHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        public @BindView(R.id.tv_list_item_progress) TextView progress;
        public @BindView(R.id.tv_list_item_name) TextView name;
        public @BindView(R.id.tv_list_item_details) TextView details;

        public PomodoroHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            FinishedPomodoro pomodoro = getItem(position);
            long id = mCursor.getLong(Contract.PomodoroEntry.POSITION_ID);
            mHandler.onClick(id, pomodoro);
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            FinishedPomodoro pomodoro = getItem(position);
            long id = mCursor.getLong(Contract.PomodoroEntry.POSITION_ID);
            mHandler.onLongClick(id, pomodoro);
            return true;
        }
    }
}
