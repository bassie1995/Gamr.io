package nl.verhoogenvansetten.gamrio;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.verhoogenvansetten.gamrio.model.Game;
import nl.verhoogenvansetten.gamrio.util.AppBarStateChangeListener;

/**
 * A fragment representing a single Game detail screen.
 * This fragment is either contained in a {@link GameListActivity}
 * in two-pane mode (on tablets) or a {@link GameDetailActivity}
 * on handsets.
 */
public class GameDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The game this fragment is presenting.
     */
    private Game mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GameDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the game specified by the fragment arguments.
            mItem = GameList.getGame(getArguments().getInt(ARG_ITEM_ID));

            final Activity activity = this.getActivity();
            CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            AppBarLayout appBarLayout = (AppBarLayout) activity.findViewById(R.id.app_bar);
            final TextView mTxtvTitle = (TextView) activity.findViewById(R.id.txtvTitle);
            if (toolBarLayout != null) {
                mTxtvTitle.setText(mItem.name);
                toolBarLayout.setTitle(mItem.name);
                appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
                    @Override
                    public void onStateChanged(AppBarLayout appBarLayout1, State state) {
                        if (state.name().equals("IDLE"))
                            mTxtvTitle.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.game_detail, container, false);

        // Show the game's details
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.game_detail)).setText(mItem.details);
        }

        return rootView;
    }
}
