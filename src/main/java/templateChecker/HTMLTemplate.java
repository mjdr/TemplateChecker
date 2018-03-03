package templateChecker;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;


public class HTMLTemplate {
	private TemplateManager parent;
	private File file;
	private String name;
	private Document html;
	private Element document;
	private CSSTemplate cssTemplate;
	private Set<String> classes;
	private List<String> errors;
	private List<String> warnings;
	
	public HTMLTemplate(TemplateManager parent, File file) {
		this.parent = parent;
		this.file = file;
		classes = new HashSet<String>();
		errors = new ArrayList<String>();
		warnings = new ArrayList<String>();
		preprocess();
		
	}
	
	private void preprocess() {
		Pattern pattern = Pattern.compile("([a-z0-9_]+)\\.blade\\.php");
		Matcher matcher = pattern.matcher(file.getName());
		
		if(matcher.find())
			name = matcher.group(1);
		else {
			errors.add("Template has wrong name");
			return;
		}
		
		String html;
		try {
			html = FileUtils.readFileToString(file);
		} catch (IOException e) {
			errors.add("Load template fail " + e.getMessage());
			return;
		}
		this.html = Jsoup.parseBodyFragment(html);
		document = this.html.body();
		
		if(document.children().size() != 1)
			errors.add("Template must had 1 root tag");
		
		classes = document.getAllElements().stream()
				.filter((e) -> e.hasAttr("class"))
				.flatMap((e) -> e.classNames().stream())
				.collect(Collectors.toSet());
		
		Set<String> ids = document.getAllElements().stream()
				.filter((e) -> e.hasAttr("id"))
				.map((e) -> e.attr("id"))
				.collect(Collectors.toSet());
		
		if(ids.size() > 0)
			HTMLTemplateData.addEntry(errors, "Template has ids", ids);
			
		
		Set<String> badClasses = classes.stream()
				.filter((c) -> !c.startsWith(name + "_"))
				.filter((c) -> !c.startsWith("js_"+name + "_"))
				.filter((c) -> !HTMLTemplateData.isExcuceClass(c))
				.collect(Collectors.toSet());

		if(badClasses.size() > 0)
			HTMLTemplateData.addEntry(errors, "Template has \"bad\" named classes", badClasses);
		
		if(document.select("script").size() > 0)
			errors.add("Template has script tag");
		
		if(document.select("style").size() > 0)
			warnings.add("Template has style tag");
		
		List<Element> handleElements = 
		document.getAllElements().stream().filter((e) -> {
			return HTMLTemplateData.jsAtrrs.stream().anyMatch((h) -> e.hasAttr(h));
		}).collect(Collectors.toList());
		
		if(handleElements.size() > 0)
			errors.add("Template has javascript html hadlers");
		
		cssTemplate = parent.loadCSSTemplate(file, name);
		
		boolean res = cssTemplate.preprocess();

		errors.addAll(cssTemplate.getErrors());
		warnings.addAll(cssTemplate.getWarnings());
		
		if(!res) return;
		
		List<String>inTemplate = classes.stream()
				.filter((c) -> !cssTemplate.getClasses().contains(c)).filter(c -> !c.startsWith("js_"))
				.filter((c) -> !HTMLTemplateData.isExcuceClass(c))
				.collect(Collectors.toList());
		List<String>inCSS = cssTemplate.getClasses().stream()
				.filter((c) -> !classes.contains(c))
				.filter((c) -> !HTMLTemplateData.isExcuceClass(c))
				.collect(Collectors.toList());
		
		if(inTemplate.size() > 0)
			HTMLTemplateData.addEntry(warnings, "Template useless classes: ", inTemplate);
		if(inCSS.size() > 0)
			HTMLTemplateData.addEntry(warnings, "CSS useless classes: ", inCSS);
		
		
		
	}
	
	
	public List<String> getErrors() {
		return errors;
	}
	
	public List<String> getWarnings() {
		return warnings;
	}
	
	public void printErrorsAndWarnings() {
		System.out.println(file.getAbsolutePath());
		errors.forEach((e) -> System.out.println("\t\033[0;31m[ERROR]\033[0m " + e));
		warnings.forEach((w) -> System.out.println("\t\033[0;33m[WARNING]\033[0m " + w));
	}
	
	public void replaceWithPretty() {
		try {
			html.outputSettings().escapeMode(EscapeMode.xhtml);
			html.outputSettings().indentAmount(4);
			PrintWriter pw = new PrintWriter(file);
			pw.print(document.html());
			pw.close();
		} catch (IOException e) {
			System.out.println(file.getAbsolutePath() + " Pretify error: " + e.getMessage());
		}
	}
	
}
