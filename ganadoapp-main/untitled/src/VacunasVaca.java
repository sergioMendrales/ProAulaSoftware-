import java.util.LinkedHashSet;

public class VacunasVaca {
    public static void main(String[] args) {

        LinkedHashSet<String> vacunas = new LinkedHashSet<>();


        vacunas.add("Fiebre Aftosa");
        vacunas.add("Brucelosis");
        vacunas.add("Carbunco");


        boolean agregada = vacunas.add("Brucelosis");


        System.out.println("Vacunas aplicadas a la vaca:");
        for (String vacuna : vacunas) {
            System.out.println("- " + vacuna);
        }


        System.out.println("\n¿Se agregó la vacuna duplicada?: " + agregada);
        System.out.println("Reflexión: El LinkedHashSet no permite duplicados. "
                + "Si intentas agregar una vacuna repetida, simplemente no la añade, "
                + "y mantiene el orden en el que fueron insertadas las demás.");
    }
}
