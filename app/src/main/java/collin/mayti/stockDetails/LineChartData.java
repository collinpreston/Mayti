package collin.mayti.stockDetails;

/**
 * Created by chpreston on 3/16/18.
 */

public class LineChartData {
    public long xCoordinate;
    public long yCoordinate;

    LineChartData(long xValue, long yValue) {
        this.xCoordinate = xValue;
        this.yCoordinate = yValue;
    }

    public long getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(long xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public long getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(long yCoordinate) {
        this.yCoordinate = yCoordinate;
    }
}
