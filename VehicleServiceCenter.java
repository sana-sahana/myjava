import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

// Custom exception for job-related errors
class InvalidJobException extends Exception {
    public InvalidJobException(String message) {
        super(message);
    }
}

// Custom exception for billing-related errors
class InvalidBillException extends Exception {
    public InvalidBillException(String message) {
        super(message);
    }
}

// Stores vehicle details
class Vehicle {
    private final String vehicleNumber;
    private String ownerName;
    private String model;

    public Vehicle(String vehicleNumber, String ownerName, String model) {
        if (vehicleNumber == null || ownerName == null || model == null || vehicleNumber.trim().isEmpty() || ownerName.trim().isEmpty() || model.trim().isEmpty()) throw new IllegalArgumentException("Vehicle details cannot be empty.");
        this.vehicleNumber = vehicleNumber.trim().toUpperCase(Locale.ROOT);
        this.ownerName = ownerName.trim();
        this.model = model.trim();
    }

    // Getters allow other classes to access private data
    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getModel() {
        return model;
    }

    public void display() {
        System.out.println("Vehicle Number: " + vehicleNumber);
        System.out.println("Owner Name   : " + ownerName);
        System.out.println("Model        : " + model);
    }
}

// Abstract class that defines common service behavior
abstract class Service {
    public abstract String getServiceType();

    public abstract double getServiceCharge();
}

// General service with a fixed charge
class GeneralService extends Service {
    @Override
    public String getServiceType() {
        return "General Service";
    }

    @Override
    public double getServiceCharge() {
        return 500;
    }
}

// Repair service with a different charge
class RepairService extends Service {
    @Override
    public String getServiceType() {
        return "Repair Service";
    }

    @Override
    public double getServiceCharge() {
        return 1000;
    }
}

// Stores details of spare parts used in a job
class SparePart {
    private String name;
    private int quantity;
    private double price;

    public SparePart(String name, int quantity, double price)
            throws InvalidBillException {

        if (name == null || name.trim().isEmpty()) {
            throw new InvalidBillException("Part name cannot be empty.");
        }

        if (quantity <= 0 || price <= 0) {
            throw new InvalidBillException(
                    "Quantity and price must be greater than zero.");
        }

        this.name = name.trim();
        this.quantity = quantity;
        this.price = price;
    }

    // Calculates total cost for this spare part
    public double getTotal() {
        return quantity * price;
    }

    public void display() {
        System.out.printf(Locale.US,
                "%s | Quantity: %d | Price: Rs. %.2f | Total: Rs. %.2f%n",
                name, quantity, price, getTotal());
    }
}

// Stores all information related to a service job
class JobCard {
    private static int nextJobId = 1001;

    private int jobId;
    private Vehicle vehicle;
    private String issue;
    private Service service;
    private ArrayList<SparePart> parts;
    private String status;

    public JobCard(Vehicle vehicle, String issue, Service service)
            throws InvalidJobException {

        if (vehicle == null || issue == null || issue.trim().isEmpty() || service == null) {
            throw new InvalidJobException("Invalid job card details.");
        }

        this.jobId = nextJobId++;
        this.vehicle = vehicle;
        this.issue = issue.trim();
        this.service = service;
        this.parts = new ArrayList<>();
        this.status = "Pending";
    }

    public int getJobId() {
        return jobId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Adds a spare part to this job card
    public void addPart(SparePart part) {
        parts.add(part);
    }

    // Calculates the total cost of all spare parts
    public double getPartsCost() {
        double total = 0;

        for (SparePart part : parts) {
            total += part.getTotal();
        }

        return total;
    }

    // Calculates service charge plus spare parts cost
    public double calculateBill() {
        return service.getServiceCharge() + getPartsCost();
    }

    // Displays complete job card information
    public void display() {
        System.out.println("\nJob Card Details");
        System.out.println("Job ID         : " + jobId);
        vehicle.display();
        System.out.println("Issue          : " + issue);
        System.out.println("Service Type   : " + service.getServiceType());
        System.out.println("Status         : " + status);
        System.out.printf(Locale.US, "Service Charge : Rs. %.2f%n",
                service.getServiceCharge());

        System.out.println("Spare Parts:");

        if (parts.isEmpty()) {
            System.out.println("No spare parts added.");
        } else {
            for (SparePart part : parts) {
                part.display();
            }
        }

        System.out.printf(Locale.US, "Parts Cost    : Rs. %.2f%n",
                getPartsCost());

        System.out.printf(Locale.US, "Total Bill    : Rs. %.2f%n",
                calculateBill());
    }

    // Displays the final bill for the job
    public void displayBillReceipt() {
        System.out.println("\nBill Receipt");
        System.out.println("Job ID       : " + jobId);
        System.out.println("Customer     : " + vehicle.getOwnerName());
        System.out.println("Vehicle      : " + vehicle.getVehicleNumber());
        System.out.println("Service      : " + service.getServiceType());

        System.out.println("Spare Parts:");

        if (parts.isEmpty()) {
            System.out.println("No spare parts used.");
        } else {
            for (SparePart part : parts) {
                part.display();
            }
        }

        System.out.printf(Locale.US, "Service Cost : Rs. %.2f%n",
                service.getServiceCharge());

        System.out.printf(Locale.US, "Parts Cost   : Rs. %.2f%n",
                getPartsCost());

        System.out.printf(Locale.US, "Total        : Rs. %.2f%n",
                calculateBill());
    }
}

// Self-implemented queue using a linked list
class ServiceQueue {

    // Each node stores a job ID and a reference to the next node
    private class Node {
        int jobId;
        Node next;

        Node(int jobId) {
            this.jobId = jobId;
        }
    }

    private Node front;
    private Node rear;

    // Adds a job to the end of the queue
    public void addJob(int jobId) {
        Node newNode = new Node(jobId);

        if (rear == null) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
    }

    // Removes the oldest job from the queue
    public Integer removeJob() {
        if (front == null) {
            return null;
        }

        int jobId = front.jobId;
        front = front.next;

        if (front == null) {
            rear = null;
        }

        return jobId;
    }

    // Removes a particular job from the queue
    public void remove(int jobId) {
        Node current = front;
        Node previous = null;

        while (current != null) {
            if (current.jobId == jobId) {

                if (previous == null) {
                    front = current.next;
                } else {
                    previous.next = current.next;
                }

                if (current == rear) {
                    rear = previous;
                }

                return;
            }

            previous = current;
            current = current.next;
        }
    }

    // Checks whether a job is already in the queue
    public boolean contains(int jobId) {
        Node current = front;

        while (current != null) {
            if (current.jobId == jobId) {
                return true;
            }

            current = current.next;
        }

        return false;
    }

    public boolean isEmpty() {
        return front == null;
    }

    // Displays jobs waiting to be serviced
    public void display(HashMap<Integer, JobCard> jobs) {
        if (isEmpty()) {
            System.out.println("No pending jobs.");
            return;
        }

        System.out.println("\nWaiting Queue");

        Node current = front;

        while (current != null) {
            JobCard job = jobs.get(current.jobId);

            if (job != null && job.getStatus().equals("Pending")) {
                System.out.println("Job ID: " + job.getJobId()
                        + " | Vehicle: "
                        + job.getVehicle().getVehicleNumber());
            }

            current = current.next;
        }
    }
}

// Manages vehicles, job cards, billing, and the service queue
class VehicleManager {

    private ArrayList<Vehicle> vehicles = new ArrayList<>();
    private ArrayList<JobCard> jobCards = new ArrayList<>();
    private HashMap<Integer, JobCard> jobMap = new HashMap<>();
    private ServiceQueue queue = new ServiceQueue();

    // Registers a new vehicle
    public boolean registerVehicle(String number, String owner, String model) {
        if (searchVehicle(number) != null) {
            System.out.println("Vehicle is already registered.");
            return false;
        }

        vehicles.add(new Vehicle(number, owner, model));
        System.out.println("Vehicle registered successfully.");
        return true;
    }

    // Searches for a vehicle using its registration number
    public Vehicle searchVehicle(String number) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVehicleNumber().equalsIgnoreCase(number.trim())) {
                return vehicle;
            }
        }

        return null;
    }

    // Creates a job card for a registered vehicle
    public JobCard createJob(String number, String issue, int type) {
        Vehicle vehicle = searchVehicle(number);

        if (vehicle == null) {
            System.out.println("Vehicle is not registered.");
            return null;
        }

        Service service;

        if (type == 1) {
            service = new GeneralService();
        } else if (type == 2) {
            service = new RepairService();
        } else {
            System.out.println("Invalid service type.");
            return null;
        }

        try {
            JobCard job = new JobCard(vehicle, issue, service);

            jobCards.add(job);
            jobMap.put(job.getJobId(), job);
            queue.addJob(job.getJobId());

            System.out.println("Job card created successfully.");
            System.out.println("Job ID: " + job.getJobId());

            return job;

        } catch (InvalidJobException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // Searches for a job card using its ID
    public JobCard findJob(int id) throws InvalidJobException {
        JobCard job = jobMap.get(id);

        if (job == null) {
            throw new InvalidJobException("Job ID not found.");
        }

        return job;
    }

    // Displays all registered vehicles
    public void displayVehicles() {
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles registered.");
            return;
        }

        for (Vehicle vehicle : vehicles) {
            System.out.println();
            vehicle.display();
        }
    }

    // Displays all job cards
    public void displayJobs() {
        if (jobCards.isEmpty()) {
            System.out.println("No job cards available.");
            return;
        }

        for (JobCard job : jobCards) {
            job.display();
        }
    }

    // Updates the status of a job card
    public void updateStatus(int id, int choice) {
        try {
            JobCard job = findJob(id);

            String newStatus;

            if (choice == 1) {
                newStatus = "Pending";
            } else if (choice == 2) {
                newStatus = "In Progress";
            } else if (choice == 3) {
                newStatus = "Completed";
            } else {
                System.out.println("Invalid status choice.");
                return;
            }

            // Completed jobs cannot be reopened
            if (job.getStatus().equals("Completed")
                    && !newStatus.equals("Completed")) {
                System.out.println("Completed jobs cannot be reopened.");
                return;
            }

            // Remove the job from the queue before updating its status
            queue.remove(id);
            job.setStatus(newStatus);

            // Only pending jobs should remain in the waiting queue
            if (newStatus.equals("Pending")) {
                if (!queue.contains(id)) {
                    queue.addJob(id);
                }
            }

            System.out.println("Status updated successfully.");

        } catch (InvalidJobException e) {
            System.out.println(e.getMessage());
        }
    }

    // Adds a spare part to an existing job card
    public void addParts(int id, String name, int quantity, double price) {
        try {
            JobCard job = findJob(id);

            if (job.getStatus().equals("Completed")) {
                throw new InvalidBillException(
                        "Cannot add parts to a completed job.");
            }

            SparePart part = new SparePart(name, quantity, price);
            job.addPart(part);

            System.out.println("Spare part added successfully.");

        } catch (InvalidJobException | InvalidBillException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Generates and displays the bill for a job
    public void generateBill(int id) {
        try {
            JobCard job = findJob(id);
            job.displayBillReceipt();

        } catch (InvalidJobException e) {
            System.out.println(e.getMessage());
        }
    }

    // Displays all previous jobs for a particular vehicle
    public void serviceHistory(String number) {
        Vehicle vehicle = searchVehicle(number);

        if (vehicle == null) {
            System.out.println("Vehicle not found.");
            return;
        }

        boolean found = false;

        System.out.println("\nService History for "
                + vehicle.getVehicleNumber());

        for (JobCard job : jobCards) {
            if (job.getVehicle().getVehicleNumber()
                    .equals(vehicle.getVehicleNumber())) {
                job.display();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No service history available.");
        }
    }

    // Processes the oldest pending job in the queue
    public void processNextJob() {
        while (!queue.isEmpty()) {
            Integer id = queue.removeJob();
            JobCard job = jobMap.get(id);

            if (job != null && job.getStatus().equals("Pending")) {
                job.setStatus("In Progress");

                System.out.println("Job " + id
                        + " is now In Progress.");

                return;
            }
        }

        System.out.println("No pending jobs.");
    }

    // Displays the waiting queue
    public void displayQueue() {
        queue.display(jobMap);
    }
}

// Main class that provides the menu and accepts user input
public class VehicleServiceCenter {

    private static Scanner sc = new Scanner(System.in);
    private static VehicleManager manager = new VehicleManager();

    public static void main(String[] args) {
        int choice;

        do {
            displayMenu();
            choice = readInt("Enter choice: ");

            switch (choice) {
                case 1:
                    registerVehicle();
                    break;

                case 2:
                    manager.displayVehicles();
                    break;

                case 3:
                    searchVehicle();
                    break;

                case 4:
                    createJob();
                    break;

                case 5:
                    manager.displayJobs();
                    break;

                case 6:
                    updateStatus();
                    break;

                case 7:
                    addParts();
                    break;

                case 8:
                    generateBill();
                    break;

                case 9:
                    serviceHistory();
                    break;

                case 10:
                    manager.displayQueue();
                    break;

                case 11:
                    manager.processNextJob();
                    break;

                case 0:
                    System.out.println("Exiting Vehicle Service Center.");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

        } while (choice != 0);

        sc.close();
    }

    // Displays all available operations
    private static void displayMenu() {
        System.out.println("\nVehicle Service Center Menu");
        System.out.println("1. Register Vehicle");
        System.out.println("2. Display Vehicles");
        System.out.println("3. Search Vehicle");
        System.out.println("4. Create Job Card");
        System.out.println("5. Display Job Cards");
        System.out.println("6. Update Service Status");
        System.out.println("7. Add Spare Parts");
        System.out.println("8. Generate Bill");
        System.out.println("9. Service History");
        System.out.println("10. View Waiting Queue");
        System.out.println("11. Process Next Job");
        System.out.println("0. Exit");
    }

    // Accepts vehicle information and registers it
    private static void registerVehicle() {
        String number = readText("Vehicle number: ");
        String owner = readText("Owner name: ");
        String model = readText("Vehicle model: ");

        manager.registerVehicle(number, owner, model);
    }

    // Searches for a vehicle and displays its details
    private static void searchVehicle() {
        String number = readText("Enter vehicle number: ");
        Vehicle vehicle = manager.searchVehicle(number);

        if (vehicle == null) {
            System.out.println("Vehicle not found.");
        } else {
            vehicle.display();
        }
    }

    // Accepts information required to create a job card
    private static void createJob() {
        String number = readText("Vehicle number: ");
        String issue = readText("Issue description: ");

        System.out.println("1. General Service (Rs. 500)");
        System.out.println("2. Repair Service (Rs. 1000)");

        int type = readInt("Choose service type: ");

        manager.createJob(number, issue, type);
    }

    // Accepts a job ID and updates its service status
    private static void updateStatus() {
        int id = readInt("Job ID: ");

        System.out.println("1. Pending");
        System.out.println("2. In Progress");
        System.out.println("3. Completed");

        int status = readInt("Choose status: ");

        manager.updateStatus(id, status);
    }

    // Accepts spare part information and adds it to a job
    private static void addParts() {
        int id = readInt("Job ID: ");
        String name = readText("Part name: ");
        int quantity = readInt("Quantity: ");
        double price = readDouble("Price per unit: ");

        manager.addParts(id, name, quantity, price);
    }

    // Requests a job ID and displays its bill
    private static void generateBill() {
        int id = readInt("Job ID: ");
        manager.generateBill(id);
    }

    // Requests a vehicle number and displays its service history
    private static void serviceHistory() {
        String number = readText("Vehicle number: ");
        manager.serviceHistory(number);
    }

    // Reads non-empty text input from the user
    private static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("Input cannot be empty.");
        }
    }

    // Reads an integer and handles invalid input
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();

            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    // Reads a valid decimal value
    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();

            try {
                double value = Double.parseDouble(input.trim());

                if (value > 0) {
                    return value;
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid price.");
            }

            System.out.println("Price must be greater than zero.");
        }
    }
}
