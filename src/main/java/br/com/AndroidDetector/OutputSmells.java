package br.com.AndroidDetector;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import static br.com.UTIL.Constants.PATH_SAVE_JSON;

public class OutputSmells {
    private String arquivo;
    private String linha;
    private String coluna;
    private String tipoSmell;

    public String getColuna() {
        return coluna;
    }

    public void setColuna(String coluna) {
        this.coluna = coluna;
    }

    public String getTipoSmell() {
        return tipoSmell;
    }

    public void setTipoSmell(String tipoSmell) {
        this.tipoSmell = tipoSmell;
    }

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public void saveJson(List<?> smells, String file) {
        try {
            File diretorio = new File(PATH_SAVE_JSON);
            diretorio.mkdir();
            Gson gson = new Gson();
            try {
                final Writer writer = new FileWriter(PATH_SAVE_JSON + file);
                gson.toJson(smells, writer);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
