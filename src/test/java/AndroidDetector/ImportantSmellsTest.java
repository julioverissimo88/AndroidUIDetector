package AndroidDetector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

public class ImportantSmellsTest {

    //Define as streams para teste, como existe código qe
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void seForFlexAdapterSmellyDeveAvisarNaTela() {
        ImportantSmells importantsmells = new ImportantSmells();

        importantsmells.FlexAdapter(new File(".").getAbsolutePath() + "/src/test/resources/FlexAdapter/Smelly/");

        Assert.assertThat(outContent.toString(), containsString("Flex Adapter detectado na classe"));

    }

    @Test
    public void seNaoForFlexAdapterSmellyNaoDeveAvisarNadaNaTela() {
        ImportantSmells importantsmells = new ImportantSmells();

        importantsmells.FlexAdapter(new File(".").getAbsolutePath() + "/src/test/resources/FlexAdapter/NotSmelly/");

        Assert.assertThat(outContent.toString(), not(containsString("Flex Adapter detectado na classe")));

    }

}