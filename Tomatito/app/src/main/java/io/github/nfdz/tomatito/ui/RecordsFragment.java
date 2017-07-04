/*
 * Copyright (C) 2017 Noe Fernandez
 */
package io.github.nfdz.tomatito.ui;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.nfdz.tomatito.R;
import io.github.nfdz.tomatito.data.DatabaseManager;
import io.github.nfdz.tomatito.data.FinishedPomodoro;

public class RecordsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        PomodoroAdapter.AdapterOnClickHandler,
        DatabaseManager.DatabaseListener {

    private static final int ID_RECORDS_LOADER = 9831;

    @BindView(R.id.rv_records) RecyclerView mRecordsView;
    @BindView(R.id.pb_records_loading) ProgressBar mLoading;
    @BindView(R.id.tv_records_empty) TextView mEmptyView;

    private final Handler mHandler = new Handler();

    private PomodoroAdapter mAdapter;
    private boolean dbChanged = false;
    private boolean paused = false;

    public RecordsFragment() {
        // required empty public constructor
    }

    public static RecordsFragment newInstance() {
        RecordsFragment fragment = new RecordsFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseManager.getInstance(getActivity()).addListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DatabaseManager.getInstance(getActivity()).removeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        paused = false;
        if (dbChanged) {
            dbChanged = false;
            getActivity().getSupportLoaderManager().restartLoader(ID_RECORDS_LOADER, null, this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().getSupportLoaderManager().initLoader(ID_RECORDS_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_records, container, false);
        ButterKnife.bind(this, view);
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        int orientation = OrientationHelper.VERTICAL;
        boolean reverseLayout = false;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), orientation, reverseLayout);
        mRecordsView.setLayoutManager(layoutManager);
        mRecordsView.setHasFixedSize(true);
        mAdapter = new PomodoroAdapter(getActivity(), this);
        mRecordsView.setAdapter(mAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == ID_RECORDS_LOADER) {
            showLoading();
            return new AsyncTaskLoader<Cursor>(getActivity()) {
                Cursor data;

                @Override
                protected void onStartLoading() {
                    if (data == null) {
                        forceLoad();
                    } else {
                        deliverResult(data);
                    }
                }

                @Override
                public Cursor loadInBackground() {
                    return DatabaseManager.getInstance(getActivity()).queryAllPomodoros();
                }

                @Override
                public void deliverResult(Cursor data) {
                    this.data = data;
                    super.deliverResult(data);
                }
            };
        } else {
            throw new RuntimeException("Unknown loader ID: " + " id=" + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            mAdapter.swapCursor(data);
            showContent();
        } else {
            mAdapter.swapCursor(null);
            showEmpty();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
        showLoading();
    }

    private void showLoading() {
        mEmptyView.setVisibility(View.INVISIBLE);
        mLoading.setVisibility(View.VISIBLE);
        mRecordsView.setVisibility(View.INVISIBLE);
    }

    private void showContent() {
        mEmptyView.setVisibility(View.INVISIBLE);
        mLoading.setVisibility(View.INVISIBLE);
        mRecordsView.setVisibility(View.VISIBLE);
    }

    private void showEmpty() {
        mEmptyView.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.INVISIBLE);
        mRecordsView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void notifyChanges() {
        if (paused) {
            dbChanged = true;
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    getActivity().getSupportLoaderManager().restartLoader(ID_RECORDS_LOADER,
                            null,
                            RecordsFragment.this);
                }
            });
        }
    }

    @Override
    public void onClick(long id, FinishedPomodoro pomodoro) {
        // TODO show more information about pomodoro
    }

    @Override
    public void onLongClick(final long id, final FinishedPomodoro pomodoro) {
        // Edit pomodoro name
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pomodoro_edit_dialog_title);
        final EditText input = new EditText(getActivity());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.append(pomodoro.name);
        builder.setView(input);
        builder.setPositiveButton(R.string.pomodoro_edit_dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String name = input.getText().toString().toLowerCase();
                if (!TextUtils.isEmpty(name) && !name.equals(pomodoro.name)) {
                    new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] params) {
                            DatabaseManager.getInstance(getActivity()).editPomodoroName(id,
                                    pomodoro,
                                    name);
                            return null;
                        }
                    }.execute();
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
