package client.model;

import java.io.Serializable;

public class Player implements Serializable {

    private String nickname;

    public Player(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
