package compila;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.codegen.types.NumericType;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Windows
 */
public class Analisador {

    private Map<Integer, ArrayList<Token>> tokens;
    private Map<String, String> lexemas;
    private String path;
    private Stack<String> pilha;
    private boolean funcao;

    Analisador(String nome) {

        tokens = new LinkedHashMap<Integer, ArrayList<Token>>();
        lexemas = new HashMap<String, String>();
        pilha = new Stack<>();
        this.path = nome;
        preencheLexemas();

    }

    private void preencheLexemas() {
//Aritmeticos
        lexemas.put("+", "+");
        lexemas.put("-", "-");
        lexemas.put("*", "*");
        lexemas.put(" x ", "*");
        lexemas.put("/", "/");
        lexemas.put(":", "/");
        lexemas.put(".", ".");
        lexemas.put(",", ".");
        lexemas.put("[", "[");
        lexemas.put("]", "]");
        lexemas.put("(", "(");
        lexemas.put(")", ")");

//Comparativos
        lexemas.put(">", "gt");
        lexemas.put(">=", "gte");
        lexemas.put("<", "lt");
        lexemas.put("=<", "lte");
        lexemas.put("==", "eq");
        lexemas.put("!=", "neq");
//Gerais
        lexemas.put("=", "=");
        lexemas.put("int", "int");
        lexemas.put("float", "float");
        lexemas.put("string", "string");
        lexemas.put("var", "id");
        lexemas.put("fun", "fun");
        lexemas.put("vet", "vet");
//Palavras-chave
//Condicionais
        lexemas.put("se", "cond");
        lexemas.put("então", "initcond");
        lexemas.put("senão", "altcond");
        lexemas.put("fim-se", "endcond");
        lexemas.put("e", "&&");
        lexemas.put("ou", "||");
//Loops
        lexemas.put("para", "forloop");
        lexemas.put("de", "rng1forloop");
        lexemas.put("até", "rng2forloop");
        lexemas.put("faça", "initforloop");
        lexemas.put("fim-para", "endforloop");
        lexemas.put("enquanto", "whileloop");
        lexemas.put("fim-enquanto", "endwhileloop");
    }

    public BufferedReader carrega(String path) throws IOException {
        BufferedReader reader;
        reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(path), "Cp1252"));
        return reader;
    }

    public void analisar() throws IOException {
        BufferedReader br = carrega(path);
        int cont = 0;
        String linha = "";
        boolean coment = false;
        ArrayList<Token> listaTokens;
        while (br.ready()) {
            listaTokens = new ArrayList<Token>();
            cont++;
            linha = br.readLine();
            Token token = null;
            String tok = "";
            String[] pal = linha.split(" ");//separa a linha em palavras
            for (int j = 0; j < pal.length; j++) {//percorre o numero de palavras da linha
                if (lexemas.containsKey(pal[j])) { //se alguma palavra tiver nos lexemas lá de cima
                    token = new Token(lexemas.get(pal[j]), pal[j]); //é um token
                    listaTokens.add(token);
                } else { //se nao, trata caracter por caracter
                    for (int i = 0; i < linha.length(); i++) {
                        String p = linha;
                        if (p.charAt(i) == '#') {
                            coment = !coment;
                        }
                        if (!coment) {
                            if (p.charAt(i) == '"') {
                                tok = "";
                                
                                i++;
                                while (p.charAt(i) != '"') {
                                    tok += p.charAt(i);
                                    i++;
                                }
                                token = new Token(lexemas.get("string"), tok);
                                listaTokens.add(token);
                                tok = "";
                            }
                        }
                    }
                }
            }
            tokens.put(cont, listaTokens);
            /*for (int i = 0; i < linha.length(); i++) {
             if (s.charAt(i) == '#') {
             comentario = !comentario;
             }
             if (!comentario) {
             if (s.charAt(i) == '"') {
             t = "";
             i++;
             while (s.charAt(i) != '"') {
             t += s.charAt(i);
             i++;
             }
             token = new Token(lexemas.get("string"), t);
             listaTokens.add(token);
             t = "";
             } else if (Character.isLetter(s.charAt(i))) {
             //t = "";
             t += s.charAt(i);
             //System.out.println("aiaiai "+ t);
             if (lexemas.containsKey(t)) {// && !ValidaLetra(s.charAt(i + 1))) {
             //t = "" + t + "";              
             if (s.length() > (i + 1) && (s.charAt(i + 1) == ' ' || s.charAt(i + 1) == '.' || s.charAt(i + 1) == ',')) {
             token = new Token(lexemas.get(t), t);
             listaTokens.add(token);
             t = "";
             }

             }
             //t = "";
             } else if (s.charAt(i) == '>' || s.charAt(i) == '<' || s.charAt(i) == '=') {
             t = "";
             t += s.charAt(i);
             if (s.length() > (i + 1) && (s.charAt(i + 1) == '>' || s.charAt(i + 1) == '<' || s.charAt(i + 1) == '=')) {
             String proximo = t + s.charAt(i + 1);
             if (lexemas.containsKey(proximo)) {
             t = proximo;
             }
             i++;
             }
             listaTokens.add(new Token(lexemas.get(t), t));
             t = "";
             } else if ((i + 1) < s.length() && (s.charAt(i) + "" + s.charAt(i + 1)).equals("!=")) {
             t = s.charAt(i) + "" + s.charAt(i + 1);
             listaTokens.add(new Token(lexemas.get(t), t));
             i++;
             } else if (Character.isDigit(s.charAt(i))) {
             //t = "";
             if (i > 0 && Character.isLetter(s.charAt(i - 1)) && s.charAt(i - 1) != 'x') {
             t += s.charAt(i);
             if (lexemas.containsKey(t)) {// && !ValidaLetra(s.charAt(i + 1))) {
             //t = "" + t + "";              
             if (s.length() > (i + 1) && (s.charAt(i + 1) == ' ' || s.charAt(i + 1) == '.' || s.charAt(i + 1) == ',')) {
             token = new Token(lexemas.get(t), t);
             listaTokens.add(token);
             t = "";
             }

             }
             /*while (Character.isDigit(s.charAt(i))) {
             t += s.charAt(i);
             i++;
             }
             i--;
             //System.err.println(t);
             token = new Token(lexemas.get("int"), t);
             t = "";
             listaTokens.add(token);
             } else {
             boolean inteiro = true;
             do {
             t = t + s.charAt(i);
             i++;
             if (i < s.length() && (s.charAt(i) == '.' || s.charAt(i) == ',') && (i + 1) < s.length() && Character.isDigit(s.charAt(i + 1)) && inteiro) {
             t = t + s.charAt(i);
             i++;
             inteiro = false;
             }
             if (i < s.length() && !Character.isDigit(s.charAt(i))) {
             i--;
             break;
             }
             } while (i < s.length() && Character.isDigit(s.charAt(i)));
             if (inteiro) {
             token = new Token(lexemas.get("int"), t);
             } else {
             token = new Token(lexemas.get("float"), t);
             }
             listaTokens.add(token);
             t = "";
             }
             } else if (s.charAt(i) == 'x' && i > 0 && (i + 1) < s.length() && ((s.charAt(i - 1) == ' ' && s.charAt(i + 1) == ' ') || (Character.isDigit((s.charAt(i - 1))) && Character.isDigit(s.charAt(i + 1))) || (s.charAt(i + 1) == '(' && (s.charAt(i - 1) == ' ' || Character.isDigit(s.charAt(i - 1)))))) {
             t = " ";
             t += s.charAt(i) + " ";
             System.err.println('"' + t + '"');
             listaTokens.add(new Token(lexemas.get(t), t));
             } else if (s.length() == 1 && s.charAt(i) == 'x') {
             t = " ";
             t += s.charAt(i) + " ";
             System.err.println('"' + t + '"');
             listaTokens.add(new Token(lexemas.get(t), t));
             // } else if (s.charAt(i) == '+' || s.charAt(i) == '*') {
             // t = "";
             // t += s.charAt(i);
             // listaTokens.add(new Token(lexemas.get(t), t));
             } else if (s.charAt(i) == '-' && (i + 1) < s.length() && t.equals("fim")) {
             t = t + s.charAt(i);
             } else if (s.charAt(i) == '.') {
             t = "";
             listaTokens.add(new Token(lexemas.get("."), "."));
             } else if (s.charAt(i) == 'e' && i > 0 && (i + 1) < s.length() && !listaTokens.isEmpty() && listaTokens.get(listaTokens.size() - 1).getTipo() == ")") {
             //i++;
             while (s.charAt(i) == ' ' && i < s.length()) {
             i++;
             }
             // i--;
             // if (s.charAt(i + 1) == '(') {
             if (s.charAt(i) == '(') {
             // t = "";
             listaTokens.add(new Token(lexemas.get("e"), "e"));
             // } else if (!Character.isLetter(s.charAt(i + 1)) && !Character.isDigit(s.charAt(i + 1))) {
             } else if (!Character.isLetter(s.charAt(i)) && !Character.isDigit(s.charAt(i))) {
             t += 'e';
             listaTokens.add(new Token(lexemas.get("var"), t));
             t = "";
             } else {
             // t = "";
             t += 'e';
             if (s.charAt(i) == ' ') {
             listaTokens.add(new Token(lexemas.get("var"), t));
             t = "";
             }
             }
             } else if (s.charAt(i) == '(' && !listaTokens.isEmpty() && listaTokens.get(listaTokens.size() - 1).getTipo() == lexemas.get("var")) {
             t = "";
             listaTokens.get(listaTokens.size() - 1).setTipo(lexemas.get("fun"));
             listaTokens.add(new Token(lexemas.get("("), "("));
             pilha.push("((");
             funcao = true;
             } else if (s.charAt(i) == ')' && pilha.size() >= 2 && pilha.get(pilha.size() - 2) == "((") {
             t = "";
             pilha.pop();
             listaTokens.add(new Token(lexemas.get(")"), ")"));
             funcao = true;
             } else if (s.charAt(i) == ')' && pilha.size() == 1) {
             t = "";
             pilha.pop();
             listaTokens.add(new Token(lexemas.get(")"), ")"));
             funcao = false;
             } else if (s.charAt(i) != 'e' && s.charAt(i) != 'x' && lexemas.containsKey(t)) {//possivelmente errado
             if (s.charAt(i) == '(') {
             t = "";
             pilha.push("(");
             listaTokens.add(new Token(lexemas.get("("), "("));
             funcao = false;
             } else if (s.charAt(i) == ')') {
             t = "";
             if (!pilha.isEmpty()) {
             pilha.pop();
             }
             listaTokens.add(new Token(lexemas.get(")"), ")"));
             } else if (s.charAt(i) == ',') {
             if (!funcao) {
             listaTokens.add(new Token(lexemas.get(t), ","));
             t = "";
             }
             }
             // else {
             // listaTokens.add(new Token(lexemas.get(t), t));
             // t = "";
             // }
             } else if ((Character.isLetter(s.charAt(i)) || Character.isDigit(s.charAt(i)))) {
             t = t + s.charAt(i);
             if (lexemas.containsKey(token) && ((s.length() > (i + 1) && (!Character.isLetter(s.charAt(i + 1)))) || (s.length() == (i + 1)))) {
             listaTokens.add(new Token(lexemas.get(t), t));
             t = "";
             }
             if (!t.equals(" ") && !t.equals("") && (i + 1) < s.length() && !Character.isLetter(s.charAt(i + 1)) && !Character.isDigit(s.charAt(i + 1)) && !t.equals("fim")) {
             listaTokens.add(new Token(lexemas.get("var"), t));
             t = "";
             }
             if (!token.equals(" ") && !token.equals("") && (i + 1) == s.length()) {
             listaTokens.add(new Token(lexemas.get("var"), t));
             t = "";
             }
             } else if (comentario == false && !Character.isLetter(s.charAt(i)) && !Character.isDigit(s.charAt(i)) && s.charAt(i) != ' ') {
             if (count != 1 && i != 0) {
             listaTokens.add(new Token(t, t));
             t = "";
             }
             }
             }
             }
             tokens.put(count, listaTokens);
             }
             br.close();
             }
             catch (IOException ex

    
             ) {
             Logger.getLogger(AnalisadorLexico.class.getName()).log(Level.SEVERE, "Arquivo nao encontrado", ex);
             }
             for (Map.Entry<Integer, ArrayList<Token>> entrySet

             : tokens.entrySet () 
             ) {
             Integer key = entrySet.getKey();
             ArrayList<Token> value = entrySet.getValue();
             System.out.print(key + " - ");
             for (Token value1 : value) {
             System.out.print(value1.toString() + " ");
             }
             System.out.println("");
             }*/
        }
        br.close();
        for (Map.Entry<Integer, ArrayList<Token>> entrySet
                : tokens.entrySet()) {
            Integer key = entrySet.getKey();
            ArrayList<Token> value = entrySet.getValue();
            System.out.print(key + " - ");
            for (Token value1 : value) {
                System.out.print(value1.toString() + " ");
            }
            System.out.println("");
        }
    }
}
