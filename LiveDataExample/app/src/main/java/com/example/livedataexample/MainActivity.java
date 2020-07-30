package com.example.livedataexample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FavouriteViewModel mFavouriteViewModel;
    private FavouriteAdapter mFavouriteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);
        FloatingActionButton fab = findViewById(R.id.fab);

        mFavouriteViewModel = new ViewModelProvider(this).get(FavouriteViewModel.class);
        List<Favourites> favourites = mFavouriteViewModel.getFavourites();
        mFavouriteAdapter = new FavouriteAdapter(this, R.layout.list_item_row, favourites);
        listView.setAdapter(mFavouriteAdapter);

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
                                mFavouriteAdapter.add(mFavouriteViewModel.addFav(urlString, date));
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }

    public void deleteFav(View view) {
        View parent = (View) view.getParent();
        int position = (int) parent.getTag(R.id.POS);
        Favourites favourites = mFavouriteAdapter.getItem(position);
        mFavouriteViewModel.removeFavourites(favourites.mId);
        mFavouriteAdapter.remove(favourites);
    }

    public static class FavouriteAdapter extends ArrayAdapter<Favourites> {

        private class ViewHolder {
            TextView tvUrl, tvDate;
        }

        FavouriteAdapter(@NonNull Context context, int resource, List<Favourites> favourites) {
            super(context, resource, favourites);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Favourites favourites = getItem(position);
            ViewHolder viewHolder;
            if(convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                convertView = layoutInflater.inflate(R.layout.list_item_row, parent, false);
                viewHolder.tvUrl = convertView.findViewById(R.id.tvUrl);
                viewHolder.tvDate = convertView.findViewById(R.id.tvDate);
                convertView.setTag(R.id.VH, viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag(R.id.VH);
            }
            viewHolder.tvUrl.setText(favourites.mLink);
            viewHolder.tvDate.setText((new Date(favourites.mDate)).toString());
            convertView.setTag(R.id.POS, position);
            return convertView;
        }
    }
}
