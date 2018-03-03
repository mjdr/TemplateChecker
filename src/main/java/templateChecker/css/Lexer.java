package templateChecker.css;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
	
	private static Map<Character, TokenType> oneCharTokens = new HashMap<>();
	
	static {
		oneCharTokens.put('#', TokenType.HASH);
		oneCharTokens.put('.', TokenType.DOT);
		oneCharTokens.put('{', TokenType.LCBR);
		oneCharTokens.put('}', TokenType.RCBR);
		oneCharTokens.put('(', TokenType.LBR);
		oneCharTokens.put(')', TokenType.RBR);
		oneCharTokens.put(';', TokenType.SEMICOLON);
		oneCharTokens.put(':', TokenType.DOUBLEDOT);
		oneCharTokens.put(',', TokenType.COMMA);
		oneCharTokens.put('@', TokenType.MAIL);
		oneCharTokens.put('!', TokenType.MARK);
		oneCharTokens.put('/', TokenType.SLACH);
		oneCharTokens.put('<', TokenType.LT);
		oneCharTokens.put('>', TokenType.GT);
		oneCharTokens.put('%', TokenType.PC);
		oneCharTokens.put('\'', TokenType.SEP);
	}
	
	private String source;
	private int index;
	
	
	
	public List<Token> tokenize(String source) {
		
		List<Token> tokens = new ArrayList<Token>();
		this.source = source;
		index = 0;
		
		while(index < source.length()) {
			if(peek() == '\n' || peek() == '\t' || peek() == ' ') {
				read();
				continue;
			}
			if(peek() == '/' && peek(1) == '/') {
				tokens.add(readOneLineComment());
				continue;
			}
			if(peek() == '/' && peek(1) == '*') {
				tokens.add(readMultiLineComment());
				continue;
			}
			if(oneCharTokens.containsKey(peek())) {
				tokens.add(new Token(oneCharTokens.get(read())));
				continue;
			}
			if(Character.isLetter(peek()) || Character.isDigit(peek()) || 
					peek() == '_' || peek() == '-') {
				tokens.add(readID());
				continue;
			}
			throw new RuntimeException("Undefiend character " + source.substring(index));
			
		}
		
		return tokens;
	}
	
	private Token readMultiLineComment() {
		String comment = "";

		read();
		read();
		while(peek() != '*' || peek(1) != '/')
			if(peek() == '\0')
				throw new RuntimeException("Unexpected oef");
			else
				comment += read();
		
		read();
		read();
		
		return new Token(TokenType.COMMENT, comment);
	}

	private Token readOneLineComment() {
		String comment = "";
		while(peek() != '\n' && peek() != '\0')
			comment += read();
		read();
		return new Token(TokenType.COMMENT, comment);
	}

	private Token readID() {
		String id = "";
		while(
				Character.isLetter(peek()) || 
				Character.isDigit(peek()) || 
				peek() == '_' || peek() == '-'
		) id += read();
		return new Token(TokenType.ID, id);
	}
	
	private char read() {
		if(index >= source.length())
			return '\0';
		return source.charAt(index++);
	}
	private char peek() {
		return peek(0);
	}
	private char peek(int i) {
		if(index + i >= source.length())
			return '\0';
		return source.charAt(index + i);
	}
}
