package templateChecker.css;

public class Token {
	private TokenType type;
	private String text;
	
	@Override
	public String toString() {
		return "{" + type.name() + ":" + text + "}";
	}

	public Token(TokenType type, String text) {
		this.type = type;
		this.text = text;
	}

	public Token(TokenType type) {
		this(type, null);
	}
	
	public String getText() {
		return text;
	}
	public TokenType getType() {
		return type;
	}
	
	
}
