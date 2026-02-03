public class Student {
    int id;
    String name;

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
public class Course {
    int courseId;
    String courseName;
    int capacity;
    Student[] students;
    int currentCount;

    public Course(int courseId, String courseName, int capacity) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.capacity = capacity;
        this.students = new Student[capacity];
        this.currentCount = 0;
    }

    public boolean isFull() {
        return currentCount == capacity;
    }

    public boolean isStudentEnrolled(int studentId) {
        for (int i = 0; i < currentCount; i++) {
            if (students[i].id == studentId)
                return true;
        }
        return false;
    }

    public boolean enrollStudent(Student s) {
        if (isFull() || isStudentEnrolled(s.id))
            return false;

        students[currentCount++] = s;
        return true;
    }

    public boolean dropStudent(int studentId) {
        for (int i = 0; i < currentCount; i++) {
            if (students[i].id == studentId) {
                // shift left
                for (int j = i; j < currentCount - 1; j++) {
                    students[j] = students[j + 1];
                }
                currentCount--;
                return true;
            }
        }
        return false;
    }

    public void display() {
        System.out.println("Course ID: " + courseId);
        System.out.println("Course Name: " + courseName);
        System.out.println("Capacity: " + capacity);
        System.out.println("Current Enrollment: " + currentCount);

        if (currentCount == 0) {
            System.out.println("No students enrolled.");
        } else {
            for (int i = 0; i < currentCount; i++) {
                System.out.println("  " + students[i].id + " - " + students[i].name);
            }
        }
        System.out.println("-------------------------");
    }
}
import java.util.Scanner;

public class Main {
    static Course[] courses;
    static Student[] students = new Student[100];
    static int studentCount = 0;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Main <number_of_courses>");
            return;
        }

        int n = Integer.parseInt(args[0]);
        if (n > 30) {
            System.out.println("Maximum 30 courses allowed.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        courses = new Course[n];

        // Input courses
        for (int i = 0; i < n; i++) {
            System.out.print("Enter Course Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Seat Capacity: ");
            int cap = sc.nextInt();
            sc.nextLine();
            courses[i] = new Course(i + 1, name, cap);
        }

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Enroll a New Student in a Course");
            System.out.println("2. Enroll Existing Student in a Course");
            System.out.println("3. Drop Student from a Course");
            System.out.println("4. Display All Courses with Enrolled Students");
            System.out.println("5. Exit");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    enrollNewStudent(sc);
                    break;
                case 2:
                    enrollExistingStudent(sc);
                    break;
                case 3:
                    dropStudent(sc);
                    break;
                case 4:
                    displayAll();
                    break;
                case 5:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    static Student findStudent(int id) {
        for (int i = 0; i < studentCount; i++) {
            if (students[i].id == id)
                return students[i];
        }
        return null;
    }

    static Course findCourse(int id) {
        for (Course c : courses) {
            if (c.courseId == id)
                return c;
        }
        return null;
    }

    // Option 1
    static void enrollNewStudent(Scanner sc) {
        System.out.print("Student ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        if (findStudent(id) != null) {
            System.out.println("Student already exists.");
            return;
        }

        System.out.print("Student Name: ");
        String name = sc.nextLine();

        System.out.print("Course ID: ");
        int cid = sc.nextInt();

        Course c = findCourse(cid);
        if (c == null) {
            System.out.println("Invalid course.");
            return;
        }

        if (c.isFull()) {
            System.out.println("Course is full.");
            return;
        }

        Student s = new Student(id, name);
        students[studentCount++] = s;
        c.enrollStudent(s);
        System.out.println("Student enrolled successfully.");
    }

    // Option 2
    static void enrollExistingStudent(Scanner sc) {
        System.out.print("Student ID: ");
        int id = sc.nextInt();
        System.out.print("Course ID: ");
        int cid = sc.nextInt();

        Student s = findStudent(id);
        Course c = findCourse(cid);

        if (s == null || c == null) {
            System.out.println("Invalid student or course.");
            return;
        }

        if (c.enrollStudent(s))
            System.out.println("Enrollment successful.");
        else
            System.out.println("Cannot enroll (full or already enrolled).");
    }

    // Option 3
    static void dropStudent(Scanner sc) {
        System.out.print("Student ID: ");
        int id = sc.nextInt();
        System.out.print("Course ID: ");
        int cid = sc.nextInt();

        Course c = findCourse(cid);
        if (c == null) {
            System.out.println("Invalid course.");
            return;
        }

        if (c.dropStudent(id))
            System.out.println("Student dropped successfully.");
        else
            System.out.println("Student not enrolled in this course.");
    }

    // Option 4
    static void displayAll() {
        for (Course c : courses) {
            c.display();
        }
    }
}
