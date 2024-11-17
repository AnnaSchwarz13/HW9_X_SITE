package exceptions;

public class UserException extends Exception{
    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public UserException(String message) {
        super(message);
    }
}
