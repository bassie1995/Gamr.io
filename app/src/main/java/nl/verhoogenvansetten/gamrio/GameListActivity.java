package nl.verhoogenvansetten.gamrio;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import nl.verhoogenvansetten.gamrio.model.Game;
import nl.verhoogenvansetten.gamrio.ui.DeviceDialogFragment;
import nl.verhoogenvansetten.gamrio.util.HighScoreTest;
import nl.verhoogenvansetten.gamrio.util.Network;
import nl.verhoogenvansetten.gamrio.util.WiFiDirectBroadcastReceiver;
import nl.verhoogenvansetten.gamrio.util.Server;

/**
 * An activity representing a list of Games. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link GameDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class GameListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, DeviceDialogFragment.OnFragmentInteractionListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    IntentFilter intentFilter;
    Network network;

    public static SimpleItemRecyclerViewAdapter adapter;
    //Temporary debug
    //todo Remove
    public static Boolean debug = true;

    FloatingActionButton fab;

    private static WeakReference<Context> mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = new WeakReference<>(getApplicationContext());
        setContentView(R.layout.activity_game_list);

        //Todo remove
        if (debug) {
            HighScoreTest.test();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        network = Network.getInstance();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                network.enableWifi();
                network.discoverPeers(manager);
            }
        });

        View recyclerView = findViewById(R.id.game_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.game_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        //handleIntent(getIntent());
        registerReceiver(network.getReceiver(), intentFilter);

    }

    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        // Verify the action and get the query
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            // Filter RecyclerView adapter here
            //doMySearch(query);
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new SimpleItemRecyclerViewAdapter(GameList.getList());
        recyclerView.setAdapter(adapter);
        // recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        // searchView.setOnQueryTextListener(this);

        // Assumes current Activity is the searchable Activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true); // Do not iconify, rather expand the widget by default
        searchView.setQueryRefinementEnabled(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextChange(String text) {
        adapter.setFilter(GameList.getList()); // Set filter to game list
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public static Context getContext() {
        return mContext.get();
    }

    public void onFragmentInteraction(Uri uri) {
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Game> mValues;

        SimpleItemRecyclerViewAdapter(List<Game> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.game_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(mValues.get(position).name);
            holder.mImageView.setImageResource(holder.mItem.image);

            BitmapDrawable drawable = (BitmapDrawable) holder.mImageView.getDrawable();
            Palette.from(drawable.getBitmap()).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    holder.mCardView.setBackgroundColor(palette.getVibrantColor(ContextCompat.getColor(getApplicationContext(), R.color.md_white)));
                }
            });

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putInt(GameDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        GameDetailFragment fragment = new GameDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.game_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, GameDetailActivity.class);
                        intent.putExtra(GameDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            String headerTransition = getString(R.string.transition_header);
                            String fabTransition = getString(R.string.transition_fab);

                            Pair<View, String> headerPair = Pair.create((View) holder.mImageView, headerTransition);
                            Pair<View, String> fabPair = Pair.create((View) fab, fabTransition);

                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(GameListActivity.this, headerPair, fabPair);

                            ActivityCompat.startActivity(GameListActivity.this, intent, options.toBundle());
                        } else {
                            startActivity(intent);
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        void setFilter(List<Game> gameList) {
            mValues.clear();
            mValues.addAll(gameList); // Use actual gameList here
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final TextView mContentView;
            final ImageView mImageView;
            final CardView mCardView;
            Game mItem;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.txtv_title);
                mImageView = (ImageView) view.findViewById(R.id.game_image);
                mCardView = (CardView) view.findViewById(R.id.card_list);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        network.onDestroy();
        unregisterReceiver(network.getReceiver());

    }

    /* register the broadcast receiver with the intent values to be matched
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(network.getReceiver(), intentFilter);
    }

    /* unregister the broadcast receiver
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(network.getReceiver());
    }*/
}
