package templateChecker.css;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {
	
	private List<Token> tokens;
	private int index;
	private List<String> ids;
	private List<String> classes;
	private List<String> comments;
	
	
	public void parse(List<Token> tokens) {
		ids = new ArrayList<String>();
		classes = new ArrayList<String>();
		comments = new ArrayList<String>();
		this.tokens = tokens;
		index = 0;
		
		while(index < tokens.size()) {
			if(is(TokenType.COMMENT))
				comments.add(read().getText());
			readStatements();
		}
		
		
		
	}
	
	private void readStatements() {
		while(is(TokenType.MAIL) || is(TokenType.DOT) || is(TokenType.HASH) || is(TokenType.ID))
			if(is(TokenType.MAIL))
				readMediaRequest();
			else
				readStatement();
	}
	private void readStatement() {
		readSelectors();
		must(TokenType.LCBR);
		while(!is(TokenType.RCBR))
			read();
		read();
		
	}
	private void readSelectors() {
		while(true) {
			readSelector();
			if(is(TokenType.ID))
				continue;
			if(is(TokenType.COMMA) || is(TokenType.GT)) {
				read();
				continue;
			}
			break;
		}
	}
	private void readSelector() {
		String tag;
		
		
		while(is(TokenType.ID) || is(TokenType.DOT) || is(TokenType.HASH)) {
			if(is(TokenType.ID)) {
				read();
				if(is(TokenType.DOUBLEDOT)) {
					read();
					must(TokenType.ID);
					if(is(TokenType.LBR)) {
						while(!is(TokenType.RBR)) read();
						read();
					}
				}
				
			}
			if(is(TokenType.DOT)) {
				read();
				classes.add(must(TokenType.ID).getText());
				if(is(TokenType.DOUBLEDOT)) {
					read();
					must(TokenType.ID);
					if(is(TokenType.LBR)) {
						while(!is(TokenType.RBR)) read();
						read();
					}
				}
				continue;
			}
			if(is(TokenType.HASH)) {
				read();
				ids.add(must(TokenType.ID).getText());
				if(is(TokenType.DOUBLEDOT)) {
					read();
					must(TokenType.ID);
					if(is(TokenType.LBR)) {
						while(!is(TokenType.RBR)) read();
						read();
					}
				}continue;
			}
			
		}
		
		
	}
	
	private void readMediaRequest() {
		read();
		String type = must(TokenType.ID).getText();
		if(!type.equals("media")) throw new RuntimeException("Undefiend request type: " + type);
		
		readMediaRequestParams();
		read();
		readStatements();
		must(TokenType.RCBR);
		
	}
	private void readMediaRequestParams() {
		while(!is(TokenType.LCBR)) {
			readMediaRequestParam();
			if(is(TokenType.ID)) read();
		}
	}
		
	private void readMediaRequestParam() {
		must(TokenType.LBR);
		must(TokenType.ID);
		must(TokenType.DOUBLEDOT);
		must(TokenType.ID);
		must(TokenType.RBR);
		
	}
		
	

	
	private boolean is(TokenType type) {
		return peek().getType() == type;
	}
	private Token must(TokenType type) {
		if(peek().getType() != type)
			throw new RuntimeException("Unexpected token " + peek().getType() + " expected " + type);
		return read();
	}
	
	private Token read() {
		if(index >= tokens.size())
			return new Token(TokenType.EOF);
		return tokens.get(index++);
	}
	private Token peek() {
		return peek(0);
	}
	private Token peek(int i) {
		if(index + i >= tokens.size())
			return new Token(TokenType.EOF);
		return tokens.get(index + i);
	}
	
	public List<String> getIds() {
		return ids.stream().distinct().collect(Collectors.toList());
	}
	public List<String> getClasses() {
		return classes.stream().distinct().collect(Collectors.toList());
	}
	public List<String> getComments() {
		return comments;
	}
}
