package templateChecker;

import java.io.File;

public class TemplateManager {
	static final String htmlPath = "/resources/views/templates";
	static final String cssPath = "/public/css/templates";
	public CSSTemplate loadCSSTemplate(File file, String name) {
		return new CSSTemplate(this, new File(file.getAbsolutePath()
				.replace(htmlPath, cssPath)
				.replace(name + ".blade.php", name+".css")
				));
	}
}
