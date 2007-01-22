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

    public String toFactString() {
        StringBuffer buf = new StringBuffer();
        buf.append("f-" + id + " (" + this.deftemplate.getName());
        if (this.slots.length > 0) {
            buf.append(" ");
        }
        for (int idx = 0; idx < this.slots.length; idx++) {
            buf.append("(" + this.slots[idx].getName() + " "
                    + ConversionUtils.formatSlot(this.slots[idx].value)
                    + ") ");
        }
        // append the temporal attributes
        buf.append("(" + TemporalFact.EXPIRATION + " " + this.expirationTime + ")");
        buf.append("(" + TemporalFact.SERVICE_TYPE + " " + this.serviceType + ")");
        buf.append("(" + TemporalFact.SOURCE + " " + this.sourceURL + ")");
        buf.append("(" + TemporalFact.VALIDITY + " " + this.validity + ")");
        buf.append(")");
        return buf.toString();
    }

    /**
     * the class overrides the method to include the additional
     * attributes.
     */
    public String toPPString() {
        StringBuffer buf = new StringBuffer();
        buf.append("(" + this.deftemplate.getName());
        if (this.slots.length > 0) {
            buf.append(" ");
        }
        for (int idx = 0; idx < this.slots.length; idx++) {
            if (this.slots[idx].value instanceof BoundParam) {
                BoundParam bp = (BoundParam) this.slots[idx].value;
                buf.append("(" + this.slots[idx].getName() + " ?"
                        + bp.getVariableName() + ") ");
            } else {
                buf.append("("
                        + this.slots[idx].getName()
                        + " "
                        + ConversionUtils
                                .formatSlot(this.slots[idx].value) + ") ");
            }
        }
        // append the temporal attributes
        buf.append("(" + TemporalFact.EXPIRATION + " " + this.expirationTime + ")");
        buf.append("(" + TemporalFact.SERVICE_TYPE + " " + this.serviceType + ")");
        buf.append("(" + TemporalFact.SOURCE + " " + this.sourceURL + ")");
        buf.append("(" + TemporalFact.VALIDITY + " " + this.validity + ") ");
        buf.append(")");
        return buf.toString();
    }
}
