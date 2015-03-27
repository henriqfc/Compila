package compila;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Compiladores {

    public static void main(String[] args) throws IOException {
        Scanner ler = new Scanner(System.in);

        System.out.println("Informe o nome/caminho de arquivo texto:");
        String caminho = ler.nextLine();
        //try {
            System.out.println("Conteúdo do arquivo texto:");
            Analisador an = new Analisador(caminho);
            an.analisar();
//        } catch (IOException ex) {
//            Logger.getLogger(Analisador.class.getName()).log(Level.SEVERE, null, ex);
//        }

    }
}
