package com.apolo;

import javax.swing.JOptionPane;

public class musicException extends RuntimeException{

    String message, error_name;

    public musicException(String message, String error_name){
        this.message = message;
        this.error_name = error_name;
    }


    public void showMessage(){
        JOptionPane.showMessageDialog(null, message, error_name, JOptionPane.ERROR_MESSAGE);
    }

}
