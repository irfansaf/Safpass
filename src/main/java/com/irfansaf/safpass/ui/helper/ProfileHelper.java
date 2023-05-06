package com.irfansaf.safpass.ui.helper;

import com.irfansaf.safpass.model.User;
import com.irfansaf.safpass.ui.ProfileDialog;
import com.irfansaf.safpass.ui.SafPassFrame;

public class ProfileHelper {
    public static void infoProfile(SafPassFrame safPassFrame) {
        User user = safPassFrame.getUser();
        String accessToken = safPassFrame.getAccessToken();
        ProfileDialog profileDialog = new ProfileDialog(safPassFrame, user, accessToken);
        profileDialog.setVisible(true);
    }
}
