package br.com.br.com.api;

import java.util.ArrayList;
import java.util.List;

import br.com.AndroidDetector.AndroidJavaCodeSmells;
import br.com.AndroidDetector.AndroidLayoutSmells;
import br.com.AndroidDetector.OutputSmells;
import br.com.github.GitManagement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static br.com.UTIL.Constants.*;

@CrossOrigin(origins = "*")
@RequestMapping("/detector")
@Controller
public class SmellController {
    private List<OutputSmells> listSmells;

    @RequestMapping(value = "/DeepNestedLayout", method = RequestMethod.POST)
    public ResponseEntity<List<OutputSmells>> deepNestedLayout(@RequestParam("repository") String repository) {
        try{
            GitManagement git = new GitManagement(repository);
            if(git.cloneRepository()){
                System.out.println(git.getRepositorio());
                listSmells = AndroidLayoutSmells.DeepNestedLayout(git.getRepositorio(), THRESHOLD_DEEPNESTEDLAYOUT);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        return new ResponseEntity<List<OutputSmells>>(new ArrayList<OutputSmells>(listSmells), HttpStatus.OK);
    }

    @RequestMapping(value = "/DuplicateStyleAttributes", method = RequestMethod.POST)
    public ResponseEntity<List<OutputSmells>> duplicateStyleAttributes(@RequestParam("repository") String repository) throws Exception {
        try{
            GitManagement git = new GitManagement(repository);
            if(git.cloneRepository()){
                System.out.println(git.getRepositorio());
                listSmells = AndroidLayoutSmells.DuplicateStyleAttributes(git.getRepositorio());
            }

            return new ResponseEntity<List<OutputSmells>>(new ArrayList<OutputSmells>(listSmells), HttpStatus.OK);

        }
        catch(Exception ex){
            ex.printStackTrace();
            throw new Exception();
        }
    }

    @RequestMapping(value = "/GodStyleResource", method = RequestMethod.POST)
    public ResponseEntity<List<OutputSmells>> godStyleResource(@RequestParam("repository") String repository) throws Exception {
        try{
            GitManagement git = new GitManagement(repository);
            if(git.cloneRepository()){
                System.out.println(git.getRepositorio());
                listSmells = AndroidLayoutSmells.GodStyleResource(git.getRepositorio(), THRESHOLD_GODSTYLERESOURCE);
            }

            return new ResponseEntity<List<OutputSmells>>(new ArrayList<OutputSmells>(listSmells), HttpStatus.OK);
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw new Exception();
        }
    }

    @RequestMapping(value = "/HiddenListener", method = RequestMethod.POST)
    public ResponseEntity<List<OutputSmells>> HiddenListener(@RequestParam("repository") String repository) throws Exception {
        try{
            GitManagement git = new GitManagement(repository);
            if(git.cloneRepository()){
                System.out.println(git.getRepositorio());
                listSmells = AndroidLayoutSmells.HiddenListener(git.getRepositorio());
            }

            return new ResponseEntity<List<OutputSmells>>(new ArrayList<OutputSmells>(listSmells), HttpStatus.OK);
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw new Exception();
        }
    }

    @RequestMapping(value = "/magicResource", method = RequestMethod.POST)
    public ResponseEntity<List<OutputSmells>> magicResource(@RequestParam("repository") String repository) throws Exception {
        try{
            GitManagement git = new GitManagement(repository);
            if(git.cloneRepository()){
                System.out.println(git.getRepositorio());
                listSmells = AndroidLayoutSmells.magicResource(git.getRepositorio());
            }

            return new ResponseEntity<List<OutputSmells>>(new ArrayList<OutputSmells>(listSmells), HttpStatus.OK);
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw new Exception();
        }
    }

    @RequestMapping(value = "/godStringResource", method = RequestMethod.POST)
    public ResponseEntity<List<OutputSmells>> godStringResource(@RequestParam("repository") String repository) throws Exception {
        try{
            GitManagement git = new GitManagement(repository);
            if(git.cloneRepository()){
                System.out.println(git.getRepositorio());
                listSmells = AndroidLayoutSmells.godStringResource(git.getRepositorio());
            }

            return new ResponseEntity<List<OutputSmells>>(new ArrayList<OutputSmells>(listSmells), HttpStatus.OK);
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw new Exception();
        }
    }

    @RequestMapping(value = "/inappropriateStringReuse", method = RequestMethod.POST)
    public ResponseEntity<List<OutputSmells>> inappropriateStringReuse(@RequestParam("repository") String repository) throws Exception {
        try{
            GitManagement git = new GitManagement(repository);
            if(git.cloneRepository()){
                System.out.println(git.getRepositorio());
                listSmells = AndroidLayoutSmells.inappropriateStringReuse(git.getRepositorio());
            }

            return new ResponseEntity<List<OutputSmells>>(new ArrayList<OutputSmells>(listSmells), HttpStatus.OK);
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw new Exception();
        }
    }

    @RequestMapping(value = "/NotFoundImage", method = RequestMethod.POST)
    public ResponseEntity<List<OutputSmells>> NotFoundImage(@RequestParam("repository") String repository) throws Exception {
        try{
            GitManagement git = new GitManagement(repository);
            if(git.cloneRepository()){
                System.out.println(git.getRepositorio());
                listSmells = AndroidLayoutSmells.NotFoundImage(git.getRepositorio());
            }

            return new ResponseEntity<List<OutputSmells>>(new ArrayList<OutputSmells>(listSmells), HttpStatus.OK);
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw new Exception();
        }
    }

    @RequestMapping(value = "/CoupledUIComponent", method = RequestMethod.POST)
    public ResponseEntity<List<OutputSmells>> CoupledUIComponent(@RequestParam("repository") String repository) throws Exception {
        try{
            GitManagement git = new GitManagement(repository);
            if(git.cloneRepository()){
                System.out.println(git.getRepositorio());
                listSmells = AndroidJavaCodeSmells.CoupledUIComponent(git.getRepositorio());
            }

            return new ResponseEntity<List<OutputSmells>>(new ArrayList<OutputSmells>(listSmells), HttpStatus.OK);
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw new Exception();
        }
    }


    @RequestMapping(value = "/SuspiciousBehavior", method = RequestMethod.POST)
    public ResponseEntity<List<OutputSmells>> SuspiciousBehavior(@RequestParam("repository") String repository) throws Exception {
        try{
            GitManagement git = new GitManagement(repository);
            if(git.cloneRepository()){
                System.out.println(git.getRepositorio());
                listSmells = AndroidJavaCodeSmells.SuspiciousBehavior(git.getRepositorio());
            }

            return new ResponseEntity<List<OutputSmells>>(new ArrayList<OutputSmells>(listSmells), HttpStatus.OK);
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw new Exception();
        }
    }

    @RequestMapping(value = "/FlexAdapter", method = RequestMethod.POST)
    public ResponseEntity<List<OutputSmells>> FlexAdapter(@RequestParam("repository") String repository) throws Exception {
        try{
            GitManagement git = new GitManagement(repository);
            if(git.cloneRepository()){
                System.out.println(git.getRepositorio());
                listSmells = AndroidJavaCodeSmells.FlexAdapter(git.getRepositorio());
            }

            return new ResponseEntity<List<OutputSmells>>(new ArrayList<OutputSmells>(listSmells), HttpStatus.OK);
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw new Exception();
        }
    }

    @RequestMapping(value = "/FoolAdapter", method = RequestMethod.POST)
    public ResponseEntity<List<OutputSmells>> FoolAdapter(@RequestParam("repository") String repository) throws Exception {
        try{
            GitManagement git = new GitManagement(repository);
            if(git.cloneRepository()){
                System.out.println(git.getRepositorio());
                listSmells = AndroidJavaCodeSmells.FoolAdapter(git.getRepositorio());
            }

            return new ResponseEntity<List<OutputSmells>>(new ArrayList<OutputSmells>(listSmells), HttpStatus.OK);
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw new Exception();
        }
    }

    @RequestMapping(value = "/BrainUIComponent", method = RequestMethod.POST)
    public ResponseEntity<List<OutputSmells>> BrainUIComponent(@RequestParam("repository") String repository) throws Exception {
        try{
            GitManagement git = new GitManagement(repository);
            if(git.cloneRepository()){
                System.out.println(git.getRepositorio());
                listSmells = AndroidJavaCodeSmells.BrainUIComponent(git.getRepositorio());
            }

            return new ResponseEntity<List<OutputSmells>>(new ArrayList<OutputSmells>(listSmells), HttpStatus.OK);
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw new Exception();
        }
    }

    @RequestMapping(value = "/CompUIIO", method = RequestMethod.POST)
    public ResponseEntity<List<OutputSmells>> CompUIIO(@RequestParam("repository") String repository) throws Exception {
        try{
            GitManagement git = new GitManagement(repository);
            if(git.cloneRepository()){
                System.out.println(git.getRepositorio());
                listSmells = AndroidJavaCodeSmells.CompUIIO(git.getRepositorio());
            }

            return new ResponseEntity<List<OutputSmells>>(new ArrayList<OutputSmells>(listSmells), HttpStatus.OK);
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw new Exception();
        }
    }

    @RequestMapping(value = "/NotFragment", method = RequestMethod.POST)
    public ResponseEntity<List<OutputSmells>> NotFragment(@RequestParam("repository") String repository) throws Exception {
        try{
            GitManagement git = new GitManagement(repository);
            if(git.cloneRepository()){
                System.out.println(git.getRepositorio());
                listSmells = AndroidJavaCodeSmells.NotFragment(git.getRepositorio());
            }

            return new ResponseEntity<List<OutputSmells>>(new ArrayList<OutputSmells>(listSmells), HttpStatus.OK);
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw new Exception();
        }
    }

    @RequestMapping(value = "/ExcessiveFragment", method = RequestMethod.POST)
    public ResponseEntity<List<OutputSmells>> ExcessiveFragment(@RequestParam("repository") String repository) throws Exception {
        try{
            GitManagement git = new GitManagement(repository);
            if(git.cloneRepository()){
                System.out.println(git.getRepositorio());
                listSmells = AndroidJavaCodeSmells.ExcessiveFragment(git.getRepositorio(), THRESHOLD_EXCESSIVEFRAGMENT);
            }

            return new ResponseEntity<List<OutputSmells>>(new ArrayList<OutputSmells>(listSmells), HttpStatus.OK);
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw new Exception();
        }
    }
}
