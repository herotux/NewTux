package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

public class TeleTuxSettingsActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter listAdapter;

    private int rowCount;
    private int easyLogoutRow;
    private int allowScreenCaptureRow;
    private int bypassRestrictionsRow;
    private int persianCalendarRow;
    private int persianFontRow;
    private int sectionShadowRow;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        return true;
    }

    private void updateRows() {
        rowCount = 0;
        easyLogoutRow = rowCount++;
        allowScreenCaptureRow = rowCount++;
        bypassRestrictionsRow = rowCount++;
        persianCalendarRow = rowCount++;
        persianFontRow = rowCount++;
        sectionShadowRow = rowCount++;
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("TeleTux Settings");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        listAdapter = new ListAdapter(context);

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(listAdapter);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView.setOnItemClickListener((view, position, x, y) -> {
            if (position == easyLogoutRow) {
                SharedConfig.easyLogout = !SharedConfig.easyLogout;
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.easyLogout);
                }
                SharedConfig.saveConfig();
            } else if (position == allowScreenCaptureRow) {
                SharedConfig.allowScreenCapture = !SharedConfig.allowScreenCapture;
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.allowScreenCapture);
                }
                SharedConfig.saveConfig();
            } else if (position == bypassRestrictionsRow) {
                SharedConfig.bypassRestrictions = !SharedConfig.bypassRestrictions;
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.bypassRestrictions);
                }
                SharedConfig.saveConfig();
            } else if (position == persianCalendarRow) {
                SharedConfig.persianCalendar = !SharedConfig.persianCalendar;
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.persianCalendar);
                }
                SharedConfig.saveConfig();
            } else if (position == persianFontRow) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle("Select Persian Font");
                builder.setItems(new CharSequence[]{"Default", "Vazir", "Yekan"}, (dialog, which) -> {
                    SharedConfig.persianFont = which;
                    SharedConfig.saveConfig();
                    if (listAdapter != null) {
                        listAdapter.notifyDataSetChanged();
                    }
                });
                showDialog(builder.create());
            }
        });

        return fragmentView;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0: {
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (position == easyLogoutRow) {
                        checkCell.setTextAndCheck("Easy Logout", SharedConfig.easyLogout, true);
                    } else if (position == allowScreenCaptureRow) {
                        checkCell.setTextAndCheck("Allow Screen Capture", SharedConfig.allowScreenCapture, true);
                    } else if (position == bypassRestrictionsRow) {
                        checkCell.setTextAndCheck("Bypass Forward/Save Restrictions", SharedConfig.bypassRestrictions, true);
                    } else if (position == persianCalendarRow) {
                        checkCell.setTextAndCheck("Persian (Jalali) Calendar", SharedConfig.persianCalendar, true);
                    }
                    break;
                }
                case 1: {
                    TextSettingsCell settingsCell = (TextSettingsCell) holder.itemView;
                    if (position == persianFontRow) {
                        String value;
                        if (SharedConfig.persianFont == 1) value = "Vazir";
                        else if (SharedConfig.persianFont == 2) value = "Yekan";
                        else value = "Default";
                        settingsCell.setTextAndValue("Persian Font", value, true);
                    }
                    break;
                }
            }
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 0 || type == 1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new TextCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new TextSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    view = new HeaderCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new ShadowSectionCell(mContext);
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == easyLogoutRow || position == allowScreenCaptureRow || position == bypassRestrictionsRow || position == persianCalendarRow) {
                return 0;
            } else if (position == persianFontRow) {
                return 1;
            } else if (position == sectionShadowRow) {
                return 3;
            }
            return 0;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }
}
