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

    private Map<Integer, ArrayList<Token>> linhaToken;
    private Map<String, String> expres;
    private String caminhoArquivo;
    

    Analisador(String caminho) {

        linhaToken = new LinkedHashMap<Integer, ArrayList<Token>>();
        expres = new HashMap<String, String>();
        this.caminhoArquivo = caminho;
        expressoes();

    }

    private void expressoes() {
//Operações
        expres.put("+", "+");
        expres.put("-", "-");
        expres.put("*", "*");
        expres.put("x", "*");
        expres.put("/", "/");
        expres.put(":", "/");
//tokens ou separadores?????        
        expres.put("[", "[");
        expres.put("]", "]");
        expres.put("(", "(");
        expres.put(")", ")");
//Comparações
        expres.put(">", "maior");
        expres.put(">=", "maiorIgual");
        expres.put("=>", "maiorIgual");
        expres.put("<", "menor");
        expres.put("=<", "menorIgual");
        expres.put("<=", "menorIgual");
        expres.put("==", "igual");
        expres.put("!=", "diferente");
        expres.put("e", "&&");
        expres.put("ou", "||");
//tipos
        expres.put("=", "=");
        expres.put("int", "int");
        expres.put("float", "float");
        expres.put("string", "string");
        expres.put("var", "ident");
        expres.put("fun", "funcao");
        expres.put("vetor", "vetor");
//Se
        expres.put("se", "if");
        expres.put("então", "inicIf");
        //expres.put("entao", "inicIf");
        expres.put("senão", "else");
        expres.put("fim-se", "fechaIf");
//Loops
        expres.put("para", "for");
        expres.put("de", "for1");
        expres.put("até", "for2");
        expres.put("faça", "inicFor");
        expres.put("fim-para", "fechaFor");
        expres.put("enquanto", "while");
        expres.put("fim-enquanto", "fechaWhile");
    }

    public BufferedReader carregaArq(String caminho) throws IOException {
        BufferedReader reader;
        reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(caminho), "UTF-8"));
        return reader;
    }
    public BufferedReader carregaArq() throws FileNotFoundException, IOException {
        FileInputStream file = new FileInputStream(caminhoArquivo);
        InputStreamReader isr = new InputStreamReader(file, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
// file.close();
// isr.close();
        return br;
    }

    public void analisar(){
        try{
            BufferedReader br = carregaArq(caminhoArquivo);
            int contLinha = 0;
        String linha = "";
        boolean coment = false;
        ArrayList<Token> listaTokens;
        while (br.ready()) {
            listaTokens = new ArrayList<Token>();
            contLinha++;
            linha = br.readLine();
            Token token = null;
            String valorToken = "";
            for (int i = 0; i < linha.length(); i++) {
                if (linha.charAt(i) == '#') {
                    coment = !coment;
                }
                if (!coment) {
                    if (linha.charAt(i) == '"') {//se tiver aspas é string e nao pega aspas
                        valorToken = "";
                        i++;
                        while (linha.charAt(i) != '"') {//enquanto nao houver outra aspas
                            valorToken += linha.charAt(i); //poe tudo dentro no token
                            i++;
                        }
                        token = new Token(expres.get("string"), valorToken); //tipo string
                        listaTokens.add(token);
                        //System.out.println("!!Cont: " + contLinha + "Token: " + valorToken);
                        valorToken = "";//zera o valor que estava no token pois já adicionou
                    } else if (Character.isLetter(linha.charAt(i)) && linha.charAt(i) != 'x') { //se for letra diferente de x
                        valorToken += linha.charAt(i); //a string recebe a letra
                        if (expres.containsKey(valorToken)) { //se for uma expressao conhecida
                            if (linha.length() > (i + 1) && (linha.charAt(i + 1) == ' ' || linha.charAt(i + 1) == '.' 
                                    || linha.charAt(i + 1) == ',' || linha.charAt(i + 1) == '(' 
                                    || Character.isDigit(linha.charAt(i + 1)))) {//se a palavra tiver terminado ou vier um numero depois
                                if (Character.isDigit(linha.charAt(i + 1))) {//se for numero
                                    valorToken += linha.charAt(i + 1);//adiciona o numero a string
                                    if (expres.containsKey(valorToken)) { //se for expressao conhecida adiciona
                                        token = new Token(expres.get(valorToken), valorToken);
                                        listaTokens.add(token);
                                        //System.out.println("fun2  Cont: " + contLinha + " Token: " + valorToken);
                                        valorToken = "";
                                    } else if (linha.length() > (i + 2) && (linha.charAt(i + 2) == ' ' || linha.charAt(i + 2) == '(')) {
                                        token = new Token(expres.get("var"), valorToken); //se nao for conhecida e termina no número adiciona uma nova
                                        listaTokens.add(token);
                                        //System.out.println("fun2  Cont: " + contLinha + " Token: " + valorToken);
                                        valorToken = "";
                                    } else { //se nao termina, incrementa a linha pra continuar a "palavra"
                                        i++;
                                    }

                                } else { //se nao for numero, entao adiciona a expressao conhecida
                                    token = new Token(expres.get(valorToken), valorToken);
                                    listaTokens.add(token);
                                    //System.out.println("lex  Cont: " + contLinha + " Token: " + valorToken);
                                    valorToken = "";
                                }

                            }
                            if (linha.length() == i + 1) { //se termina naquele caracter
                                if (expres.containsKey(valorToken)) { //se for expressao adiciona com o valor dela
                                    token = new Token(expres.get(valorToken), valorToken);
                                    listaTokens.add(token);
                                    //System.out.println("lex2 Cont: " + contLinha + " Token: " + valorToken);
                                    valorToken = "";
                                } else { //se nao for adiciona uma nova
                                    token = new Token(expres.get("var"), valorToken);
                                    listaTokens.add(token);
                                    //System.out.println("lex2 Cont: " + contLinha + " Token: " + valorToken);
                                    valorToken = "";
                                }

                            }

                        } else if (valorToken.equals("fim") && (i + 1) < linha.length() && linha.charAt(i + 1) == '-') {
                            valorToken += linha.charAt(i + 1); //se a palavra chegando for fim
                            //adiciona o hífen e incrementa o i para pegar o resto da expressao
                            i++;
                            //System.out.println("entrou "+i+ " palavra: " + t);
                        } else {//se o caracter n tiver inscrito nos lexemas
                            if (linha.length() > (i + 1) && (linha.charAt(i + 1) == ' ' || linha.charAt(i + 1) == '.' || linha.charAt(i + 1) == ',')) {
                                token = new Token(expres.get("var"), valorToken); //se termina a palavra aqui adiciona nova
                                listaTokens.add(token);
                                //System.out.println("oioioivar Cont: " + contLinha + " Token: " + valorToken);
                                valorToken = "";
                            } else if (linha.length() > (i + 1) && (linha.charAt(i + 1) == '(' || linha.charAt(i + 1) == ')' || linha.charAt(i + 1) == '[' || linha.charAt(i + 1) == ']')) {
                                //System.out.println("Vish " + linha.charAt(i) + linha.charAt(i + 1));
                                String tt = ""; //se depois da palavra vem um colchete ou parenteses
                                tt += linha.charAt(i); //precisa tratar
                                token = new Token(expres.get("var"), valorToken);
                                listaTokens.add(token);
                                //System.out.println("vartt Cont: " + contLinha + " Token: " + valorToken);
                            } else if (linha.length() > (i + 1) && (Character.isDigit(linha.charAt(i + 1)))) {//se depois vem numero
                                if (linha.length() > (i + 2) && (linha.charAt(i + 2) == ' ' || linha.charAt(i + 2) == '.' || linha.charAt(i + 2) == '(' || linha.charAt(i + 2) == ')' || linha.charAt(i + 2) == ',')) {
                                    valorToken += linha.charAt(i + 1); //se depois do numero termina                                    
                                    token = new Token(expres.get("var"), valorToken); 
                                    listaTokens.add(token);
                                    //System.out.println("varnumero Cont: " + contLinha + " Token: " + valorToken);
                                    i++;
                                    valorToken = "";
                                }
                            }
                            if (linha.length() == i + 1) { //se linha terminou
                                //System.out.println(t);
                                token = new Token(expres.get("var"), valorToken);
                                //System.out.println("Cont: "+ count + "Token: " + t);
                                listaTokens.add(token);
                                //System.out.println("var2 Cont: " + contLinha + " Token: " + valorToken);
                                valorToken = "";
                            }
                        }
                        //t = "";
                    } else if (linha.charAt(i) == '>' || linha.charAt(i) == '<' || linha.charAt(i) == '=') { //se for comparativo
                        valorToken = "";
                        valorToken += linha.charAt(i);

                        if (linha.length() > (i + 1) && (linha.charAt(i + 1) == '>' || linha.charAt(i + 1) == '<' || linha.charAt(i + 1) == '=')) {
                            String proximo = valorToken + linha.charAt(i + 1);//se tiver 2 caracteres comparativos
                            if (expres.containsKey(proximo)) {
                                valorToken = proximo;
                            }
                            i++;
                        }
                        listaTokens.add(new Token(expres.get(valorToken), valorToken));
                        //System.out.println("1Cont: " + contLinha + "Token: " + valorToken);
                        valorToken = "";
                    } else if ((i + 1) < linha.length() && (linha.charAt(i) + "" + linha.charAt(i + 1)).equals("!=")) {
                        valorToken = linha.charAt(i) + "" + linha.charAt(i + 1); //se a string for !=
                        listaTokens.add(new Token(expres.get(valorToken), valorToken));
                        //System.out.println("2Cont: " + contLinha + "Token: " + valorToken);
                        i++;
                        valorToken = "";
                    } else if (Character.isDigit(linha.charAt(i))) {//se for um número
                        //t = "";
                        if (i > 0 && Character.isLetter(linha.charAt(i - 1)) && linha.charAt(i - 1) != 'x') {
                            valorToken += linha.charAt(i);//se o caracter anterior for letra e nao for x
                            if (expres.containsKey(valorToken)) {// se for expressao conhecida
//t = "" + t + "";              
                                if (linha.length() > (i + 1) && (linha.charAt(i + 1) == ' ' || linha.charAt(i + 1) == '.' || linha.charAt(i + 1) == ',')) {
                                    token = new Token(expres.get(valorToken), valorToken);//se termina nesse caracter
                                    listaTokens.add(token);
                                    //System.out.println("3Cont: " + contLinha + "Token: " + valorToken);
                                    valorToken = "";
                                }

                            }
                        } else { //se nao for letra
                            boolean inteiro = true;
                            do {
                                valorToken = valorToken + linha.charAt(i);
                                i++;
                                if (i < linha.length() && (linha.charAt(i) == '.' || linha.charAt(i) == ',') && (i + 1) < linha.length() && Character.isDigit(linha.charAt(i + 1)) && inteiro) {
                                    valorToken = valorToken + linha.charAt(i);//se tiver ponto ou vírgula
                                    i++;
                                    inteiro = false; //nao é inteiro
                                }
                                if (i < linha.length() && !Character.isDigit(linha.charAt(i))) {
                                    i--;
                                    break;
                                }
                            } while (i < linha.length() && Character.isDigit(linha.charAt(i)));//enquanto for número
                            if (inteiro) {
                                token = new Token(expres.get("int"), valorToken);
                            } else {
                                token = new Token(expres.get("float"), valorToken);
                            }
                            listaTokens.add(token);
                            //System.out.println("4Cont: " + contLinha + "Token: " + valorToken);
                            valorToken = "";
                        }
                    } else if ((linha.charAt(i) == 'x' || linha.charAt(i) == ':' || linha.charAt(i) == '*' || linha.charAt(i) == '/' || linha.charAt(i) == '+' || linha.charAt(i) == '-')
                            && i > 0 && (i + 1) < linha.length() && ((linha.charAt(i - 1) == ' ' && linha.charAt(i + 1) == ' ') || (Character.isDigit((linha.charAt(i - 1))) && Character.isDigit(linha.charAt(i + 1))) || (linha.charAt(i + 1) == '(' && (linha.charAt(i - 1) == ' ' || Character.isLetter(i - 1) || Character.isDigit(linha.charAt(i - 1)))))) {
                        //confere se é algum tipo de operação x : / * etc
                        valorToken = linha.charAt(i) + "";
                        //System.err.println('"' + t + '"');
                        listaTokens.add(new Token(expres.get(valorToken), valorToken));
                        //System.out.println("5Cont: " + contLinha + "Token: " + valorToken);
                        valorToken = "";
                    }else if (Character.isLetter(linha.charAt(i)) && linha.charAt(i) == 'x') {//se for x
                        if ((i + 1) < linha.length()) { //se nao for final da linha

                            if (Character.isLetter(linha.charAt(i + 1))) {//se depois for letra
                                valorToken += linha.charAt(i);//adiciona o x normalmente

                            } else if (linha.charAt(i + 1) == ' ') {
                                valorToken += linha.charAt(i);
                                if (expres.containsKey(valorToken)) {

                                    token = new Token(expres.get(valorToken), valorToken);
                                    listaTokens.add(token);
                                    //System.out.println("7Cont: " + contLinha + "Token: " + valorToken);
                                    valorToken = "";
                                }//se nao for conhecida???
                            }
                        } else if ((i + 1) == linha.length()) {//se for final da linha
                            valorToken += linha.charAt(i);
                            if (expres.containsKey(valorToken)) {

                                token = new Token(expres.get(valorToken), valorToken);
                                listaTokens.add(token);
                                System.out.println("8Cont: " + contLinha + "Token: " + valorToken);
                                valorToken = "";
                            }//se nao for conhecida???
                        }

                    }else if (coment == false && !Character.isLetter(linha.charAt(i)) && !Character.isDigit(linha.charAt(i)) && linha.charAt(i) != ' ' && linha.charAt(i) != '.' && linha.charAt(i) != ',' && linha.charAt(i) != '#') {
                        //System.out.println("22Cont: " + contLinha + " Caracter: " + linha.charAt(i));
                        if (linha.charAt(i) == '(' || linha.charAt(i) == ')' || linha.charAt(i) == ']' || linha.charAt(i) == '[') {
                            //tratar parenteses
                            String tt = "";
                            tt += linha.charAt(i);
                            listaTokens.add(new Token(expres.get(tt), tt));
                            //System.out.println("22Cont: " + contLinha + "Token: " + tt);
                            valorToken = "";
                        } else if (contLinha != 1 && i != 0) {
                            listaTokens.add(new Token(expres.get("var"), valorToken));
                            //System.out.println("22Cont: " + contLinha + "Token: " + valorToken);
                            valorToken = "";
                        }
                    }
                }
            }
            linhaToken.put(contLinha, listaTokens);
        }
        br.close();
        }catch (IOException ex) {
            Logger.getLogger(Analisador.class.getName()).log(Level.SEVERE, "Arquivo nao encontrado", ex);
        }
        
        

        for (Map.Entry<Integer, ArrayList<Token>> entrySet
                : linhaToken.entrySet()) {
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
