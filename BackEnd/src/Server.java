import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;

public class Server extends JFrame implements Runnable {
    private ServerSocket serverSocket;
    private JTextArea chatArea;
    private PrintWriter out;
    private Map<String, Contact> contacts = new HashMap<>();
    private List<BlockList> blockedContacts = new ArrayList<>();

    public Server(int port) {

        try {
            serverSocket = new ServerSocket(port);
            setupGUI();
            preAddContacts();
            new Thread(this).start(); // Start server thread
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupGUI() {
        chatArea = new JTextArea(20, 30);
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        JTextField inputField = new JTextField(20);
        JButton sendButton = new JButton("Send");

        //Adding Function that if users press ENTER key then message will be sent
        inputField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendButton.doClick();
                }
            }
        });

        sendButton.addActionListener(e -> {
            String message = inputField.getText();
            if (!message.isEmpty()) {
                sendMessage(message);
                inputField.setText("");
            }
        });

        JPanel panel = new JPanel();
        panel.add(inputField);
        panel.add(sendButton);

        JButton manageContactsButton = new JButton("Manage Contacts");
        manageContactsButton.addActionListener(e -> openContactManagement());

//        JButton viewBlockedButton = new JButton("View Blocked Contacts");
//        viewBlockedButton.addActionListener(e -> viewBlockedContacts());

        //Adding button to Show creators of applicatio
        JButton creatorsInfoButton = new JButton("Creators Info");
        creatorsInfoButton.addActionListener(e -> viewCreators() );


        JPanel topPanel = new JPanel();
        topPanel.add(manageContactsButton);
//        topPanel.add(viewBlockedButton);
        topPanel.add(creatorsInfoButton);

        add(topPanel, "North");
        add(scrollPane, "Center");
        add(panel, "South");

        setTitle("ChatHub");
        setSize(500, 500);

        //Setting app name and logo
        setTitle("ChatHub");
        ImageIcon logo = new ImageIcon("logo.png");
        setIconImage(logo.getImage());


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void preAddContacts() {
        contacts.put("1", new Contact("1", "Imad Wasim", "03039812367"));
        contacts.put("2", new Contact("2", "Mohammad Amir", "0317080150"));
    }

    private void openContactManagement() {
        String[] options = {"Add Contact", "Remove Contact", "Block Contact", "View All contacts" , "View Blocked contacts"};
        int choice = JOptionPane.showOptionDialog(this, "Choose an option:", "Manage Contacts",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0 -> addContact();
            case 1 -> removeContact();
            case 2 -> blockContact();
            case 3 -> viewAllContacts();
            case 4 -> viewBlockedContacts();
        }
    }

    private void addContact() {
        String name = JOptionPane.showInputDialog(this, "Enter Contact Name:");
        String number = JOptionPane.showInputDialog(this, "Enter Contact Number:");
        String id = String.valueOf(contacts.size() + 1);
        Contact contact = new Contact(id, name, number);
        contacts.put(id, contact);
        JOptionPane.showMessageDialog(this, "Contact added successfully!");
    }


    private void removeContact() {
        String id = JOptionPane.showInputDialog(this, "Enter Contact ID to remove:");
        if (contacts.remove(id) != null) {
            JOptionPane.showMessageDialog(this, "Contact removed successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Contact not found.");
        }
    }

    private void blockContact() {
        String id = JOptionPane.showInputDialog(this, "Enter Contact ID to block:");
        Contact contact = contacts.get(id);
        if (contact != null) {
            blockedContacts.add(new BlockList(contact.getId(), contact.getName(), contact.getPhoneNumber()));
            JOptionPane.showMessageDialog(this, "Contact blocked successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Contact not found.");
        }
    }


    //This is will execute when user hits viewBlock contacts
    private void viewBlockedContacts() {
        if (blockedContacts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No contacts are currently blocked.");
            return;
        }
        StringBuilder blockedList = new StringBuilder("Blocked Contacts:\n");
        for (BlockList blocked : blockedContacts) {
            blockedList.append(blocked).append("\n");
        }
        JOptionPane.showMessageDialog(this, blockedList.toString());
    }

    public void viewCreators(){
        StringBuilder creators = new StringBuilder("Creators:\n");
        creators.append("\tSami-ur-Rehman(https://github.com/SamiUrRehman2395)\nMuhammad Eman(https://github.com/Eman-07)\n");

        JOptionPane.showMessageDialog(null, creators.toString());
    }

    //creating method to view all contacts
    public void viewAllContacts() {
        StringBuilder contactList = new StringBuilder("Contacts:\n");
            for (Contact contact : contacts.values()) {
                contactList.append(contact).append("\n");
            }
            JOptionPane.showMessageDialog(this, contactList.toString());


    }

    public void run() {
        try {
            chatArea.append("Waiting for client connection...\n");
            Socket clientSocket = serverSocket.accept();
            chatArea.append("Client connected.\n");

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                chatArea.append("Client: " + inputLine + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void sendMessage(String message) {
        if (out != null) {
            out.println(message);
            chatArea.append("Server: " + message + "\n");
        }
//
//        //Adding method that if user press ENTER key message will be sent
//        chatArea.addKeyListener(new KeyAdapter() {
//            public void keyPressed(KeyEvent e) {
//                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//                    sendMessage(message);
//                }
//            }
//        });



    }

    public static void main(String[] args) {
        new Server(12345);
    }
}