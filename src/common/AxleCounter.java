import java.util.Scanner;

public class AxleCounter {

    static class Section {
        String name;
        int inCount = 0;
        int outCount = 0;

        Section(String name) {
            this.name = name;
        }

        void in() {
            inCount++;
            System.out.println(name + " IN count: " + inCount);
        }

        void out() {
            outCount++;
            System.out.println(name + " OUT count: " + outCount);
        }

        void reset() {
            inCount = 0;
            outCount = 0;
            System.out.println(name + " counters reset.");
        }

        void display() {
            System.out.println(name + " -> IN: " + inCount + ", OUT: " + outCount);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Section[] sections = {
            new Section("ML_HSF"), new Section("LL_HSF"), new Section("ML_SSF"), 
            new Section("ML_Ad_S"), new Section("SC_HSF"), new Section("SC_SSF"), 
            new Section("SC_LL")
        };

        while (true) {
            System.out.println("\nAxle Counter Menu:");
            System.out.println("1. IN  2. OUT  3. RESET  4. Display  5. Exit");
            int choice = sc.nextInt();
            if (choice == 5) break;

            System.out.println("Select Section (0-6):");
            for (int i = 0; i < sections.length; i++) System.out.println(i + " -> " + sections[i].name);
            int secIndex = sc.nextInt();
            Section sec = sections[secIndex];

            switch (choice) {
                case 1 -> sec.in();
                case 2 -> sec.out();
                case 3 -> sec.reset();
                case 4 -> sec.display();
                default -> System.out.println("Invalid choice!");
            }
        }
        sc.close();
    }
}
