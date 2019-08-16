package br.com;

import br.com.github.GitManagement;

public class GitTest {
    public static void main(String[] args) {
        try{
            GitManagement git = new GitManagement("https://github.com/matheusflauzino/crawler-fdroid.git");
            git.cloneRepository();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
