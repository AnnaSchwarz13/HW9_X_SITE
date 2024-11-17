package exceptions;

public class TweetException extends Exception{
    public TweetException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
