
import java.util.Scanner;

public class SectionStatus {

    static class Section {
        String name;
        char setting = 'F'; // F=Not Set, S=Set
        boolean occupied = false;

        Section(String name) {
            this.name = name;
        }

        void toggleSetting() {
            setting = (setting == 'F') ? 'S' : 'F';
            System.out.println(name + " Setting: " + setting);
        }

        void occupy() {
            occupied = true;
            System.out.println(name + " is now OCCUPIED");
        }

        void clear() {
            occupied = false;
            System.out.println(name + " is now FREE");
        }

        void display() {
            System.out.println(name + " -> Setting: " + setting + ", Occupied: " + occupied);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Section[] sections = {
            new Section("ML_HSF"), new Section("LL_HSF"), new Section("ML_SSF"), new Section("ML_Ad_S")
        };

        while (true) {
            System.out.println("\nSection Menu:");
            System.out.println("1. Toggle Setting  2. Occupy  3. Clear  4. Display  5. Exit");
            int choice = sc.nextInt();
            if (choice == 5) break;

            System.out.println("Select Section (0-3):");
            for (int i = 0; i < sections.length; i++) System.out.println(i + " -> " + sections[i].name);
            int secIndex = sc.nextInt();
            Section sec = sections[secIndex];

            switch (choice) {
                case 1 -> sec.toggleSetting();
                case 2 -> sec.occupy();
                case 3 -> sec.clear();
                case 4 -> sec.display();
                default -> System.out.println("Invalid choice!");
            }
        }
        sc.close();
    }
}
