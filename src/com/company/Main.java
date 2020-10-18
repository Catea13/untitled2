package com.company;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
public class Main {
    static JComboBox comboBox = new JComboBox();
    public static <E> void main(String[] args) {
        // Create frame
        final JFrame frame = new JFrame();
        frame.setVisible(true);
        frame.setSize(500, 500);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        // Graph G9 are 7 puncte = 7 persoane
        Person P1 = new Person("P1", 100, 100);
        Person P2 = new Person("P2", 300, 80);
        Person P3 = new Person("P3", 190, 150);
        Person P4 = new Person("P4", 125, 180);
        Person P5 = new Person("P5", 80, 225);
        Person P6 = new Person("P6", 190, 245);
        Person P7 = new Person("P7", 280, 225);
        // Make connections between people
        P1.addFriends(P5, P4, P3, P2);
        P2.addFriends(P1, P3, P7);
        P3.addFriends(P1, P2, P4, P6);
        P4.addFriends(P1, P6);
        P5.addFriends(P1, P6);
        P6.addFriends(P5, P4, P3, P7);
        P7.addFriends(P2, P6);
        final ArrayList<Person> personList = new ArrayList<Person>(Arrays.asList(P1, P2, P3, P4, P5, P6, P7));
        // Print list of friends
        for (Person person : personList) {
            person.printFriendList();
        }
        // Draw points/graph
        final GUIpanel guiPanel = new GUIpanel(personList);
        frame.add(guiPanel);
        // Create combobox for selection
        comboBox.addItem("-1");
        for (int i = 0; i < personList.size(); i++) {
            comboBox.addItem(i);
        }
        guiPanel.add(comboBox);
        comboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                guiPanel.repaint();
            }
        });
        // Add button to add/remove additional graph
        JButton button = new JButton("Complementar");
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(guiPanel.additionalGraph) {
                    guiPanel.additionalGraph = false;
                } else {
                    guiPanel.additionalGraph = true;
                }
            }
        });
        guiPanel.add(button);
    }
}
/**
 * This panel draws points
 *
 */
class GUIpanel extends JPanel {
    ArrayList<Person> personList;
    boolean additionalGraph = true;
    /**
     * Constructor
     *
     * @param Ggraph
     */
    public GUIpanel(ArrayList<Person> personList) {
        super();
        this.personList = personList;
    }
    /**
     * This part draws dots on this JPanel for user to see
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw specific friend group
        int valueSelected = Integer.valueOf(Main.comboBox.getSelectedItem().toString());
        if (valueSelected != -1) {
            drawGroupConnection(g, personList.get(valueSelected).friendList);
        } else {
            // Draw each friend group on the graph
            for (Person person : personList) {
                drawGroupConnection(g, person.friendList);
            }
        }
        this.updateUI();
    }
    public void drawGroupConnection(Graphics g, ArrayList<String> friendList) {
        g.setColor(Color.black);
        if(!additionalGraph) {
            // Reverse friend list - creates another graph opposite to the original
            friendList = getReversedFriendList(personList, friendList);
        }
        // Draw all connections
        for (int z = 0; z < friendList.size(); z++) {
            int x = getCoordinatesByName(friendList.get(0))[0];
            int y = getCoordinatesByName(friendList.get(0))[1];
            for (int i = 0; i < friendList.size(); i++) {
                int x2 = getCoordinatesByName(friendList.get(i))[0];
                int y2 = getCoordinatesByName(friendList.get(i))[1];
                g.drawLine(x, y, x2, y2);
            }
        }
        // Draw each dot and display name and coordinate
        g.setColor(Color.blue);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 15));
        for (int i = 0; i < friendList.size(); i++) {
            int x = getCoordinatesByName(friendList.get(i))[0];
            int y = getCoordinatesByName(friendList.get(i))[1];
            g.drawOval(x, y, 6, 6);
            g.drawString("(" + x + "," + y + ") " + friendList.get(i), x, y);
        }
    }
    public ArrayList<String> getReversedFriendList(ArrayList<Person> personList, ArrayList<String> friendList) {
        ArrayList<String> listOfAllPeopleNames = new ArrayList<String>();
        ArrayList<String> reversedFriendList = new ArrayList<String>();
        for(Person person : personList) {
            listOfAllPeopleNames.add(person.name);
        }
        // Make new list
        for (String name : listOfAllPeopleNames) {
            if (!friendList.contains(name)) {
                reversedFriendList.add(name);
            }
        }
        return reversedFriendList;
    }
    public int[] getCoordinatesByName(String name) {
        for (Person person : personList) {
            if (person.name.equals(name)) {
                int[] xyPoints = { person.x, person.y };
                return xyPoints;
            }
        }
        // Return null if a name match was not found
        return null;
    }
}
/**
 * This class holds info about a person: name, friendList, X & Y coordinates
 */
class Person {
    String name;
    int x, y;
    ArrayList<String> friendList = new ArrayList<String>();
    public Person(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        // Add self in the list of friends
        friendList.add(this.name);
    }
    public void printFriendList() {
        System.out.println(friendList);
    }
    public void addFriends(Person... newFriends) {
        for (Person friend : newFriends) {
            addFriend(friend);
        }
    }
    private void addFriend(Person newFriend) {
        // Don't add new friend if they are already friends
        if (friendList.contains(newFriend.name)) {
            return;
        }
        // Add each friend in the friend list
        friendList.add(newFriend.name);
        newFriend.addFriend(this);
        // Sort list alphabetically
        // Collections.sort(friendList);
    }
}




