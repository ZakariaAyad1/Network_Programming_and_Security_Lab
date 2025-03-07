import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    public static void main(String argv[]) throws Exception {
        String phraseRecu;
        ServerSocket conn = new ServerSocket(7010);
        System.out.println("[Serveur] En attente de connexion...");

        while(true) {
            Socket comm = conn.accept();
            if(comm != null)
                System.out.println("[Serveur] Connexion entrante du client acceptee");
            else
                return;

            BufferedReader entreeDepuisClient =
                    new BufferedReader(new InputStreamReader(comm.getInputStream()));
            DataOutputStream sortieVersClient =
                    new DataOutputStream(comm.getOutputStream());

            int quit = 1;
            while(quit != 0) {
                phraseRecu = entreeDepuisClient.readLine();
                String[] valeurs = phraseRecu.split(";");

                int operation = Integer.parseInt(valeurs[0]);
                if(operation == 0) {
                    quit = 0;
                    break;
                }

                // Handle mixed operations (option 5)
                if(operation == 5) {
                    String expression = valeurs[1].trim();
                    try {
                        double resultat = evaluateExpression(expression);
                        sortieVersClient.writeBytes("Le resultat de l'expression " + expression + " est egale = " + resultat + "\n");
                    } catch (Exception e) {
                        sortieVersClient.writeBytes("[Erreur] " + e.getMessage() + "\n");
                    }
                    continue;
                }

                // Handle regular operations (1-4)
                String[] operandeStrings = valeurs[1].split(" ");
                ArrayList<Double> operandes = new ArrayList<>();
                for (String operande : operandeStrings) {
                    operandes.add(Double.parseDouble(operande));
                }

                double resultat = operandes.get(0);
                String expression = "Le resultat de l'operation " + operandes.get(0) + "";

                for (int i = 1; i < operandes.size(); i++) {
                    double op = operandes.get(i);
                    switch (operation) {
                        case 1:
                            resultat += op;
                            expression += " + " + op;
                            break;
                        case 2:
                            resultat -= op;
                            expression += " - " + op;
                            break;
                        case 3:
                            resultat *= op;
                            expression += " x " + op;
                            break;
                        case 4:
                            if(op == 0) {
                                sortieVersClient.writeBytes("[Erreur] Division par zero \n");
                                break;
                            }
                            resultat /= op;
                            expression += " / " + op;
                            break;
                        default:
                            System.out.println("L'utilisateur a entre un choix incorrect");
                            sortieVersClient.writeBytes("[Erreur] Choix incorrect, choisir 1..5 \n");
                    }
                }

                sortieVersClient.writeBytes(expression + " est egale = " + resultat + "\n");
            }
        }
    }

    // Method to evaluate mixed operations expressions with operator precedence
    private static double evaluateExpression(String expression) throws Exception {
        // Remove all spaces
        expression = expression.replaceAll("\\s+", "");

        // Check for valid characters
        if (!expression.matches("[0-9+\\-*/().]+")) {
            throw new Exception("Expression contient des caractères invalides");
        }

        return evaluateExpressionRecursive(expression);
    }

    private static double evaluateExpressionRecursive(String expression) throws Exception {
        // Stack for numbers
        Stack<Double> values = new Stack<>();

        // Stack for operators
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            // If current character is a digit, extract the complete number
            if (Character.isDigit(c) || c == '.') {
                StringBuilder numBuilder = new StringBuilder();

                // Get the complete number
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    numBuilder.append(expression.charAt(i++));
                }
                i--; // Move back one position to compensate for the loop increment

                values.push(Double.parseDouble(numBuilder.toString()));
            }
            // If opening bracket, push to operator stack
            else if (c == '(') {
                operators.push(c);
            }
            // If closing bracket, solve the inner expression
            else if (c == ')') {
                while (!operators.empty() && operators.peek() != '(') {
                    values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
                }

                if (!operators.empty() && operators.peek() == '(') {
                    operators.pop(); // Remove the '('
                } else {
                    throw new Exception("Parentheses non équilibrées");
                }
            }
            // If an operator
            else if (c == '+' || c == '-' || c == '*' || c == '/') {
                // Handle unary minus
                if (c == '-' && (i == 0 || expression.charAt(i-1) == '(' || expression.charAt(i-1) == '+' ||
                        expression.charAt(i-1) == '-' || expression.charAt(i-1) == '*' || expression.charAt(i-1) == '/')) {
                    values.push(0.0);
                }

                // While top of 'operators' has higher or same precedence to current
                // operator, apply operator on top of 'operators' to top two elements in values stack
                while (!operators.empty() && hasPrecedence(c, operators.peek())) {
                    values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
                }

                // Push current operator to 'operators'
                operators.push(c);
            }
        }

        // Apply remaining operators to remaining values
        while (!operators.empty()) {
            if (operators.peek() == '(' || operators.peek() == ')') {
                throw new Exception("Parentheses non équilibrées");
            }
            values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
        }

        // Top of 'values' is the result
        if (values.size() != 1) {
            throw new Exception("Expression malformée");
        }

        return values.pop();
    }

    // Returns true if 'op2' has higher or same precedence as 'op1'
    private static boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }

        return (op1 != '*' && op1 != '/') || (op2 != '+' && op2 != '-');
    }

    // Apply an operation ('+', '-', '*', '/')
    private static double applyOperation(char op, double b, double a) throws Exception {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new Exception("Division par zéro");
                }
                return a / b;
        }
        return 0;
    }
}