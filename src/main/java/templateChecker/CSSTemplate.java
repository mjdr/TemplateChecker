package templateChecker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.steadystate.css.parser.CSSOMParser;

import templateChecker.css.Lexer;
import templateChecker.css.Parser;
import templateChecker.css.Token;

public class CSSTemplate {
	private TemplateManager parent;
	private File file;
	private String name;
	private Lexer lexer;
	private Parser parser;
	private List<String> errors;
	private List<String> warnings;
	
	public CSSTemplate(TemplateManager parent, File file) {
		this.parent = parent;
		this.file = file;
		
		lexer = new Lexer();
		parser = new Parser();

		errors = new ArrayList<String>();
		warnings = new ArrayList<String>();
		
	}
	
	public boolean preprocess() {
		Pattern pattern = Pattern.compile("([a-z0-9_]+)\\.css");
		Matcher matcher = pattern.matcher(file.getName());
		
		if(matcher.find())
			name = matcher.group(1);
		else {
			errors.add("Template has wrong name");
			return true;
		}
		String css;
    	try {
			css = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
		} catch (IOException e) {
			errors.add("Can't load file " + e.getMessage());
			return false;
		}
    	
    	List<Token> tokens = lexer.tokenize(css);
    	try {
    		parser.parse(tokens);
    	}catch (Exception e) {
    		errors.add("CSS Parse error: " + e.getMessage());
    		return false;
		}
    	
    	
    	
    	if(getIds().size() > 0)
			errors.add("CSS has ids: " + getIds().stream().reduce("\n\t\t\t", (acc, c) -> acc + c + "\n\t\t\t"));
		
		Set<String> cssBadClasses = getClasses().stream()
				.filter((c) -> !c.startsWith(name + "_"))
				.collect(Collectors.toSet());
		
		if(cssBadClasses.size() > 0)
			errors.add("CSS has \"bad\" named classes("+ cssBadClasses.size() +"): " + cssBadClasses.stream().reduce("\n\t\t\t", (acc, c) -> acc + c + "\n\t\t\t"));
		
    	
    	if(parser.getComments().size() > 0)
    		warnings.add("CSS Comments("+parser.getComments().size()+")");
    	
    	Set<String> redefClasses = getClasses().stream()
				.filter((c) -> HTMLTemplateData.isExcuceClass(c))
				.collect(Collectors.toSet());
    	
    	if(redefClasses.size() > 0)
			errors.add("CSS has re defiend classes("+ redefClasses.size() +"): " + redefClasses.stream().reduce("\n\t\t\t", (acc, c) -> acc + c + "\n\t\t\t"));
		
    	
    	return true;
	}
	
	public List<String> getIds() {
		return parser.getIds();
	}
	public List<String> getClasses() {
		return parser.getClasses();
	}
	public List<String> getComments() {
		return parser.getComments();
	}
	public List<String> getErrors() {
		return errors;
	}
	public List<String> getWarnings() {
		return warnings;
	}
}
