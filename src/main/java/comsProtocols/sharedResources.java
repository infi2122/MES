package comsProtocols;

public class sharedResources {

    private long startTime;
    private String internalOrdersConcat;
    private String finishedOrdersTimes;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getInternalOrdersConcat() {
        return internalOrdersConcat;
    }

    public void setInternalOrdersConcat(String internalOrdersConcat) {
        this.internalOrdersConcat = internalOrdersConcat;
    }

    public String getFinishedOrdersTimes() {
        return finishedOrdersTimes;
    }

    public void setFinishedOrdersTimes(String finishedOrdersTimes) {
        this.finishedOrdersTimes = finishedOrdersTimes;
    }
}
