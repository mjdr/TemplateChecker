package templateChecker;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;

import templateChecker.css.Lexer;
import templateChecker.css.Parser;
import templateChecker.css.Token;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
    	TemplateManager manager = new TemplateManager();
    	
    	Stream.of(new File("resources/views/templates").listFiles())
    	.filter((f) -> f.isFile())
    	.map((f) -> new HTMLTemplate(manager, f))
    	.filter((t) -> t.getErrors().size() + t.getWarnings().size() > 0)
    	.forEach((t)->{t.printErrorsAndWarnings();System.out.println();});
    	
    	
//    	Lexer lexer = new Lexer();
//    	String css = new String(Files.readAllBytes(Paths.get("/home/jdr/web/hermitage/public/css/home.css")));
//    	
//    	List<Token> tokens = lexer.tokenize(css);
//    	Parser parser = new Parser();
//    	
//    	parser.parse(tokens);
//    	
//    	System.out.println("=============class ===========");
//    	parser.getClasses().forEach(System.out::println);
//    	System.out.println("=============  id  ===========");
//    	parser.getIds().forEach(System.out::println);
//    	System.out.println("=============comments===========");
//    	parser.getComments().forEach(System.out::println);
    	
    	//tokens.forEach(System.out::println);
    	
        
    }
}
