package org.codeforafrica.citizenreporter.eNCA.wallet;

/**
 * Created by nick on 1/3/15.
 */

public class Payment {

    private String id;
    private String message;
    private String remoteID;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    private String amount;

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    private String confirmed;

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    private String receipt;

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    private String post;

    public void setId(String _id){this.id=_id;}
    public void setMessage(String _message){this.message=_message;}

    public String getId(){return this.id;}
    public String getMessage(){return this.message;}

    public String getRemoteID() {
        return remoteID;
    }

    public void setRemoteID(String remoteID) {
        this.remoteID = remoteID;
    }
}
