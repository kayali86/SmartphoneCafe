package com.kayali_developer.smartphonecafe;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
import butterknife.Unbinder;

public class MembersFragment
        extends Fragment
        implements MembersAdapter.MembersAdapterListener {

    public static final String TAG = "AllMembersFragmentTag";

    @BindView(R.id.rv_members)
    RecyclerView rvMembers;

    private MainActivity mActivity;
    private Unbinder unbinder;
    private MembersAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = ((MainActivity) getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_members, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvMembers.setHasFixedSize(true);
        rvMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MembersAdapter(this);
        rvMembers.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mActivity.mViewModel.getAllUsers() != null && mActivity.mViewModel.getAllUsers().size() > 0) {
            updateData(mActivity.mViewModel.getAllUsers());
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
        if (mActivity.mViewModel.mCurrentUser.getPermissionType() >= User.ADMIN_PERMISSION_TYPE) {

            if (mActivity.mViewModel.mCurrentUser.getPermissionType() == User.DEVELOPER_PERMISSION_TYPE
                    || mActivity.mViewModel.mCurrentUser.getPermissionType() == User.SUPER_ADMIN_PERMISSION_TYPE) {

                showFullUserPermissionTypeSelectionDialog(user);

            } else if (mActivity.mViewModel.mCurrentUser.getPermissionType() == User.ADMIN_PERMISSION_TYPE) {
                showMemberHelperAdminUserPermissionTypeSelectionDialog(user);
            }
            updateData(mActivity.mViewModel.getAllUsers());
        }
    }

    @Override
    public void onRateMemberClicked(User ratedUser) {

    }


    private void showFullUserPermissionTypeSelectionDialog(User helper) {

        ContextThemeWrapper cw = new ContextThemeWrapper(getContext(), R.style.AlertDialogTheme);
        AlertDialog.Builder builder = new AlertDialog.Builder(cw);
        View titleView = new TextView(getContext());
        ((TextView) titleView).setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        titleView.setPadding(64, 0,0,0);
        ((TextView) titleView).setText("Set User Permission!");
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
                    updateData(mActivity.mViewModel.getAllUsers());
                    if (mActivity.mViewPagerAdapter.getHelpersFragment() != null){
                        mActivity.mViewPagerAdapter.getHelpersFragment().updateData(mActivity.mViewModel.getAllHelpers());
                    }
                    Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Display an alert dialog
    private void showDeletionConfirmDialog(DialogInterface.OnClickListener deleteButtonClickListener, String message, String positiveButtonCaption, String negativeButtonCaption) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonCaption, deleteButtonClickListener);
        builder.setNegativeButton(negativeButtonCaption, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteMember(User user) {
        FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
        DatabaseReference articlesReference = fbDatabase.getReference().child(AppConstants.USERS_CHILD);
        articlesReference.child(user.getUid()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                mActivity.mViewModel.mAllArticles.remove(user);
                if (mActivity.mViewPagerAdapter.getHelpersFragment() != null){
                    mActivity.mViewPagerAdapter.getHelpersFragment().updateData(mActivity.mViewModel.getAllHelpers());
                }
                if (user.getImageURL() != null){
                    deleteMemberImage(user);
                }else{
                    mActivity.mViewModel.deleteMemberByUID(user.getUid());
                    mAdapter.removeMember(user);
                    Toast.makeText(getContext(), "User deleted!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void deleteMemberImage(User user) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imagePath = storageReference.child(AppConstants.ARTICLE_IMAGE_REFERENCE).child(user.getUid());
        imagePath.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mActivity.mViewModel.deleteMemberByUID(user.getUid());
                mAdapter.removeMember(user);
                Toast.makeText(getContext(), "User deleted!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(getContext(), "Deletion failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
