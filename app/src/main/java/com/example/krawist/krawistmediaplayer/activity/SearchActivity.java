package com.example.krawist.krawistmediaplayer.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.krawist.krawistmediaplayer.R;
import com.example.krawist.krawistmediaplayer.helper.Helper;
import com.example.krawist.krawistmediaplayer.models.Musique;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    Intent intent;
    String TAG = SearchActivity.class.getSimpleName();
    RecyclerAdapter adapter;
    ArrayList<Musique> listOfMusic = new ArrayList<>();
    ImageButton backButton;
    EditText searchInput;
    Toolbar toolbar;
    UpdateMusicData updateMusicData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initialisation();

        addActionsOnView();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initialisation(){
        recyclerView = findViewById(R.id.search_recyclerview);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RecyclerAdapter(listOfMusic);
        recyclerView.setAdapter(adapter);

        backButton = findViewById(R.id.imageButton_search_back);

        searchInput = findViewById(R.id.edittext_search_field);

        toolbar = findViewById(R.id.toolbar_search);

        updateMusicData = new UpdateMusicData();

        setSupportActionBar(toolbar);
    }

    private void addActionsOnView(){

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.super.onBackPressed();
            }
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    handleSearch(s.toString());
                }else{
                    listOfMusic.clear();
                    adapter.swicthList(listOfMusic);
                }
            }
        });
    }

    private void handleSearch(String word){

        if(word.length()>0){
            listOfMusic = Helper.getResearchMusic(SearchActivity.this,word);
            adapter.swicthList(listOfMusic);
/*            UpdateMusicData updateMusicData = new UpdateMusicData();
            updateMusicData.execute();*/
        }

    }

    public class RecyclerAdapter extends RecyclerView.Adapter{

        ArrayList<Musique> listOfMusique = new ArrayList<>();

        public RecyclerAdapter(ArrayList<Musique> listOfMusique){
            this.listOfMusique = listOfMusique;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new RecyclerViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_item_layout,viewGroup,false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ((RecyclerViewHolder)viewHolder).putData(listOfMusique.get(i));
        }

        @Override
        public int getItemCount() {
            return listOfMusique.size();
        }

    public void swicthList(ArrayList<Musique> list){
            this.listOfMusique = list;
            notifyDataSetChanged();
    }
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView albumArt;
        TextView songName;
        TextView songArtist;
        int pochetteHeight = 50;
        int pochetteWidth = 50;
        RelativeLayout rootLayout;

        public RecyclerViewHolder(View view){
            super(view);
            //albumArt = view.findViewById(R.id.search_item_music_pochette);
            songName = view.findViewById(R.id.textview_search_item_music_name);
            songArtist = view.findViewById(R.id.textview_search_item_music_artist);
            rootLayout = view.findViewById(R.id.layout_root);
        }

        public void putData(final Musique musique){

            Bitmap bitmap;
            bitmap = Helper.decodeSampleBitmap(musique.getPochette(),pochetteWidth,pochetteHeight);
            if(bitmap==null)
                bitmap = (Helper.decodeSampleBitmap(getResources(),R.drawable.default_background,pochetteWidth,pochetteHeight));


            //albumArt.setImageBitmap(bitmap);
            songName.setText(musique.getMusicTitle());
            songArtist.setText(musique.getArtistName());

            rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent musicIntent = new Intent(SearchActivity.this,PlayingMusic.class);
                    musicIntent.putExtra(Helper.PLAYING_MUSIC_LIST,listOfMusic);
                    musicIntent.putExtra(Helper.PLAYING_SONG,musique);
                    startActivity(musicIntent);
                }
            });

        }

    }

    @Override
    protected void onResume() {
        updateColor();
        super.onResume();
    }

    private void updateColor(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String color = sharedPreferences.getString(getString(R.string.theme_color_preference_key),getString(R.string.theme_color_default_value));
        toolbar.setBackgroundColor(Color.parseColor(color));
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(Color.parseColor(color));
        }
    }

    private class UpdateMusicData extends AsyncTask<Void,Void,Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            listOfMusic = Helper.updateMusic(SearchActivity.this,listOfMusic);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.swicthList(listOfMusic);
        }
    }
}
