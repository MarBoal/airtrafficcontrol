import java.util.ArrayList;

enum FlightType {Arrival, Departure};
class Flight {
    String flightNumber;
    FlightType flightType;
    int minuteInQueue;
    int minuteOutQueue;


    public Flight(String flightNumber, FlightType flightType) {
        this.flightNumber = flightNumber;
        this.flightType = flightType;
    }

    public void setMinuteInQueue(int minute) {
        this.minuteInQueue = minute;
    }

    public void setMinuteOutQueue(int minute) {
        this.minuteOutQueue = minute;
    }

    public String toString() {
        return flightType + ": " + flightNumber + " MIQ " + minuteInQueue + " MOQ " + minuteOutQueue;
    }
    public static double timeInQueue(ArrayList<Flight> flights) {
        int totalMinutesInQueue = 0;
        for (Flight flight : flights) {
            totalMinutesInQueue += flight.minuteOutQueue - flight.minuteInQueue;
        }
        if (!flights.isEmpty()) {
            return (double) totalMinutesInQueue / flights.size();
        }
        return 0;
    }
}