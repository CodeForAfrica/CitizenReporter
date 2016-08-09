package org.codeforafrica.citizenreporter.eNCA.chat;

/**
 * Created by nick on 1/3/15.
 */

public class Message {

    private String id;
    private String message;
    private String user;
    private String is_mine;

    public void setId(String _id){this.id=_id;}
    public void setIsMine(String _is_mine){this.is_mine=_is_mine;}
    public void setMessage(String _message){this.message=_message;}
    public void setUser(String _user){this.user=_user;}

    public String getId(){return this.id;}
    public String getMessage(){return this.message;}
    public String getIsMine(){return this.is_mine;}
    public String getUser(){return this.user;}

}
