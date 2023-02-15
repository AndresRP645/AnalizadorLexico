package Analizador;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;

public class Funciones {

    private ArrayList<String> especiales = new ArrayList<>();
    private ArrayList<String> reservadas = new ArrayList<>();
    private ArrayList<String> palabra = new ArrayList<>();
    private ArrayList<String> Error = new ArrayList<>();
    private ArrayList<String> id = new ArrayList<>();
    private ArrayList<String> numeros = new ArrayList<>();
    private ArrayList<String> archivo = new ArrayList<>();
    private ArrayList<String> token = new ArrayList<>();
    private String file;
    private JTextArea text;
    private JTextArea source;

    ArrayList<Character> word = new ArrayList<>(),
            number = new ArrayList<>(),
            simbRaros = new ArrayList<>();

    ArrayList<String> C_Especiales = new ArrayList<>(),
            P_Reservadas = new ArrayList<>(),
            palabras = new ArrayList<>();

    private String[] palabrasReservadas = {"INT", "int", "DOUBLE", "double", "LONG", "long", "FLOAT", "float", "CHAR", "char", "ESTRUCT", "estruct", "FALSE", "false", "TRUE", "true", "IF", "if", "FOR", "for"};
    private String[] operaciones = {"+", "-", "*", "/", "=", "(", ")", ",", ";"};

    public Funciones(JTextArea text, JTextArea source) throws IOException {
        this.text = text;
        this.source = source;
        abreArchivo();
        tokens();
        init();
    }

    public void print() {
        text.setText("");
        text.append("--------------Tokens encontrados--------------");
        text.append("\n");
        text.append("Palabras:=" + palabra.size());
        text.append("\n");
        text.append("Id:=" + id.size());
        text.append("\n");
        text.append("Numeros:=" + numeros.size());
        text.append("\n");
        String[] pr = {"int", "double", "long", "float", "char", "estruct", "false", "true", "if", "for"};
        for (int i = 0; i < pr.length; i++) {
            text.append(pr[i] + ":=");
            int cont = 0;
            for (int j = 0; j < reservadas.size(); j++) {
                if (reservadas.get(j).toLowerCase().equals(pr[i])) {
                    cont++;
                }

            }
            text.append(cont + "");
            text.append("\n");
        }

        for (int i = 0; i < operaciones.length; i++) {
            text.append(operaciones[i] + ":=");
            int cont = 0;
            for (int j = 0; j < especiales.size(); j++) {
                if (especiales.get(j).equals(operaciones[i])) {
                    cont++;
                }

            }
            text.append(cont + "");
            text.append("\n");
        }

        text.append("Errores:=" + Error.size());
        text.append("\n");
        int total = palabra.size() + id.size() + numeros.size() + reservadas.size() + especiales.size() + Error.size();
        text.append("Total de tokens:=" + total);
        text.append("\n");
    }

    public void printTokens() {
        source.setText("");
        for (String str : token) {
            String token = 
                    reservadas.contains(str) ? "Palabra Reservada" :
                    id.contains(str) ? "Id" :
                    numeros.contains(str) ? "Numero" :
                    palabra.contains(str) ? "Palabra" :
                    Error.contains(str) ? "Error" :
                    especiales.contains(str) ? "Caracter Especial" :
                    "Caracter Desconocido";
            
            if (token.equals("Caracter Especial"))
            for (String operacion : operaciones) {
                switch (str) {
                    case "+":
                        token = "Suma";
                        break;
                    case "-":
                        token = "Resta";
                        break;
                    case "*":
                        token = "Multiplicación";
                        break;
                    case "/":
                        token = "División";
                        break;
                    case "=":
                        token = "Asignación";
                        break;
                    case "(":
                        token = "Paréntesis Apertura";
                        break;
                    case ")":
                        token = "Paréntesis Cierre";
                        break;
                    case ",":
                        token = "Coma";
                        break;
                    case ";":
                        token = "Punto y Coma";
                        break;
                }
            }
            
            source.append("<" + token + ">  <\" " + str + " \">\n");
        }
    }

    public void init() throws IOException {

        C_Especiales();
        P_Reservadas();

        letras();
        numeros();

        separar();

        ArrayList<String> Esp = Especiales();

        especiales = especiales(Esp, C_Especiales);
        reservadas = especiales(palabras, P_Reservadas);

        Error = detectarErrores(Esp, C_Especiales);
        palabra = detectarErrores(palabras, P_Reservadas);

        palabra = detectarNumero(palabra, word);
        palabra = detectarIDs(palabra, number);

    }

    private void abreArchivo() throws IOException {
        JFileChooser archivo = new JFileChooser();
        archivo.showOpenDialog(archivo);
        File file = archivo.getSelectedFile();
        this.file = file.getAbsolutePath();
        if (file != null) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while (br.ready()) {
                String tem = br.readLine();
                source.append(tem);
                source.append("\n");
                String[] palabra = tem.split(" ");
                for (String string : palabra) {
                    if (!string.equals("")) {
                        this.archivo.add(string);
                    }
                }
            }
            br.close();
        }
    }

    private void letras() {
        for (char i = 'a'; i <= 'z'; i++) {
            word.add(i);
        }
        for (char i = 'A'; i <= 'Z'; i++) {
            word.add(i);
        }
        word.add( 'ñ');
        word.add( 'Ñ');
        word.add('_');

    }

    private void numeros() {
        for (char i = '0'; i < '9'; i++) {
            number.add(i);
        }
    }

    private void C_Especiales() {
        for (String c : operaciones) {
            C_Especiales.add(c);
        }
    }

    private void P_Reservadas() {
        for (String pr : palabrasReservadas) {
            P_Reservadas.add(pr);
        }
    }

    private ArrayList<Character> error(String cadena) {
        char[] cad = cadena.toCharArray();
        ArrayList<Character> cade = new ArrayList();
        for (int i = 0; i < cad.length; i++) {
            if (!word.contains(cad[i]) && !number.contains(cad[i])) {
                cade.add(cad[i]);
            }
        }
        return cade;
    }

    private ArrayList<String> divide(String cadena) {
        char[] cad = cadena.toCharArray();
        ArrayList<String> let = new ArrayList();
        String cade = "";
        for (int i = 0; i < cad.length; i++) {
            if (word.contains(cad[i]) || number.contains(cad[i])) {
                cade += cad[i];
            } else {
                if (!cade.equals("")) {
                    let.add(cade);
                }
                cade = "";
            }

        }
        if (!cade.equals("")) {
            let.add(cade);
        }
        return let;
    }

    private ArrayList<String> Especiales() {
        ArrayList<String> Error = new ArrayList();
        simbRaros.forEach(l -> Error.add(String.valueOf(l)));
        return Error;
    }

    private void separar() {
        for (String string : archivo) {
            palabras.addAll(divide(string));
            simbRaros.addAll(error(string));
        }
    }

    private ArrayList<String> especiales(ArrayList<String> string, ArrayList<String> compara) {
        ArrayList<String> especiales = new ArrayList();
        for (String especial : string) {
            if (compara.contains(especial)) {
                especiales.add(especial);
            }
        }
        return especiales;
    }

    private ArrayList<String> detectarErrores(ArrayList<String> string, ArrayList<String> compara) {
        ArrayList<String> especiales = new ArrayList();
        for (String especial : string) {
            if (!compara.contains(especial)) {
                especiales.add(especial);
            }
        }
        return especiales;
    }

    private boolean id(String ID, ArrayList<Character> numeros) {
        char[] cadena = ID.toCharArray();
        if (numeros.contains(cadena[0])) {
            Error.add(ID);
            return false;
        }
        for (char c : cadena) {
            if (numeros.contains(c)) {
                id.add(ID);
                return false;
            }
        }
        return true;
    }

    private ArrayList<String> detectarIDs(ArrayList<String> palabras, ArrayList<Character> numeros) {
        ArrayList<String> dropID = new ArrayList();
        for (String palabra : palabras) {
            if (id(palabra, numeros)) {
                dropID.add(palabra);
            }
        }
        return dropID;
    }

    private boolean numero(String num, ArrayList<Character> letras) {
        char[] cadena = num.toCharArray();
        for (char c : cadena) {
            if (letras.contains(c)) {
                return true;
            }
        }
        numeros.add(num);
        return false;
    }

    private ArrayList<String> detectarNumero(ArrayList<String> palabras, ArrayList<Character> letras) {
        ArrayList<String> dropNumero = new ArrayList();
        for (String palabra : palabras) {
            if (numero(palabra, letras)) {
                dropNumero.add(palabra);
            }
        }
        return dropNumero;
    }

    private void tokens() {
        letras();
        numeros();
        
        for (String line : archivo) {
            char[] cadena = line.toCharArray();
            String tmp = "";

            for (int i = 0; i < cadena.length; i++) {
                char caracter = cadena[i];
                if (word.contains(caracter) || number.contains(caracter)) {
                    tmp += caracter;
                    if (i + 1 < cadena.length) {
                        if (!word.contains(cadena[i + 1]) && !number.contains(cadena[i + 1])) {
                            token.add(tmp);
                            tmp = "";
                        }
                    } else if (!(i + 1 < cadena.length)) {
                        token.add(tmp);
                        tmp = "";
                    }
                }
            }
            for (char caracter : cadena) {
                if (!word.contains(caracter) && !number.contains(caracter)) {
                    token.add(String.valueOf(caracter));
                }
            }
        }
    }
}
