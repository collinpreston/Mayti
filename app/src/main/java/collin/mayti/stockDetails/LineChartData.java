package collin.mayti.stockDetails;

/**
 * Created by chpreston on 3/16/18.
 */

public class LineChartData {
    public Double xCoordinate;
    public Double yCoordinate;

    public LineChartData(Double xValue, Double yValue) {
        this.xCoordinate = xValue;
        this.yCoordinate = yValue;
    }

    public Double getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(Double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public Double getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(Double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }
}
