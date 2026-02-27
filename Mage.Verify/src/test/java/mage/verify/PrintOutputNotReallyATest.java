package mage.verify;

import mage.cards.Card;
import mage.cards.repository.CardScanner;
import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrintOutputNotReallyATest {
    @Test
    public void print() {
        try {
            File f = new File("output.txt");
            PrintWriter writer = new PrintWriter(f);
            List<Card> allCards = CardScanner.getAllCards();
            Set<String> seenNames = new HashSet<>();
            for (Card card : allCards) {
                String name = card.getName();
                if (seenNames.add(name)) {
                    writer.println(card.getName());
                }
            }
            writer.close();
            System.out.println("Data written to "+f.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

}
