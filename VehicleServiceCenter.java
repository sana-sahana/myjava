import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Locale;

// ================= CUSTOM EXCEPTIONS =================

class InvalidJobException extends Exception {
    public InvalidJobException(String message) {
        super(message);
    }
}

class InvalidBillException extends Exception {
    public InvalidBillException(String message) {
        super(message);
    }
}

// ================= VEHICLE CLASS =================

class Vehicle {
    private final String vehicleNumber;
    private String ownerName;
    private String model;

    public Vehicle(String number, String owner, String model) {
        if (number.trim().isEmpty() || owner.trim().isEmpty()
                || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Fields cannot be empty.");
        }

        vehicleNumber = number.trim().toUpperCase(Locale.ROOT);
        ownerName = owner.trim();
        this.model = model.trim();
    }

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
        System.out.println("Owner: " + ownerName);
        System.out.println("Model: " + model);
    }
}

// ================= ABSTRACTION =================

abstract class Service {
    public abstract String getServiceType();
    public abstract double getServiceCharge();
}

// ================= INHERITANCE =================

class GeneralService extends Service {
    public String getServiceType() {
        return "General Service";
    }

    public double getServiceCharge() {
        return 500;
    }
}

class RepairService extends Service {
    public String getServiceType() {
        return "Repair Service";
    }

    public double getServiceCharge() {
        return 1000;
    }
}

// ================= SPARE PART CLASS =================

class SparePart {
    private String name;
    private int quantity;
    private double price;

    public SparePart(String name, int quantity, double price)
            throws InvalidBillException {

        if (name.trim().isEmpty() || quantity <= 0 || price <= 0) {
            throw new InvalidBillException("Invalid spare part details!");
        }

        this.name = name.trim();
        this.quantity = quantity;
        this.price = price;
    }

    public double getTotal() {
        return quantity * price;
    }

    public void display() {
        System.out.println(name + " x " + quantity + " = Rs." + getTotal());
    }
}

// ================= JOB CARD =================

class JobCard {
    private final int jobId;
    private final Vehicle vehicle;
    private final String issue;
    private Service service;
    private String status = "Pending";

    private ArrayList<SparePart> parts = new ArrayList<>();
    private double bill;

    public JobCard(int id, Vehicle vehicle, String issue, Service service) {
        jobId = id;
        this.vehicle = vehicle;
        this.issue = issue;
        this.service = service;
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

    public void updateStatus(String status) {
        this.status = status;
    }

    public void addPart(SparePart part) {
        parts.add(part);
    }

    public double getPartsCost() {
        double total = 0;

        for (SparePart part : parts) {
            total += part.getTotal();
        }

        return total;
    }

    public double calculateBill() {
        return service.getServiceCharge() + getPartsCost();
    }

    public void setBill(double bill) {
        this.bill = bill;
    }

    public void display() {
        System.out.println("\n========== JOB CARD ==========");
        System.out.println("Job ID: " + jobId);
        vehicle.display();
        System.out.println("Issue: " + issue);
        System.out.println("Service Type: " + service.getServiceType());
        System.out.println("Status: " + status);
        System.out.println("Service Charge: Rs." + service.getServiceCharge());

        System.out.println("Spare Parts:");
        if (parts.isEmpty()) {
            System.out.println("No spare parts added.");
        } else {
            for (SparePart part : parts) {
                part.display();
            }
        }

        System.out.println("Parts Cost: Rs." + getPartsCost());
        System.out.println("Total Bill: Rs." + calculateBill());
        System.out.println("============================");
    }
}

// ================= SELF-IMPLEMENTED QUEUE =================

class ServiceQueue {
    private Node front, rear;

    // Linked-list node
    private class Node {
        int jobId;
        Node next;

        Node(int id) {
            jobId = id;
        }
    }

    // Adds a job at the rear of the queue
    public void addJob(int id) {
        Node node = new Node(id);

        if (rear == null) {
            front = rear = node;
        } else {
            rear.next = node;
            rear = node;
        }
    }

    // Removes the oldest job (FIFO)
    public int removeJob() {
        if (front == null) {
            return -1;
        }

        int id = front.jobId;
        front = front.next;

        if (front == null) {
            rear = null;
        }

        return id;
    }

    // Removes a particular job when its status changes
    public void remove(int id) {
        Node current = front, previous = null;

        while (current != null) {
            if (current.jobId == id) {
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

    public boolean contains(int id) {
        Node current = front;

        while (current != null) {
            if (current.jobId == id) {
                return true;
            }
            current = current.next;
        }

        return false;
    }

    public void showJobs() {
        if (front == null) {
            System.out.println("No pending jobs.");
            return;
        }

        System.out.print("Waiting Queue: ");

        Node current = front;

        while (current != null) {
            System.out.print(current.jobId + " -> ");
            current = current.next;
        }

        System.out.println("END");
    }
}

// ================= VEHICLE MANAGER =================

class VehicleManager {
    private ArrayList<Vehicle> vehicles = new ArrayList<>();
    private ArrayList<JobCard> jobs = new ArrayList<>();
    private HashMap<Integer, JobCard> jobMap = new HashMap<>();

    private ServiceQueue queue = new ServiceQueue();
    private int nextId = 1001;

    // Registers a new vehicle
    public void registerVehicle(String number, String owner, String model) {
        try {
            number = number.trim().toUpperCase(Locale.ROOT);

            if (searchVehicle(number) != null) {
                System.out.println("Vehicle already registered!");
                return;
            }

            Vehicle vehicle = new Vehicle(number, owner, model);
            vehicles.add(vehicle);

            System.out.println("Vehicle registered successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    // Searches using vehicle number
    public Vehicle searchVehicle(String number) {
        if (number == null) {
            return null;
        }

        number = number.trim().toUpperCase(Locale.ROOT);

        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVehicleNumber().equals(number)) {
                return vehicle;
            }
        }

        return null;
    }

    // Creates a job card for a registered vehicle
    public void createJob(String number, String issue, int type) {
        Vehicle vehicle = searchVehicle(number);

        if (vehicle == null) {
            System.out.println("Vehicle is not registered!");
            return;
        }

        Service service;

        if (type == 1) {
            service = new GeneralService();
        } else if (type == 2) {
            service = new RepairService();
        } else {
            System.out.println("Invalid service type!");
            return;
        }

        JobCard job = new JobCard(nextId++, vehicle, issue, service);

        jobs.add(job);
        jobMap.put(job.getJobId(), job);
        queue.addJob(job.getJobId());

        System.out.println("Job card created!");
        System.out.println("Job ID: " + job.getJobId());
    }

    // Finds a job using its ID
    public JobCard findJob(int id) throws InvalidJobException {
        JobCard job = jobMap.get(id);

        if (job == null) {
            throw new InvalidJobException("Job ID not found!");
        }

        return job;
    }

    // Displays registered vehicles
    public void displayVehicles() {
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles registered.");
            return;
        }

        for (Vehicle vehicle : vehicles) {
            System.out.println("\n----------------");
            vehicle.display();
        }
    }

    // Displays all job cards
    public void displayJobs() {
        if (jobs.isEmpty()) {
            System.out.println("No job cards found.");
            return;
        }

        for (JobCard job : jobs) {
            job.display();
        }
    }

    // Updates service status
    public void updateStatus(int id, int choice) {
        try {
            JobCard job = findJob(id);

            if (choice < 1 || choice > 3) {
                throw new InvalidJobException("Invalid status!");
            }

            String status = choice == 1 ? "Pending"
                    : choice == 2 ? "In Progress" : "Completed";

            queue.remove(id);
            job.updateStatus(status);

            if (choice == 1 && !queue.contains(id)) {
                queue.addJob(id);
            }

            System.out.println("Status updated successfully!");

        } catch (InvalidJobException e) {
            System.out.println(e.getMessage());
        }
    }

    // Adds spare parts to a job card
    public void addParts(int id, String name, int quantity, double price) {
        try {
            JobCard job = findJob(id);
            SparePart part = new SparePart(name, quantity, price);

            job.addPart(part);
            System.out.println("Spare part added successfully!");

        } catch (InvalidJobException | InvalidBillException e) {
            System.out.println(e.getMessage());
        }
    }

    // Generates the bill
    public void generateBill(int id) {
        try {
            JobCard job = findJob(id);

            double total = job.calculateBill();

            if (total < 0) {
                throw new InvalidBillException("Invalid bill amount!");
            }

            job.setBill(total);

            System.out.println("\n========== BILL ==========");
            job.display();
            System.out.println("Final Amount: Rs." + total);
            System.out.println("========================");

        } catch (InvalidJobException | InvalidBillException e) {
            System.out.println(e.getMessage());
        }
    }

    // Displays service history for a vehicle
    public void serviceHistory(String number) {
        boolean found = false;

        for (JobCard job : jobs) {
            if (job.getVehicle().getVehicleNumber()
                    .equalsIgnoreCase(number.trim())) {

                job.display();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No service history found.");
        }
    }

    // Processes the oldest waiting job
    public void processNextJob() {
        int id = queue.removeJob();

        if (id == -1) {
            System.out.println("Queue is empty.");
            return;
        }

        try {
            JobCard job = findJob(id);

            if (job.getStatus().equals("Completed")) {
                System.out.println("Job already completed.");
                return;
            }

            job.updateStatus("In Progress");
            System.out.println("Job " + id + " is now In Progress.");

        } catch (InvalidJobException e) {
            System.out.println(e.getMessage());
        }
    }

    public void displayQueue() {
        queue.showJobs();
    }
}

// ================= MAIN PROGRAM =================

public class VehicleServiceCenter {
    private static Scanner sc = new Scanner(System.in);
    private static VehicleManager manager = new VehicleManager();

    public static void main(String[] args) {
        int choice;

        do {
            System.out.println("\n===== VEHICLE SERVICE CENTER =====");
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
                    System.out.println("Thank you for visiting!");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 0);

        sc.close();
    }

    // ================= MENU FUNCTIONS =================

    private static void registerVehicle() {
        String number = readText("Vehicle number: ");
        String owner = readText("Owner name: ");
        String model = readText("Vehicle model: ");

        manager.registerVehicle(number, owner, model);
    }

    private static void searchVehicle() {
        String number = readText("Enter vehicle number: ");
        Vehicle vehicle = manager.searchVehicle(number);

        if (vehicle == null) {
            System.out.println("Vehicle not found.");
        } else {
            vehicle.display();
        }
    }

    private static void createJob() {
        String number = readText("Vehicle number: ");
        String issue = readText("Issue description: ");

        System.out.println("1. General Service (Rs.500)");
        System.out.println("2. Repair Service (Rs.1000)");

        int type = readInt("Service type: ");

        manager.createJob(number, issue, type);
    }

    private static void updateStatus() {
        int id = readInt("Job ID: ");

        System.out.println("1. Pending");
        System.out.println("2. In Progress");
        System.out.println("3. Completed");

        int status = readInt("Choose status: ");

        manager.updateStatus(id, status);
    }

    private static void addParts() {
        int id = readInt("Job ID: ");
        String name = readText("Part name: ");
        int quantity = readInt("Quantity: ");
        double price = readDouble("Price per unit: ");

        manager.addParts(id, name, quantity, price);
    }

    private static void generateBill() {
        int id = readInt("Job ID: ");
        manager.generateBill(id);
    }

    private static void serviceHistory() {
        String number = readText("Vehicle number: ");
        manager.serviceHistory(number);
    }

    // ================= INPUT METHODS =================

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

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);

            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid integer.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);

            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
            }
        }
    }
}