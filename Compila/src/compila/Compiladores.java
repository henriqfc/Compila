package compila;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

    

public class Compiladores {
    public static void main(String[] args) throws IOException {
    Scanner ler = new Scanner(System.in);

    System.out.println("Informe o nome de arquivo texto:");
    String nome = ler.nextLine();

    System.out.println("Conteúdo do arquivo texto:");
    Analisador an= new Analisador(nome);
    an.analisar();
    
  }
}