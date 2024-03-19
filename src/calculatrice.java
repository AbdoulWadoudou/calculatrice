import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

class Calculatrice {
    static double a = 0;
    static int op = 0;
    static StringBuilder history = new StringBuilder();
    static boolean powerMode = false; // Indicateur pour la puissance

    public static void main(String ar[]) {
        JFrame f1 = new JFrame("Calculator");
        f1.setLayout(new BorderLayout());
        f1.setResizable(false);

        // Barre de menu
        JMenuBar menuBar = new JMenuBar();
        JMenu infoMenu = new JMenu("Mes Informations ");
        // Identifiants de l'étudiant dans le Menu
        JMenuItem nameItem = new JMenuItem("Nom et prénom: OUEDRAOGO Abdoul Wadoudou");
        infoMenu.add(nameItem);
        menuBar.add(infoMenu);
        f1.setJMenuBar(menuBar);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel pour les chiffres et le bouton pour vider le textfield
        JPanel numPanel = new JPanel();
        numPanel.setBounds(10, 10, 200, 30);
        numPanel.setBackground(Color.gray);
        numPanel.setLayout(new GridLayout(4, 4, 5, 5));
        numPanel.add(new JButton("7"));
        numPanel.add(new JButton("8"));
        numPanel.add(new JButton("9"));
        numPanel.add(new JButton("4"));
        numPanel.add(new JButton("5"));
        numPanel.add(new JButton("6"));
        numPanel.add(new JButton("1"));
        numPanel.add(new JButton("2"));
        numPanel.add(new JButton("3"));
        numPanel.add(new JButton("0"));
        numPanel.add(new JButton("."));
        numPanel.add(new JButton("C")); // boutton pour vider le textfield
        mainPanel.add(numPanel, BorderLayout.WEST);

        // Panel pour les opérateurs
        JPanel operatorPanel = new JPanel();
        operatorPanel.setBounds(10, 10, 200, 30);
        operatorPanel.setBackground(Color.gray);
        operatorPanel.setLayout(new GridLayout(4, 4, 5, 5));
        operatorPanel.add(new JButton("+"));
        operatorPanel.add(new JButton("-"));
        operatorPanel.add(new JButton("x"));
        operatorPanel.add(new JButton("%"));
        operatorPanel.add(new JButton("√")); // operateur pour racien carré d'un nombre
        operatorPanel.add(new JButton("^")); // operateur pour la puissance d'un nombre
        operatorPanel.add(new JButton("="));
        mainPanel.add(operatorPanel, BorderLayout.EAST);

        // Champ de texte pour la saisie de l'opération et le resultat
        final JTextField t1 = new JTextField("");
        mainPanel.add(t1, BorderLayout.NORTH);

        // Panel pour les boutons d'historique
        JPanel controlPanel = new JPanel();
        JButton historyButton = new JButton("Afficher l'historique"); // boutton pour afficher l'historique des opérations
        JButton clearHistoryButton = new JButton("Effacer l'historique"); // boutton pour supprimer l'historique des opérations
        controlPanel.add(historyButton);
        controlPanel.add(clearHistoryButton);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        f1.add(mainPanel);

        class MyListener1 implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                JButton button = (JButton) e.getSource();
                String cmd = button.getText();

                if (cmd.matches("[0-9.]")) { //  Vérification de la saisie de l'utilisateur si c'est un chiffre ou un point
                    t1.setText(t1.getText() + cmd);
                } else if (cmd.equals("C")) {
                    t1.setText("");
                    powerMode = false;
                } else if (cmd.equals("=")) {
                    if (powerMode) {
                        String[] parts = t1.getText().split("\\^");
                        if (parts.length == 2) {
                            double base = Double.parseDouble(parts[0].trim());
                            double exponent = Double.parseDouble(parts[1].trim());
                            double result = Math.pow(base, exponent);
                            t1.setText("" + result);

                            // Ajouter l'opération à l'historique des opérations dans le fichier
                            history.append(base).append("^").append(exponent).append("=").append(result).append("\n");
                            saveHistoryToFile(history.toString());
                        }
                        powerMode = false;
                    } else {
                        if (!t1.getText().isEmpty() && t1.getText().contains(" ")) {
                            String[] parts = t1.getText().split(" ");
                            double b = Double.parseDouble(parts[2]); // conversion de donnée en double
                            double c = 0;
                            // Vérifier la division par zéro
                            if (op == 4 && b == 0) {
                                JOptionPane.showMessageDialog(f1, "Impossible de diviser un nombre par 0", "Erreur", JOptionPane.ERROR_MESSAGE);
                                t1.setText("");
                                return; // Sortir de la méthode pour éviter la division par zéro
                            }
                            switch (op) {
                                case 1:
                                    c = a + b;
                                    break;
                                case 2:
                                    c = a - b;
                                    break;
                                case 3:
                                    c = a * b;
                                    break;
                                case 4:
                                    c = a / b;
                                    break;
                                default:
                                    c = 0;
                            }
                            t1.setText("" + c);

                            // Ajouter l'opération à l'historique des opérations dans le fichier
                            history.append(a).append(getOperatorSymbol(op)).append(b).append("=").append(c).append("\n");
                            saveHistoryToFile(history.toString());
                        }
                    }
                } else if (cmd.equals("√")) { // Nouvelle condition pour la racine carrée d'un nombre
                    if (!t1.getText().isEmpty()) {
                        double operand = Double.parseDouble(t1.getText());
                        double result = Math.sqrt(operand);
                        t1.setText("" + result);

                        // Ajouter l'opération à l'historique des opérations dans le fichier
                        history.append("√").append(operand).append("=").append(result).append("\n");
                        saveHistoryToFile(history.toString());
                    }
                } else if (cmd.equals("^")) { // Nouvelle condition pour calculer la puissance d'un nombre
                    if (!t1.getText().isEmpty()) {
                        a = Double.parseDouble(t1.getText());
                        op = 5; // Code pour la puissance
                        t1.setText(t1.getText() + " ^ ");
                        powerMode = true;
                    }
                } else { // Si la saisie de l'utilisateur est un opérateur
                    if (!t1.getText().isEmpty()) { // Vérifier si le champ n'est pas vide
                        a = Double.parseDouble(t1.getText());
                        op = getOperatorCode(cmd);
                        t1.setText(t1.getText() + " " + cmd + " ");
                    }
                }
            }
        }

        class HistoryListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(f1, history.toString(), "Historique des opérations",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }

        // Fonction de suppression n de l'historique des opérations
        class ClearHistoryListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                history.setLength(0);
                saveHistoryToFile(history.toString());
            }
        }

        MyListener1 ml = new MyListener1();
        HistoryListener historyListener = new HistoryListener();
        ClearHistoryListener clearHistoryListener = new ClearHistoryListener();

        historyButton.addActionListener(historyListener);
        clearHistoryButton.addActionListener(clearHistoryListener);

        // Ajout des écouteurs aux boutons numériques et opérateurs
        for (Component comp : numPanel.getComponents()) {
            if (comp instanceof JButton) {
                ((JButton) comp).addActionListener(ml);
            }
        }

        for (Component comp : operatorPanel.getComponents()) {
            if (comp instanceof JButton) {
                ((JButton) comp).addActionListener(ml);
            }
        }

        f1.setSize(400, 300);
        f1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f1.setVisible(true);
    }

    private static String getOperatorSymbol(int op) {
        switch (op) {
            case 1:
                return "+";
            case 2:
                return "-";
            case 3:
                return "x";
            case 4:
                return "%";
            case 5:
                return "^";
            default:
                return "";
        }
    }

    private static int getOperatorCode(String opSymbol) {
        switch (opSymbol) {
            case "+":
                return 1;
            case "-":
                return 2;
            case "x":
                return 3;
            case "%":
                return 4;
            default:
                return 0;
        }
    }


    // Fonction pour enregistrer l'opération dans le fichier des historiques
    private static void saveHistoryToFile(String history) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("historique.txt"))) {
            writer.write(history);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
