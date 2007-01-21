package org.jamocha.rete;

public class TemporalDeffact extends Deffact implements TemporalFact {

    protected long expirationTime = 0;
    protected String sourceURL = null;
    protected String serviceType = null;
    protected int validity;
    
    public TemporalDeffact(Deftemplate template, Object instance,
            Slot[] values, long id) {
        super(template, instance, values, id);
    }

    public long getExpirationTime() {
        return this.expirationTime;
    }

    public String getServiceType() {
        return this.serviceType;
    }

    public String getSource() {
        return this.sourceURL;
    }

    public int getValidity() {
        return this.validity;
    }

    public void setExpirationTime(long time) {
        this.expirationTime = time;
    }

    public void setServiceType(String type) {
        this.serviceType = type;
    }

    public void setSource(String url) {
        this.sourceURL = url;
    }

    public void setValidity(int valid) {
        this.validity = valid;
    }

}
