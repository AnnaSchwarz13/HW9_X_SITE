package exceptions;

public class TagException extends Exception{
    public TagException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
