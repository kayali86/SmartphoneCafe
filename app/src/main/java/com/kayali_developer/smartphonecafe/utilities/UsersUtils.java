package com.kayali_developer.smartphonecafe.utilities;

import com.kayali_developer.smartphonecafe.data.model.User;

import java.util.ArrayList;
import java.util.List;

public class UsersUtils {

    public static List<User> getHelpers(List<User> users){
        if (users == null) return null;
        List<User> allHelpers = new ArrayList<>();
        for (User user : users){
            if (user != null){
                if (user.getPermissionType() == User.DEVELOPER_PERMISSION_TYPE || user.getPermissionType() == User.SUPER_ADMIN_PERMISSION_TYPE ||
                        user.getPermissionType() == User.ADMIN_PERMISSION_TYPE || user.getPermissionType() == User.HELPER_PERMISSION_TYPE){
                    allHelpers.add(user);
                }
            }
        }
        return allHelpers;
    }

    public static List<User> getMembersOnly(List<User> users){
        if (users == null) return null;
        List<User> allUsers = new ArrayList<>();
        for (User user : users){
            if (user != null){
                if (user.getPermissionType() == User.MEMBER_PERMISSION_TYPE ){
                    allUsers.add(user);
                }
            }
        }
        return allUsers;
    }

}
