package com.kayali_developer.smartphonecafe;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kayali_developer.smartphonecafe.data.model.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HelpersFragment
        extends Fragment
        implements MembersAdapter.MembersAdapterListener {

    public static final String TAG = "HelpersFragmentTag";

    @BindView(R.id.rv_members_list)
    RecyclerView rvMembersList;
    @BindView(R.id.fl_btn_to_members_fragment)
    FloatingActionButton flBtnToMembersFragment;

    private MainActivity mActivity;
    private Unbinder unbinder;
    private MembersAdapter mAdapter;
    private MembersFragment membersFragment;

    private HelpersFragmentListener helpersFragmentListener;

    interface HelpersFragmentListener{
        void onRateMemberClicked(User member);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        helpersFragmentListener = ((HelpersFragmentListener) context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = ((MainActivity) getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_helpers_list, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvMembersList.setHasFixedSize(true);
        rvMembersList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MembersAdapter(this);
        rvMembersList.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mActivity.mViewModel.getAllHelpers() != null && mActivity.mViewModel.getAllHelpers().size() > 0) {
            updateData(mActivity.mViewModel.getAllHelpers());
        }

        if (mActivity.mViewModel.mCurrentUser != null
                && mActivity.mViewModel.mCurrentUser.getPermissionType() >= User.ADMIN_PERMISSION_TYPE){
            flBtnToMembersFragment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public void updateData(List<User> users) {
        mAdapter.setData(users);
    }

    @Override
    public void onMemberLongClicked(User user) {
        if (!mActivity.mViewModel.mCurrentUser.getUid().equals(user.getUid())
                && mActivity.mViewModel.mCurrentUser.getPermissionType() > user.getPermissionType()) {

            if (mActivity.mViewModel.mCurrentUser.getPermissionType() == User.DEVELOPER_PERMISSION_TYPE
                    || mActivity.mViewModel.mCurrentUser.getPermissionType() == User.SUPER_ADMIN_PERMISSION_TYPE) {

                showFullUserPermissionTypeSelectionDialog(user);

            } else if (mActivity.mViewModel.mCurrentUser.getPermissionType() == User.ADMIN_PERMISSION_TYPE) {
                showMemberHelperAdminUserPermissionTypeSelectionDialog(user);
            }
            updateData(mActivity.mViewModel.getAllHelpers());
        }
    }

    @Override
    public void onRateMemberClicked(User ratedUser) {
        if (mActivity.mViewModel.mCurrentUser.getPermissionType() == User.MEMBER_PERMISSION_TYPE){
            helpersFragmentListener.onRateMemberClicked(ratedUser);
        }else{
            Toast.makeText(getContext(), "Only Members can give a Feedback!", Toast.LENGTH_SHORT).show();
        }
    }



    private void showFullUserPermissionTypeSelectionDialog(User helper) {

        ContextThemeWrapper cw = new ContextThemeWrapper(getContext(), R.style.AlertDialogTheme);
        AlertDialog.Builder builder = new AlertDialog.Builder(cw);
        View titleView = new TextView(getContext());
        ((TextView) titleView).setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        titleView.setPadding(64, 0,0,0);
        ((TextView) titleView).setText("Set Helper Permission!");
        builder.setCustomTitle(titleView);

        String[] labelsArray = new String[]{"User", "Helper", "Admin", "Super Admin"};
        builder.setItems(labelsArray, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {

                    case 0:
                        changeMemberPermissionType(helper, User.MEMBER_PERMISSION_TYPE);
                        break;

                    case 1:
                        changeMemberPermissionType(helper, User.HELPER_PERMISSION_TYPE);
                        break;

                    case 2:
                        changeMemberPermissionType(helper, User.ADMIN_PERMISSION_TYPE);
                        break;

                    case 3:
                        changeMemberPermissionType(helper, User.SUPER_ADMIN_PERMISSION_TYPE);
                        break;
                }
            }
        });

        builder.show();
    }

    private void showMemberHelperAdminUserPermissionTypeSelectionDialog(User helper) {

        ContextThemeWrapper cw = new ContextThemeWrapper(getContext(), R.style.AlertDialogTheme);
        AlertDialog.Builder builder = new AlertDialog.Builder(cw);
        View titleView = new TextView(getContext());
        ((TextView) titleView).setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        titleView.setPadding(64, 0,0,0);
        ((TextView) titleView).setText("Set User Permission!");
        builder.setCustomTitle(titleView);

        String[] labelsArray = new String[]{"User", "Helper", "Admin"};

        builder.setItems(labelsArray, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {

                    case 0:
                        changeMemberPermissionType(helper, User.MEMBER_PERMISSION_TYPE);
                        break;

                    case 1:
                        changeMemberPermissionType(helper, User.HELPER_PERMISSION_TYPE);
                        break;

                    case 2:
                        changeMemberPermissionType(helper, User.ADMIN_PERMISSION_TYPE);
                        break;
                }
            }

        });

        builder.show();
    }

    private void changeMemberPermissionType(User user, int newPermissionType) {
        FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
        DatabaseReference usersReference = fbDatabase.getReference().child(AppConstants.USERS_CHILD);
        User newUser = null;
        try {
            newUser = (User) user.clone();
            newUser.setPermissionType(newPermissionType);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        if (newUser != null) {
            usersReference.child(user.getUid()).setValue(newUser, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    mActivity.mViewModel.changeMemberPermissionByUID(user.getUid(), newPermissionType);
                    updateData(mActivity.mViewModel.getAllHelpers());
                    Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @OnClick(R.id.fl_btn_to_members_fragment)
    public void onViewClicked() {
        mActivity.mainActivityFragmentContainer.setVisibility(View.VISIBLE);
        membersFragment = new MembersFragment();
        if (mActivity.mFragmentManager != null){
            mActivity.mFragmentManager.beginTransaction().add(R.id.main_activity_fragment_container, membersFragment, MembersFragment.TAG).addToBackStack(null).commitAllowingStateLoss();
        }
    }
}
