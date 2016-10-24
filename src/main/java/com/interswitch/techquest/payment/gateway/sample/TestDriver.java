package com.interswitch.techquest.payment.gateway.sample;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interswitch.techquest.secure.utils.TransactionSecurity;

public class TestDriver {

    public static final String HTTP_CODE = "HTTP_CODE";
    public static final String RESPONSE_BODY = "RESPONSE_BODY";

    public static void main(String... args) throws Exception {
        try {

            System.out.println("To leave a field empty, please press enter");
            Scanner scanner = new Scanner(System.in);
            String quitFlag = "";

            while (quitFlag != null && !quitFlag.equalsIgnoreCase("q")) {
                System.out.println("");
                System.out.println("===================================");
                System.out.println("1.Validations 2.Purchases 3.Status");
                String menuItem = "1";
                scanner = new Scanner(System.in);
                menuItem = scanner.nextLine();

                System.out.println("");
                System.out.println("===================================");
                System.out.println("Enter your PAN: ");
                String pan = scanner.nextLine();

                System.out.println("Enter PAN Expiry Date (Format YYMM e.g. 5004 for Apr, 2050): ");
                String expiryDate = scanner.nextLine();

                System.out.println("Enter CVV. Press enter to ignore: ");
                String cvv = scanner.nextLine();

                System.out.println("Enter PIN. Press enter to ignore: ");
                String pin = scanner.nextLine();

                String certFilePath = "C:\\Users\\abiola.adebanjo\\Documents\\isw-api-jam\\paymentgateway.crt";
                String authData = TransactionSecurity.generateAuthData("1", pan, pin, expiryDate, cvv, certFilePath);

                if ("1".equals(menuItem)) {

                    HashMap<String, String> validateResponse = PaymentGateway.doValidation(authData);

                    int httpResponseCode = Integer.parseInt(validateResponse.get(HTTP_CODE));
                    switch (httpResponseCode) {
                        case 200:
                            //
                            break;
                        case 202:
                            //
                            ObjectMapper mapper = new ObjectMapper();
                            Map<String, Object> responseBody = new HashMap<String, Object>();
                            responseBody = mapper.readValue(validateResponse.get(RESPONSE_BODY), new TypeReference<Map<String, String>>() {
                            });
                            if (responseBody != null && responseBody.containsKey("responseCode")) {
                                String responseCode = responseBody.get("responseCode").toString();
                                if (responseCode.equalsIgnoreCase("T0")) {
                                    System.out.println("Enter your OTP e.g. 958274");
                                    String otp = scanner.nextLine();

                                    String transactionRef = responseBody.get("transactionRef").toString();
                                    PaymentGateway.doValidationAuthOTP(otp, transactionRef);
                                }
                            }
                            break;
                        default:
                            break;
                    }

                    System.out.println();
                    System.out.println("===================================");
                    System.out.println("Press any key to contiue, Q to quit");
                    quitFlag = scanner.nextLine();
                } else if ("2".equals(menuItem)) {
                    System.out.println("Enter amount in major denomination e.g. 100.00");
                    String amount = scanner.nextLine();

                    HashMap<String, String> purchaseResponse = PaymentGateway.doPurchase(authData, amount);
                    int httpResponseCode = Integer.parseInt(purchaseResponse.get(HTTP_CODE));
                    switch (httpResponseCode) {
                        case 200:
                            //
                            break;
                        case 202:
                            //
                            ObjectMapper mapper = new ObjectMapper();
                            Map<String, Object> responseBody = new HashMap<String, Object>();
                            responseBody = mapper.readValue(purchaseResponse.get(RESPONSE_BODY), new TypeReference<Map<String, String>>() {
                            });
                            if (responseBody != null && responseBody.containsKey("responseCode")) {
                                String responseCode = responseBody.get("responseCode").toString();
                                if (responseCode.equalsIgnoreCase("T0")) {
                                    System.out.println("Enter your OTP e.g. 958274");
                                    String otp = scanner.nextLine();

                                    String paymentId = responseBody.get("paymentId").toString();
                                    PaymentGateway.doPurchaseAuthOTP(otp, paymentId);
                                }
                            }
                            break;
                        default:
                            break;
                    }
                } else if ("3".equals(menuItem)) {
                    System.out.println("Amount: ");
                    String amount = scanner.nextLine();
                    System.out.println("TransactionRef : ");
                    String transactionRef = scanner.nextLine();

                    PaymentGateway.doTransactionQuery(amount, transactionRef);
                }
            }
            scanner.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            Thread.sleep(50000);
        }
    }

}
