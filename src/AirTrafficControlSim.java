import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class AirTrafficControlSim {
    ArrayDeque<Flight> arrivalQueue = new ArrayDeque<>();
    ArrayDeque<Flight> departureQueue = new ArrayDeque<>();
    ArrayList<Flight> arrivalStatistics = new ArrayList<>();
    ArrayList<Flight> departureStatistics = new ArrayList<>();
    static final int MIN_FLIGHT_SPACING=10;
    int timeInterval = 0;
    int flightNumberCounter =0;
    int numberOfDivertedArrivals =0;
    int numberOfDeniedDepartures =0;
    int numberOfArrivals = 0;
    int numberOfDepartures = 0;
    int timerCounter = 0;
    int arrivalQueueEmpty =0;
    int fltSpacingCounter =0;
    int departureQueueEmpty = 0;

    Random r = new Random(System.nanoTime());

    public int getPoissonRandom(double mean) {
        double L = Math.exp(-mean);
        int x = 0;
        double p = 1.0;
        do {
            p = p * r.nextDouble();
            x++;
        } while (p > L);
        return x - 1;
    }
    public void addToArrivalQueue(int count) {
        for (int i = 0; i < count; i++) {
            Flight arrivalFlight = new Flight("AA" + flightNumberCounter++, FlightType.Arrival);
            if (arrivalQueue.size() < 5) {
                arrivalFlight.setMinuteInQueue(timeInterval);
                arrivalQueue.add(arrivalFlight);
            } else {
                this.numberOfDivertedArrivals++;
                System.out.println("Arrival queue full. Flight " + arrivalFlight + " rerouted at: "  + timeInterval/60 + ":" + String.format("%02d",timeInterval % 60) +  " hours");
            }
        }
    }

    public void addToDepartureQueue(int count) {
        for (int i = 0; i < count; i++) {
            Flight departureFlight = new Flight("UA" + flightNumberCounter++, FlightType.Departure);
            if (departureQueue.size() < 5) {
                departureFlight.setMinuteInQueue(timeInterval);
                departureQueue.add(departureFlight);
            } else {
                this.numberOfDeniedDepartures++;
                System.out.println("Departure queue full. Flight " + departureFlight + " delayed at: "  + timeInterval/60 + ":" + String.format("%02d",timeInterval % 60) +  " hours");
            }
        }
    }


    public void removeFromDepartureQueue() {
        if (departureQueue.size() > 0) {
            Flight departureFlight = departureQueue.removeFirst();
            departureFlight.setMinuteOutQueue(timeInterval);
            departureStatistics.add(departureFlight);
            System.out.println("Flight " + departureFlight + " left at: " +      timeInterval/60 + ":" + String.format("%02d",timeInterval % 60) + " hours");
            numberOfDepartures++;
        }
    }

    public void removeFromArrivalQueue() {
        if (arrivalQueue.size() > 0) {
            Flight arrivalFlight = arrivalQueue.removeFirst();
            arrivalFlight.setMinuteOutQueue(timeInterval);
            arrivalStatistics.add(arrivalFlight);
            System.out.println("Flight " + arrivalFlight + " arrived at: " +     + timeInterval/60 + ":" + String.format("%02d",timeInterval % 60) + " hours");
            numberOfArrivals++;
        }
    }
    void processArrival(double meanArrivalFreq) {
        int count = 0;
        if ((count = getPoissonRandom(meanArrivalFreq)) > 0)
            addToArrivalQueue(count);
        if (timerCounter >= MIN_FLIGHT_SPACING) {
            if (arrivalQueue.size() > 0) {
                removeFromArrivalQueue();
                timerCounter = 0;
            }
        }
    }

    public void processDeparture(double meanDepartureFreq) {
        int count = 0;
        if ((count = getPoissonRandom(meanDepartureFreq)) > 0)
            addToDepartureQueue(count);
        if (fltSpacingCounter >= MIN_FLIGHT_SPACING) {
            if (departureQueue.size() > 0 && arrivalQueue.size() > 0 ) {
                removeFromDepartureQueue();
                fltSpacingCounter = 0;
            }
        }
    }


    public static void main(String[] args) {
        AirTrafficControlSim atc = new AirTrafficControlSim();
        atc.doSimulation();
    }
    public void doSimulation() {
        double meanArrivalFreq = 0.0;
        double meanDepartureFreq = 0.0;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter mean departure frequency (0.0 > df < 1.0): ");
        if  (scanner.hasNextDouble())
            meanDepartureFreq = scanner.nextDouble();
        System.out.println("Enter mean arrival frequency   0.0 > af < 1.0): ");
        if  (scanner.hasNextDouble())
            meanArrivalFreq =  scanner.nextDouble();
        if ( meanDepartureFreq + meanArrivalFreq > 1.0) {
            System.out.println("Mean departure frequency plus mean arrival frequency cannot exceed 100%. Try again...");
            return;
        }
        for (int i = 0; i < 1440; i++) {
            timerCounter++;
            processArrival(meanArrivalFreq);
            processDeparture(meanDepartureFreq);
            if (arrivalQueue.size() == 0 )
                arrivalQueueEmpty ++;
            if (departureQueue.size() == 0 )
                departureQueueEmpty++;
            timeInterval++;
            fltSpacingCounter++;
        }
        printSimSummaryStatistics();
    }

    public void printSimSummaryStatistics() {
        System.out.println("===========================================\n" + "Satistics: " + "\n===========================================\n" +
                        "Planes arrived: " + arrivalStatistics +
                        "\n===========================================\n" +
                        "Planes departed: " + departureStatistics +
                        "\n===========================================\n" +
                        "Number of arrivals: " + numberOfArrivals +
                        "\nNumber of departures: " + numberOfDepartures +
                        "\nNumber of diverted arrivals: " + numberOfDivertedArrivals +
                        "\nNumber of delayed departures: " + numberOfDeniedDepartures +
                        "\nNumber of flights handled: " + (numberOfArrivals + numberOfDepartures) +
                        "\nNumber of arrivals in queue: " + arrivalQueue.size() +
                        "\nNumber of departures in queue: " + departureQueue.size() +
                        "\nTimes arrival queue was empty: " + arrivalQueueEmpty +
                        "\nTimes departing queue was empty: " + departureQueueEmpty +
                        "\nArrivals per hour: " +  (double) numberOfArrivals / 24 +
                        "\nDepartures per hour: " + (double) (numberOfDepartures / 24) +
                        "\nAverage time in queue for arrivals: " + Flight.timeInQueue(arrivalStatistics) + " minutes" +
                        "\nAverage time in queue for departures: " + Flight.timeInQueue(departureStatistics) + " minutes" +
                        "\nTime Passed: " + timeInterval / 60 + " Hours" +
                        "\n===========================================\n"
                    );
    }
}
