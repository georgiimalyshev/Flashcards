package flashcards;


import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

import static java.lang.System.out;

public class CardBox {

    private HashMap<String, String> hashMapDef;
    private LinkedHashMap<String, String> cards;
    private HashMap<String, Integer> mistakes;
    private ArrayList<String> logList = new ArrayList<>();
    private Scanner scanner;
    private String pathToExport;
    private String pathToImport;

    public CardBox(String[] args) {
        hashMapDef = new HashMap<>();
        cards = new LinkedHashMap<>();
        scanner = new Scanner(System.in);
        mistakes = new HashMap<>();
        ArrayList<String> argsList = new ArrayList<String>(Arrays.asList(args));
        int importIndex = argsList.indexOf("-import");
        int exportIndex = argsList.indexOf("-export");
        if(importIndex != -1) {
            pathToImport = argsList.get(importIndex + 1);
        }
        if (exportIndex != -1) {
            pathToExport = argsList.get(exportIndex + 1);
        }


        importAction(true);
        process();
    }

    private void process() {
        while (true) {
            addStringToLogAndPrint("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            String input = getStringFromInputAndAddToLog();
            switch (input) {

                case "add": {
                    addAction();
                    break;
                }
                case "remove": {
                    removeAction();
                    break;
                }
                case "ask": {
                    askAction();
                    break;
                }
                case "import": {
                    importAction(false);
                    break;
                }
                case "export": {
                    exportAction(false);
                    break;
                }
                case "log": {
                    logAction();
                    break;
                }
                case "hardest card": {
                    hardestCardAction();
                    break;
                }
                case "reset stats": {
                    resetStatsAction();
                    break;
                }
                default: {
                    addStringToLogAndPrint("Bye bye!");
                    exportAction(true);
                    return;
                }
            }
        }
    }

    private void addAction() {
        addStringToLogAndPrint("The card: ");
        String term;
        String definition;

        term = getStringFromInputAndAddToLog();
        if (cards.containsKey(term)) {
            addStringToLogAndPrint("The card \"" + term + "\" already exists.");
            return;
        }

        addStringToLogAndPrint("The definition of the card: ");

        definition = getStringFromInputAndAddToLog();
        if (hashMapDef.containsKey(definition)) {
            addStringToLogAndPrint("The definition \"" + definition + "\" already exists.");
            return;
        }

        addPair(term, definition, 0);
        addStringToLogAndPrint(String.format("The pair (\"%s\":\"%s\") has been added", term, definition));
    }

    private void removeAction() {
        addStringToLogAndPrint("The card: ");
        String term;
        String definition;
        term = getStringFromInputAndAddToLog();
        if (!cards.containsKey(term)) {
            addStringToLogAndPrint(String.format("Can't remove \"%s\": there is no such card.", term));
            return;
        }
        definition = cards.get(term);

        cards.remove(term);
        hashMapDef.remove(definition);
        mistakes.remove(term);
        addStringToLogAndPrint("The card has been removed.");
    }

    private void askAction() {
        addStringToLogAndPrint("How many times to ask?");
        int n = Integer.parseInt(getStringFromInputAndAddToLog());

        Random random = new Random();
        Object[] array = cards.keySet().toArray();

        while (n-- > 0) {
            String term = (String) array[random.nextInt(cards.size())];
            String correctDefinition = cards.get(term);
            addStringToLogAndPrint(String.format("Print the definition of \"%s\":", term));
            String definition = getStringFromInputAndAddToLog();
            if (!hashMapDef.containsKey(definition)) {
                addStringToLogAndPrint(String.format("Wrong answer. The correct one is \"%s\".", correctDefinition));
                mistakes.put(term, mistakes.getOrDefault(term, 0) + 1);
                continue;
            }
            String termFromInput = hashMapDef.get(definition);
            if (termFromInput.equals(term)) {
                addStringToLogAndPrint("Correct answer!");
            } else {
                addStringToLogAndPrint(String.format("Wrong answer. The correct one is \"%s\", you've just written the definition of \"%s\"", correctDefinition, termFromInput));
                mistakes.put(term, mistakes.getOrDefault(term, 0) + 1);
            }
        }
    }

    private void importAction(boolean fromStart) {

        String fileName;
        if (fromStart) {
            if(pathToImport == null) {
                return;
            }
            fileName = pathToImport;
        } else {
            addStringToLogAndPrint("File name:");
            fileName = getStringFromInputAndAddToLog();
        }
        File file = new File(fileName);
        if (!file.exists()) {
            addStringToLogAndPrint("File not found.");
            return;
        }
        try (Scanner fileScanner = new Scanner(file)) {
            int loaded = 0;
            while (fileScanner.hasNextLine()) {
                String curLine = fileScanner.nextLine();
                String[] pair = curLine.split(";");
                addPair(pair[0], pair[1], Integer.parseInt(pair[2]));
                loaded++;
            }
            addStringToLogAndPrint(String.format("%d cards have been loaded", loaded));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        pathToImport = null;
    }

    private void exportAction(boolean fromExit) {
        String fileName;
        if (fromExit) {
            if (pathToExport == null) {
                return;
            }
            fileName = pathToExport;
        } else {
            addStringToLogAndPrint("File name:");
            fileName = getStringFromInputAndAddToLog();
        }
        try {
            FileWriter writer = new FileWriter(fileName);
            for (Map.Entry<String, String> elem : cards.entrySet()) {
                writer.write(elem.getKey() + ";" + elem.getValue() + ";" + mistakes.getOrDefault(elem.getKey(), 0) + "\n");
            }
            writer.close();
            addStringToLogAndPrint(String.format("%d cards have been saved.", cards.size()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logAction() {
        addStringToLogAndPrint("File name:");
        String fileName = getStringFromInputAndAddToLog();
        try {
            FileWriter writer = new FileWriter(fileName);
            for (String str :
                    logList) {
                writer.write(str + "\n");
            }

            writer.close();
            addStringToLogAndPrint("The log has been saved.");


        } catch (IOException e) {
            e.printStackTrace();
        }

        
    }

    private void hardestCardAction() {
        if (mistakes.isEmpty()) {
            addStringToLogAndPrint("There are no cards with errors.");
            return;
        }
        String isAre;
        String themIt;
        ArrayList<String> hardCards = new ArrayList<>();
        String strHardCards = "";
        int maxMistakes = mistakes.values().stream().max(Integer::compare).get();
        for (String str : mistakes.keySet()) {
            if (mistakes.get(str) == maxMistakes) {
                hardCards.add(str);
            }
        }
        strHardCards = hardCards.toString()
                .replaceAll("\\[", "\"")
                .replaceAll("\\]", "\"")
                .replaceAll(", ", "\", \"");
        if (hardCards.size() > 1) {
            isAre = "cards are";
            themIt = "them";
        } else {
            isAre = "card is";
            themIt = "it";
        }


        addStringToLogAndPrint(String.format("The hardest %s %s. You have %d errors answering %s.", isAre, strHardCards, maxMistakes, themIt));
    }

    private void resetStatsAction() {
        mistakes.clear();
        addStringToLogAndPrint("Card statistics has been reset.");
    }

    private void addPair(String term, String definition, int mistakesNum) {
        if (hashMapDef.containsKey(cards.get(term))) {
            hashMapDef.remove(cards.get(term));
        }

        cards.put(term, definition);
        hashMapDef.put(definition, term);
        if (mistakesNum != 0) {
            mistakes.put(term, mistakesNum);
        }
    }

    private void addStringToLogAndPrint(String str) {
        out.println(str);
        logList.add(str);
    }

    private String getStringFromInputAndAddToLog() {
        String str = scanner.nextLine();
        logList.add(str);
        return str;
    }

}
