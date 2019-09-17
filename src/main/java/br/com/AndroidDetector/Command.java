package br.com.AndroidDetector;

import br.com.github.GitManagement;

import static br.com.UTIL.Constants.*;

public class Command {
    public static void main(String[] args) {
        try{
            String pathToAndroid = args[0];
            String pathToSaveResults = args[1];
            String smell = args[2];

            if(pathToAndroid.isEmpty()){
                throw new Exception("Invalid path android application.");
            }

            if(pathToSaveResults.isEmpty()){
                throw new Exception("Invalid path for save results");
            }

            if(smell.isEmpty()){
                throw new Exception("Invalid smell.");
            }

            if(pathToAndroid.endsWith(".git")) {
                GitManagement git = new GitManagement(pathToAndroid);
                if (git.cloneRepository()) {
                    System.out.println("Git repository: " + git.getRepositorio());
                    pathToAndroid = git.getRepositorio();
                }
            }

            if(smell.equals("DeepNestedLayout") ) {
                AndroidLayoutSmells.DeepNestedLayout(pathToAndroid, THRESHOLD_DEEPNESTEDLAYOUT);       //Layout Profundamente Aninhado
            }
            else if(smell.equals("DuplicateStyleAttributes")) {
                AndroidLayoutSmells.DuplicateStyleAttributes(pathToAndroid);          //Atributo de Estilo repetido
            }
            else if(smell.equals("GodStyleResource")) {
            AndroidLayoutSmells.GodStyleResource(pathToAndroid, THRESHOLD_GODSTYLERESOURCE);    //Longo Recurso de Estilo
            }
            else if(smell.equals("HiddenListener")) {
            AndroidLayoutSmells.HiddenListener(pathToAndroid);                 //Listener Escondido
            }
            else if(smell.equals("magicResource")) {
            AndroidLayoutSmells.magicResource(pathToAndroid);               //Recurso Mágico
            }
            else if(smell.equals("godStringResource")) {
            AndroidLayoutSmells.godStringResource(pathToAndroid);          //Recurso de String Bagunçado
            }
            else if(smell.equals("inappropriateStringReuse")) {
            AndroidLayoutSmells.inappropriateStringReuse(pathToAndroid);   //Reuso inadequado de String
            }
            else if(smell.equals("NotFoundImage")) {
            AndroidLayoutSmells.NotFoundImage(pathToAndroid);            //Imagem Faltante
            }
            else if(smell.equals("CoupledUIComponent")) {
            AndroidJavaCodeSmells.CoupledUIComponent(pathToAndroid);      //Componente de UI Acoplado
            }
            else if(smell.equals("SuspiciousBehavior")) {
            AndroidJavaCodeSmells.SuspiciousBehavior(pathToAndroid);     //Comportamento Suspeito
            }
            else if(smell.equals("FlexAdapter")) {
            AndroidJavaCodeSmells.FlexAdapter(pathToAndroid);           // Adapter Complexo
            }
            else if(smell.equals("FoolAdapter")) {
            AndroidJavaCodeSmells.FoolAdapter(pathToAndroid);           // Adapter
            }
            else if(smell.equals("BrainUIComponent")) {
            AndroidJavaCodeSmells.BrainUIComponent(pathToAndroid);     //Componente de Ui Cerebro
            }
            else if(smell.equals("CompUIIO")) {
            AndroidJavaCodeSmells.CompUIIO(pathToAndroid);            //Componente de UI fazendo IO
            }
            else if(smell.equals("NotFragment")) {
            AndroidJavaCodeSmells.NotFragment(pathToAndroid);        //Não Uso de Fragments
            }
            else if(smell.equals("ExcessiveFragment")) {
            AndroidJavaCodeSmells.ExcessiveFragment(pathToAndroid, THRESHOLD_EXCESSIVEFRAGMENT);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
