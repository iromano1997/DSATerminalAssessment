import java.io.*;
import java.util.*;

public class DSAMilestone2 {
    static final String FILE_NAME = "MotorPH Inventory Data - March 2023 Inventory Data.csv";
    static List<String[]> inventory = new ArrayList<>();
    static Map<String, List<String[]>> brandMap = new HashMap<>();
    static Map<String, String[]> engineMap = new HashMap<>();
    static TreeSet<String[]> brandBST = new TreeSet<>(Comparator.comparing(o -> o[2]));
    static TreeSet<String[]> engineBST = new TreeSet<>(Comparator.comparing(o -> o[3]));

    public static void main(String[] args) {
        loadInventory();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nWelcome to MotorPH Inventory System");
            System.out.println("Dito ka na! The Filipino's Choice!");
            System.out.println("1. Add a motorcycle");
            System.out.println("2. View motorcycles");
            System.out.println("3. Search motorcycle by Engine Number");
            System.out.println("4. Delete motorcycle");
            System.out.println("5. Sort motorcycles by their latest date");
            System.out.println("6. Edit motorcycle details");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1: addMotorcycle(scanner); break;
                case 2: viewMotorcycles(); break;
                case 3: searchEngineCode(scanner); break;
                case 4: deleteMotorcycle(scanner); break;
                case 5: sortAndDisplayMotorcycles(); break;
                case 6: editMotorcycle(scanner); break;
                case 7: saveAndExit(); break;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }
    
    private static void loadInventory() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] bike = line.split(",");
                inventory.add(bike);
                brandMap.computeIfAbsent(bike[2], k -> new ArrayList<>()).add(bike);
                engineMap.put(bike[3], bike);
                brandBST.add(bike);
                engineBST.add(bike);
            }
        } catch (IOException e) {
            System.out.println("Inventory file not found.");
        }
    }

    private static void addMotorcycle(Scanner scanner) {
        System.out.print("Enter engine number: ");
        String engineNumber = scanner.nextLine();
        if (engineMap.containsKey(engineNumber)) {
            System.out.println("Engine number already exists!");
            return;
        }
        System.out.print("Enter brand: ");
        String brand = scanner.nextLine();
        String date = new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date());
        String[] newEntry = {date, "New", brand, engineNumber, "On-hand"};
        inventory.add(newEntry);
        brandMap.computeIfAbsent(brand, k -> new ArrayList<>()).add(newEntry);
        engineMap.put(engineNumber, newEntry);
        brandBST.add(newEntry);
        engineBST.add(newEntry);
        System.out.println("Motorcycle added successfully!");
    }

    private static void viewMotorcycles() {
        System.out.println("\nInventory:");
        for (String[] bike : inventory) {
            System.out.println(String.join(" | ", bike));
        }
    }

    private static void searchEngineCode(Scanner scanner) {
        System.out.print("Enter Engine Number: ");
        String term = scanner.nextLine();
        for (String[] bike : engineBST) {
            if (bike[3].equalsIgnoreCase(term)) {
                System.out.println(String.join(" | ", bike));
                return;
            }
        }
        System.out.println("Engine Number not found.");
    }

    private static void deleteMotorcycle(Scanner scanner) {
        System.out.print("Enter engine number to delete: ");
        String engineNumber = scanner.nextLine();
        Iterator<String[]> iterator = inventory.iterator();
        while (iterator.hasNext()) {
            String[] bike = iterator.next();
            if (bike[3].equalsIgnoreCase(engineNumber)) {
                if (bike[1].equals("Old") && bike[4].equals("Sold")) {
                    iterator.remove();
                    brandMap.get(bike[2]).remove(bike);
                    brandBST.remove(bike);
                    engineBST.remove(bike);
                    System.out.println("Sold motorcycle confirmed.");
                    System.out.println("Motorcycle has been removed.");
                    return;
                } else {
                    System.out.println("Motorcycle is still available. Cannot be deleted!");
                    return;
                }
            }
        }
        System.out.println("Motorcycle not found.");
    }

    private static void sortAndDisplayMotorcycles() {
        String[][] array = inventory.toArray(new String[0][]);
        mergeSort(array, 0, array.length - 1);
        System.out.println("\nMotorcycles sorted by latest date:");
        for (String[] bike : array) {
            System.out.println(String.join(" | ", bike));
        }
    }

    private static void mergeSort(String[][] array, int left, int right) {
        if (left < right) {
            int middle = (left + right) / 2;
            mergeSort(array, left, middle);
            mergeSort(array, middle + 1, right);
            merge(array, left, middle, right);
        }
    }

    private static void merge(String[][] array, int left, int middle, int right) {
        int n1 = middle - left + 1;
        int n2 = right - middle;

        String[][] leftArray = new String[n1][];
        String[][] rightArray = new String[n2][];

        System.arraycopy(array, left, leftArray, 0, n1);
        System.arraycopy(array, middle + 1, rightArray, 0, n2);

        int i = 0, j = 0;
        int k = left;
        while (i < n1 && j < n2) {
            if (leftArray[i][0].compareTo(rightArray[j][0]) >= 0) {
                array[k] = leftArray[i];
                i++;
            } else {
                array[k] = rightArray[j];
                j++;
            }
            k++;
        }
        while (i < n1) {
            array[k] = leftArray[i];
            i++;
            k++;
        }
        while (j < n2) {
            array[k] = rightArray[j];
            j++;
            k++;
        }
    }
    
    private static void editMotorcycle(Scanner scanner) {
        System.out.print("Enter engine number to edit: ");
        String engineNumber = scanner.nextLine();
        String[] bike = engineMap.get(engineNumber);
        if (bike == null) {
            System.out.println("Motorcycle not found.");
            return;
        }
        System.out.println("Current details: " + String.join(" | ", bike));
        System.out.print("Enter new Date Entered (MM/dd/yyyy): ");
        bike[0] = scanner.nextLine();
        System.out.print("Enter new Stock Label (Old/New): ");
        bike[1] = scanner.nextLine();
        System.out.print("Enter new Brand: ");
        bike[2] = scanner.nextLine();
        System.out.print("Enter new Engine Number: ");
        String newEngineNumber = scanner.nextLine();
        engineMap.remove(engineNumber);
        bike[3] = newEngineNumber;
        engineMap.put(newEngineNumber, bike);
        System.out.print("Enter new Status (On-hand/Sold): ");
        bike[4] = scanner.nextLine();
        saveInventory();
        System.out.println("Motorcycle details updated successfully!");
    }

    private static void saveInventory() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (String[] bike : inventory) {
                pw.println(String.join(",", bike));
            }
        } catch (IOException e) {
            System.out.println("Error saving inventory.");
        }
    }

    private static void saveAndExit() {
        saveInventory();
        System.out.println("Exiting..");
        System.out.println("Saved");
    }
}
