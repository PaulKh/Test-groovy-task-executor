import junit.framework.TestCase;
import model.DatabaseHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.junit.Test;

/**
 * Created by Paul on 16/08/15.
 */
public class DatabaseTest extends TestCase {
    private String simpleScript = "class Person {\n" +
            "    String name\n" +
            "}\n" +
            "def p = new Person(name: 'Norman')\n" +
            "p.getName()";
    @Test
    public void testAddToDatabase(){
        int sizeBefore = DatabaseHandler.getInstance().getAll().size();
        int id = DatabaseHandler.getInstance().addNewTask(simpleScript);
        int sizeAfter = DatabaseHandler.getInstance().getAll().size();
        assertEquals(sizeBefore + 1, sizeAfter);
    }
}
