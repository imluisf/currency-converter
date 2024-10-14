import javax.net.ssl.HttpsURLConnection; // Import for HTTPS connection
import java.net.URL; // Import for URL handling
import java.util.HashMap; // Import for using HashMap
import java.util.Map; // Import for using Map
import java.util.Scanner; // Import for user input
import com.google.gson.Gson; // Import for JSON parsing
import com.google.gson.JsonObject; // Import for JSON object handling

public class CurrencyConverter {
    public static void main(String[] args) {
        double amountToConvert; // Variable to store the amount to convert

        // Initialize exchange rates map
        Map<String, Double> exchangeRates = new HashMap<>();
        Scanner scanner = new Scanner(System.in); // Create a scanner object for user input

        System.out.println("**************************************************");
        System.out.println("Welcome to the Currency Converter System!");
        System.out.println("**************************************************");

        // Fetch and display exchange rates
        try {
            // URL for fetching exchange rates
            URL url = new URL("https://v6.exchangerate-api.com/v6/0f153fe7f37e2d5b42bcb37f/latest/USD");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); // Set request method to GET
            connection.connect(); // Establish connection

            int responseCode = connection.getResponseCode(); // Get the response code
            if (responseCode != 200) {
                throw new RuntimeException("Error: " + responseCode); // Throw error if response is not OK
            } else {
                StringBuilder informationString = new StringBuilder(); // StringBuilder to hold the response
                Scanner urlScanner = new Scanner(url.openStream()); // Scanner to read the response

                // Read the response line by line
                while (urlScanner.hasNext()) {
                    informationString.append(urlScanner.nextLine());
                }
                urlScanner.close(); // Close the scanner

                // Use Gson to parse the JSON response
                Gson gson = new Gson();
                JsonObject jsonResponse = gson.fromJson(informationString.toString(), JsonObject.class);

                // Get specific exchange rates
                String[] currencies = {"USD", "CLP", "MXN", "VES", "TRY", "EUR", "COP"}; // Added COP for Colombian Peso
                for (String currency : currencies) {
                    // Check if the currency exists in the response
                    if (jsonResponse.getAsJsonObject("conversion_rates").has(currency)) {
                        // Store the exchange rate in the map
                        exchangeRates.put(currency, jsonResponse.getAsJsonObject("conversion_rates").get(currency).getAsDouble());
                    }
                }

                // Display exchange rates
                System.out.println("Exchange rates today:");
                exchangeRates.forEach((currency, rate) ->
                        System.out.println("1 USD to " + currency + ": " + rate));
                System.out.println("**************************************************");
            }
        } catch (Exception e) {
            System.out.println("Error fetching exchange rates: " + e.getMessage()); // Display error message
            return; // Exit if there is an error
        }

        // Ask for the user's name
        System.out.println("Please enter your name:");
        String userName = scanner.nextLine(); // Read user's name
        System.out.println("Nice to meet you, " + userName + "!");

        // Conversion menu
        while (true) {
            System.out.println("**************************************************");
            System.out.println("Available currencies for conversion:");
            exchangeRates.keySet().forEach(currency -> System.out.println(currency)); // Display available currencies

            System.out.println("Enter the currency code you want to convert from (or type 'exit' to quit):");
            String fromCurrency = scanner.next().toUpperCase(); // Read the currency to convert from

            if (fromCurrency.equals("EXIT")) {
                break; // Exit the loop if the user types 'exit'
            }

            // Validate the currency selection
            if (!exchangeRates.containsKey(fromCurrency)) {
                System.out.println("Invalid currency selection. Please try again.");
                continue; // Continue to the next iteration if invalid
            }

            System.out.println("Enter the currency code you want to convert to:");
            String toCurrency = scanner.next().toUpperCase(); // Read the currency to convert to

            // Validate the currency selection
            if (!exchangeRates.containsKey(toCurrency)) {
                System.out.println("Invalid currency selection. Please try again.");
                continue; // Continue to the next iteration if invalid
            }

            System.out.println("What amount of " + fromCurrency + " do you want to convert?");
            amountToConvert = scanner.nextDouble(); // Read the amount to convert

            // Convert from the source currency to USD
            double amountInUSD = amountToConvert / exchangeRates.get(fromCurrency);
            // Convert from USD to the destination currency
            double convertedAmount = amountInUSD * exchangeRates.get(toCurrency);

            // Display the converted amount
            System.out.printf("Your value converted is: %.2f %s%n", convertedAmount, toCurrency);
        }

        scanner.close(); // Close the scanner
        System.out.println("Thank you for using the Currency Converter. Goodbye!"); // Exit message
    }
}
