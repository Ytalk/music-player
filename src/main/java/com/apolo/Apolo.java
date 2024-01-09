package com.apolo;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Color;

public class Apolo extends JFrame{

    Play music = new Play("mp3/Purple.mp3");
    Thread musicThread;

    public Apolo(){

        JButton play_button = new JButton("tocar musica");
        play_button.setBounds(485, 300, 100, 30);//posição e tamanho
        add(play_button);

        play_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (musicThread == null || !musicThread.isAlive()) {
                    musicThread = new Thread(music);
                    musicThread.start();
                }
            }

        });


        JButton pause_button = new JButton("parar musica");
        pause_button.setBounds(400, 200, 100, 30);//posição e tamanho
        add(pause_button);

        pause_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                music.pausePlayback();
            }

        });


        JButton resume_button = new JButton("prosseguir musica");
        resume_button.setBounds(285, 100, 100, 30);//posição e tamanho
        add(resume_button);

        resume_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                music.resumePlayback();
            }

        });


        //DETALHES DO FRAME
        setLayout(null);
        setTitle("Harmonic Apolo");
        setPreferredSize(new Dimension(854, 480));
        getContentPane().setBackground(new Color( 129, 13, 175));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();//empacota/organiza
        setLocationRelativeTo(null);//centraliza
        setVisible(true);
    }


    public static void main(String[] args) {
        new Apolo();
    }


}
