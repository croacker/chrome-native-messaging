package com.croc.documentum.print;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.Map;

/**
 *
 */
public class SwingTestApplet implements IMethod{

    private String data;

    @Override
    public void init(String data) {
        this.data = data;
    }

    @Override
    public String getResult() {
        showFrame();
        return "show";
    }

    private void showFrame(){
        JFrame frame = new JFrame("Native messaging JFrame");
        frame.setSize(300, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {

        panel.setLayout(null);

        JLabel userLabel = new JLabel("Пользователь");
        userLabel.setBounds(10, 10, 80, 25);
        panel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(100, 10, 160, 25);
        panel.add(userText);

        JLabel passwordLabel = new JLabel("Пароль");
        passwordLabel.setBounds(10, 40, 80, 25);
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 40, 160, 25);
        panel.add(passwordText);

        JButton loginButton = new JButton("Войти");
        loginButton.setBounds(10, 80, 80, 25);
        panel.add(loginButton);

        JButton registerButton = new JButton("Зарегистрироваться");
        registerButton.setBounds(180, 80, 80, 25);
        panel.add(registerButton);
    }
}