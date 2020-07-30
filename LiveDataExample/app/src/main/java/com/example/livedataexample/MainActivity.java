package com.example.livedataexample;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private FavouriteViewModel mFavouriteViewModel;
    private FavouriteAdapter mFavouriteAdapter;
    public List<Favourites> mFavourites;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        FloatingActionButton fab = findViewById(R.id.fab);

        mFavouriteViewModel = new ViewModelProvider(this).get(FavouriteViewModel.class);

        final Observer<List<Favourites>> listObserver = new Observer<List<Favourites>>() {
            @Override
            public void onChanged(final List<Favourites> updatedList) {
                if(mFavourites == null) {
                    mFavourites = updatedList;
                    mFavouriteAdapter = new FavouriteAdapter();
                    mRecyclerView.setAdapter(mFavouriteAdapter);
                } else {
                    DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                        @Override
                        public int getOldListSize() {
                            return mFavourites.size();
                        }

                        @Override
                        public int getNewListSize() {
                            return updatedList.size();
                        }

                        @Override
                        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                            return mFavourites.get(oldItemPosition) == updatedList.get(newItemPosition);
                        }

                        @Override
                        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                            Favourites favouritesOld = mFavourites.get(oldItemPosition);
                            Favourites favouritesNew = updatedList.get(newItemPosition);
                            return favouritesOld.equals(favouritesNew);
                        }
                    });
                    result.dispatchUpdatesTo(mFavouriteAdapter);
                    mFavourites = updatedList;
                }
            }
        };
        mFavouriteViewModel.getFavourites().observe(this, listObserver);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText url = new EditText(MainActivity.this);
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("New favourite")
                        .setMessage("Add a url link below")
                        .setView(url)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String urlString = url.getText().toString();
                                long date = (new Date().getTime());
                                mFavouriteViewModel.addFav(urlString, date);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }

    public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder> {

        @NonNull
        @Override
        public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.list_item_row, parent, false);
            return new FavouriteViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {
            Favourites favourites = mFavourites.get(position);
            holder.mUrl.setText(favourites.mLink);
            holder.mDate.setText(new Date(favourites.mDate).toString());
        }

        @Override
        public int getItemCount() {
            return mFavourites.size();
        }

        class FavouriteViewHolder extends RecyclerView.ViewHolder {
            TextView mUrl, mDate;

            public FavouriteViewHolder(@NonNull View itemView) {
                super(itemView);
                mUrl = itemView.findViewById(R.id.tvUrl);
                mDate = itemView.findViewById(R.id.tvDate);
                itemView.findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = getAdapterPosition();
                        Favourites favourites = mFavourites.get(pos);
                        mFavouriteViewModel.removeFavourites(favourites.mId);
                    }
                });
            }
        }

    }
}
