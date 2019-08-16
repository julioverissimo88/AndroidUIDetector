package br.com.github;

import org.eclipse.jgit.api.Git;
import static br.com.UTIL.Constants.*;


import java.io.File;

public class GitManagement {
    public GitManagement(String repositorio) {
        this.repositorio = repositorio;
    }

    public String getRepositorio() {
        return repositorio;
    }

    public void setRepositorio(String repositorio) {
        this.repositorio = repositorio;
    }

    private String repositorio;

    public Boolean cloneRepository() throws Exception {
        try{
            String[] folderArray = repositorio.split("/");
            String folder = folderArray[folderArray.length - 1].replace(".","");
            if(!(new File(PATH_CLONE_REPOSITORY + folder).exists())) {
                Git git = Git.cloneRepository().setURI(this.repositorio).setDirectory(new File(PATH_CLONE_REPOSITORY + folder)).call();
            }
            this.repositorio = PATH_CLONE_REPOSITORY + folder;
            return true;
        }
        catch(Exception ex){
            throw new Exception();
        }
    }

}
