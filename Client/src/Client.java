import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class Client extends JFrame {

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 88;

    private PrintWriter output;
    private BufferedReader input;

    private JTextArea area;
    private JTextField field;

    public Client() {
        // Задаем положение и размеры окна
        setBounds(0, 0, 400, 420);
        // Задаем заголовок окна
        setTitle("Chat");
        // Делаем окно видимым
        setVisible(true);
        // Запрещаем изменять размер окна
        setResizable(false);
        // Будем завершать приложение при нажатии кнопки "Закрыть"
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Создаем объект панели
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JButton btnConn = new JButton("Connect");
        btnConn.setBounds(10, 10, 180, 50);
        btnConn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                connect();
            }
        });
        panel.add(btnConn);

        JButton btnDisc = new JButton("Disconnect");
        btnDisc.setBounds(210, 10, 180, 50);
        btnDisc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                disconnect();
            }
        });
        panel.add(btnDisc);

        area = new JTextArea();
        area.setBounds(10, 70, 380, 260);
        area.setBackground(Color.RED);
        area.setEditable(false);
        panel.add(area);

        field = new JTextField();
        field.setBounds(10, 340, 340, 50);
        panel.add(field);

        JButton btnSend = new JButton(">");
        btnSend.setBounds(360, 340, 30, 50);
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                sendMessage(field.getText());
            }
        });
        panel.add(btnSend);

        // Прикрепляем панель к окну
        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Client();
            }
        });
    }

    private void connect() {
        new Thread(new Connector()).start();
    }

    private void sendMessage(String text) {
        if (output != null && !text.isEmpty()) new Thread(new Sender(text)).start();
    }

    private void disconnect() {
        new Thread(new Sender("disconnect")).start();
    }

    private class Connector implements Runnable {
        @Override
        public void run() {
            Socket socket;
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                output = new PrintWriter(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new Thread(new Receiver()).start();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Receiver implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    final String message = input.readLine();
                    if (message != null) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                area.append(message + "\n");
                            }
                        });
                    } else {
                        output = null;
                        input = null;
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Sender implements Runnable {

        private String message;

        Sender(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            output.println(message);
            output.flush();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    field.setText("");
                }
            });
        }
    }

}
