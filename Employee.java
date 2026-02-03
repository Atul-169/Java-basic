public class Employee {
    int id;
    String name;
    int daysPresent;

    public Employee(int id, String name, int daysPresent) {
        this.id = id;
        this.name = name;
        this.daysPresent = daysPresent;
    }

    public void increaseDays() {
        daysPresent++;
    }

    public boolean decreaseDays() {
        if (daysPresent > 0) {
            daysPresent--;
            return true;
        }
        return false;
    }

    public void display() {
        System.out.println("ID: " + id + 
                           ", Name: " + name + 
                           ", Days Present: " + daysPresent);
    }
}
public class Department {
    int deptId;
    Employee[] employees = new Employee[50];
    int empCount = 0;

    public Department(int deptId) {
        this.deptId = deptId;
    }

    public boolean addEmployee(Employee e) {
        if (empCount == 50)
            return false;

        employees[empCount++] = e;
        return true;
    }

    public Employee findEmployee(int empId) {
        for (int i = 0; i < empCount; i++) {
            if (employees[i].id == empId)
                return employees[i];
        }
        return null;
    }

    public void markAttendance(int empId) {
        Employee e = findEmployee(empId);
        if (e != null) {
            e.increaseDays();
            System.out.println("Attendance marked.");
        } else {
            System.out.println("Employee not found.");
        }
    }

    public void correctAttendance(int empId) {
        Employee e = findEmployee(empId);
        if (e != null) {
            if (e.decreaseDays())
                System.out.println("Attendance corrected.");
            else
                System.out.println("Days already zero.");
        } else {
            System.out.println("Employee not found.");
        }
    }

    public void display() {
        System.out.println("Department ID: " + deptId);
        if (empCount == 0) {
            System.out.println("No employees registered.");
        } else {
            for (int i = 0; i < empCount; i++) {
                employees[i].display();
            }
        }
        System.out.println("--------------------");
    }
}
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Main <number_of_departments>");
            return;
        }

        int n = Integer.parseInt(args[0]);
        if (n > 10) {
            System.out.println("Maximum 10 departments allowed.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        Department[] departments = new Department[n];

        // Initial input
        for (int i = 0; i < n; i++) {
            departments[i] = new Department(i + 1);

            System.out.print("How many employees in Department " + (i + 1) + "? ");
            int m = sc.nextInt();
            sc.nextLine();

            if (m > 50) {
                System.out.println("Maximum 50 employees allowed.");
                m = 50;
            }

            for (int j = 0; j < m; j++) {
                System.out.print("Employee ID: ");
                int id = sc.nextInt();
                sc.nextLine();

                System.out.print("Name: ");
                String name = sc.nextLine();

                System.out.print("Days Present: ");
                int days = sc.nextInt();
                sc.nextLine();

                departments[i].addEmployee(
                    new Employee(id, name, days)
                );
            }
        }

        // Menu loop
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Mark Attendance");
            System.out.println("2. Correct Attendance");
            System.out.println("3. Display All Department Info");
            System.out.println("4. Exit");

            int choice = sc.nextInt();

            if (choice == 1) {
                System.out.print("Department ID: ");
                int d = sc.nextInt();
                System.out.print("Employee ID: ");
                int e = sc.nextInt();

                departments[d - 1].markAttendance(e);
            }

            else if (choice == 2) {
                System.out.print("Department ID: ");
                int d = sc.nextInt();
                System.out.print("Employee ID: ");
                int e = sc.nextInt();

                departments[d - 1].correctAttendance(e);
            }

            else if (choice == 3) {
                for (int i = 0; i < n; i++) {
                    departments[i].display();
                }
            }

            else if (choice == 4) {
                System.out.println("Program terminated.");
                break;
            }

            else {
                System.out.println("Invalid option.");
            }
        }
    }
}
