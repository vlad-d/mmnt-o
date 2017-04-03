package com.dr.vlad.memento;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.dr.vlad.memento.model.Note;
import com.dr.vlad.memento.model.NoteItem;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    //Bottom Action Bar
    public static final int BAB_STATE_COLLAPSED = 0;
    public static final int BAB_STATE_EXPANDED = 1;
    public int babState = BAB_STATE_COLLAPSED;

    FloatingActionButton fab;
    View bottomActionsContainer;
    View btnCreateNote, btnCreateFromCamera, btnBAction3, btnBAction4;

    List<Note> notes;
    RecyclerView rvMainRecyclerView;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Notes");
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        notes = getNotesWithItems();
        TextView tvEmptyList = (TextView) findViewById(R.id.tv_empty_list);
        if (notes.isEmpty()) {
            if (tvEmptyList.getVisibility() != View.VISIBLE) {
                tvEmptyList.setVisibility(View.VISIBLE);
            }
        } else {
            tvEmptyList.setVisibility(View.GONE);
            rvMainRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
            rvMainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new NoteAdapter(MainActivity.this, notes);
            rvMainRecyclerView.setAdapter(adapter);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (babState == BAB_STATE_COLLAPSED) {
                    revealBottomNavigation();
                } else {
                    hideBottomNavigation();
                }
            }
        });


        setBottomActionBarViews();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_notes);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (babState == BAB_STATE_EXPANDED) {
            hideBottomNavigation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        resetBottomNavigation();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
//            closeAppDialog();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void revealBottomNavigation() {

        int cx = bottomActionsContainer.getMeasuredWidth() / 2;
        int cy = bottomActionsContainer.getMeasuredHeight() / 2;

        float finalRadius = (float) Math.hypot(cx, cy);

        //Nav animation
        Animator navigationAnimator = ViewAnimationUtils.createCircularReveal(bottomActionsContainer, cx, cy, 0, finalRadius);

        //Fab rotation animator
        ValueAnimator fabRotationAnimator = ValueAnimator.ofFloat(0, 405);
        fabRotationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                fab.setRotation(value);
            }
        });
        
        //Fab elevation animator
        ValueAnimator fabEleveationAnimator = ValueAnimator.ofFloat(4, 0);
//        fabEleveationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                float value = (float) valueAnimator.getAnimatedValue();
//                fab.setElevation(value);
//            }
//        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(navigationAnimator).with(fabRotationAnimator).with(fabEleveationAnimator);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.setDuration(500);
        bottomActionsContainer.setVisibility(View.VISIBLE);
        animatorSet.start();

        babState = BAB_STATE_EXPANDED;

    }

    private void hideBottomNavigation() {

        int cx = bottomActionsContainer.getMeasuredWidth() / 2;
        int cy = bottomActionsContainer.getMeasuredHeight() / 2;
        float initialRadius = (float) Math.hypot(cx, cy);

        //Nav animation
        Animator navigationAnimator = ViewAnimationUtils.createCircularReveal(bottomActionsContainer, cx, cy, initialRadius, 0);
        navigationAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                bottomActionsContainer.setVisibility(View.INVISIBLE);
            }
        });

        //Fab rotation animator
        ValueAnimator fabRotationAnimator = ValueAnimator.ofFloat(405, 0);
        fabRotationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                fab.setRotation(value);
            }
        });

        //Fab elevation animator
        ValueAnimator fabEleveationAnimator = ValueAnimator.ofFloat(0, 4);
//        fabEleveationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                float value = (float) valueAnimator.getAnimatedValue();
//                fab.setElevation(value);
//            }
//        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(navigationAnimator).with(fabRotationAnimator).with(fabEleveationAnimator);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.setDuration(500);
        bottomActionsContainer.setVisibility(View.VISIBLE);
        animatorSet.start();

        babState = BAB_STATE_COLLAPSED;

    }

    private void resetBottomNavigation() {
        if (babState == BAB_STATE_EXPANDED) {
            bottomActionsContainer.setVisibility(View.INVISIBLE);
            babState = BAB_STATE_COLLAPSED;
            fab.setRotation(0);
        }
    }

    private void setBottomActionBarViews() {
        btnCreateNote = findViewById(R.id.btn_create_note);
        btnCreateNote.setOnClickListener(this);
        btnCreateFromCamera = findViewById(R.id.btn_create_from_camera);
//        btnCreateFromCamera.setOnClickListener(this);
//        btnBAction3 = findViewById(R.id.btn_b_3);
//        btnBAction4 = findViewById(R.id.btn_b_4);
        bottomActionsContainer = findViewById(R.id.actions_container);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_create_note:
                startActivity(new Intent(this, NoteActivity.class));
                break;

            case R.id.btn_create_from_camera:
                startActivity(new Intent(this, CreateChecklistActivity.class));
                break;

        }
    }

    private void closeAppDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).setMessage(R.string.message_close_app)
                .setPositiveButton(R.string.close_app, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private List<Note> getNotesWithItems() {
        DatabaseHelper db = new DatabaseHelper(this);
        List<Note> notes = db.getNotes();
        if (!notes.isEmpty()) {
            for (Note note : notes) {
                List<NoteItem> items = db.getNoteItems(note.getId());
                note.setItems(items);
            }
        }

        return notes;
    }


    public void openNote(long noteId) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(this.getResources().getString(R.string.key_note_id), noteId);
        this.startActivity(intent);
    }


}
