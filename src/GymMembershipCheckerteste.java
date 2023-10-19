import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

public class GymMembershipCheckerteste {
    private static final long MILLIS_PER_YEAR = 31536000000L;

    public static void main(String[] args) {

        // Läs in personnummer eller namn från användaren
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ange personnummer eller namn: ");
        String userInput = scanner.nextLine();

        // Läs in filen med kunduppgifter
        String customerInfoFilePath = ".idea/kunduppgifter.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(customerInfoFilePath))) {
            String line;
            String customerInfo = null;
            String paymentDateString = null;
            while ((line = br.readLine()) != null) {
                if (customerInfo == null) {
                    customerInfo = line;  // Läs in personnummer eller namn
                } else if (paymentDateString == null) {
                    paymentDateString = line;  // Läs in betalningsdatum
                }

                if (customerInfo != null && paymentDateString != null) {
                    Date paymentDate = parseDate(paymentDateString);
                    if (paymentDate != null) {
                        if (userInput.equalsIgnoreCase(customerInfo.split(",")[1].trim())) {
                            if (isPaymentWithinLastYear(paymentDate)) {
                                System.out.println("Kunden är en nuvarande medlem.");

                                // Spara träningsinformation för personlig tränare
                                saveTrainingInfo(userInput, paymentDate);
                            } else {
                                System.out.println("Kunden är en före detta kund.");
                            }
                            return;
                        }
                    } else {
                        System.out.println("Ogiltigt datumformat i filen.");
                        return;
                    }

                    customerInfo = null;
                    paymentDateString = null;
                }
            }
            System.out.println("Personen finns inte i filen och är obehörig.");
        } catch (IOException e) {
            System.err.println("Kunde inte läsa filen: " + e.getMessage());
        }
    }

    private static Date parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    private static boolean isPaymentWithinLastYear(Date paymentDate) {
        long currentTime = System.currentTimeMillis();
        long paymentTime = paymentDate.getTime();
        return (currentTime - paymentTime) <= MILLIS_PER_YEAR;
    }

    private static void saveTrainingInfo(String name, Date date) {
        String trainingInfoFilePath = "P.T.info.txt";
        System.out.println("Saving training info to: " + trainingInfoFilePath); // Print the file path

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(trainingInfoFilePath, true))) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = dateFormat.format(date);
            String trainingInfo = name + "," + formattedDate;

            bw.write(trainingInfo);
            bw.newLine();
            System.out.println("Training information saved for the personal trainer.");
        } catch (IOException e) {
            System.err.println("Could not save training information: " + e.getMessage());
        }
    }

    @Test
    void parseDate_validDate_shouldParseSuccessfully() {

        String dateString = "2023-10-17";
        Date expectedDate = new Date();
        try {
            expectedDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date parsedDate = GymMembershipCheckerteste.parseDate(dateString);

        assertEquals(expectedDate, parsedDate);
    }

    @Test
    void isPaymentWithinLastYear_paymentWithinLastYear_shouldReturnTrue() {

        long oneYearInMillis = 31536000000L;
        Date paymentDate = new Date(System.currentTimeMillis() - oneYearInMillis + 1000);

        boolean result = GymMembershipCheckerteste.isPaymentWithinLastYear(paymentDate);


        assertTrue(result);
    }

    @Test
    void isPaymentWithinLastYear_paymentOverLastYear_shouldReturnFalse() {

        long oneYearInMillis = 31536000000L;
        Date paymentDate = new Date(System.currentTimeMillis() - oneYearInMillis - 1000);


        boolean result = GymMembershipCheckerteste.isPaymentWithinLastYear(paymentDate);

        assertFalse(result);
    }
}


