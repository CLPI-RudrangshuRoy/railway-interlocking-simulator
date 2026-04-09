
import java.util.Scanner;

public class CrossoverStatus {

    static class Crossover {
        String name;
        char position = 'N'; // N=Normal, R=Reverse
        char setting = 'F'; // F=Free, S=Set

        Crossover(String name) {
            this.name = name;
        }

        void togglePosition() {
            position = (position == 'N') ? 'R' : 'N';
            System.out.println(name + " Position: " + position);
        }

        void toggleSetting() {
            setting = (setting == 'F') ? 'S' : 'F';
            System.out.println(name + " Setting: " + setting);
        }

        void display() {
            System.out.println(name + " -> Position: " + position + ", Setting: " + setting);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Crossover[] crossovers = {
            new Crossover("SC_HSF"), new Crossover("SC_SSF"), new Crossover("SC_LL")
        };

        while (true) {
            System.out.println("\nCrossover Menu:");
            System.out.println("1. Toggle Position  2. Toggle Setting  3. Display  4. Exit");
            int choice = sc.nextInt();
            if (choice == 4) break;

            System.out.println("Select Crossover (0-2):");
            for (int i = 0; i < crossovers.length; i++) System.out.println(i + " -> " + crossovers[i].name);
            int crossIndex = sc.nextInt();
            Crossover co = crossovers[crossIndex];

            switch (choice) {
                case 1 -> co.togglePosition();
                case 2 -> co.toggleSetting();
                case 3 -> co.display();
                default -> System.out.println("Invalid choice!");
            }
        }
        sc.close();
    }
}
