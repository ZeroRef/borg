package org.zeroref.borg.sagas.domain;

public class TrialActivated {
    private String userEmail;

    public TrialActivated(String email) {
        userEmail = email;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
