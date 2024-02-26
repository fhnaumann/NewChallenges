package wand555.github.io.challenges.validation;

public abstract class Validator {

    public static final Validator EMPTY_VALIDATOR = new Validator() {
        @Override
        protected ValidationResult.ValidationResultBuilder performValidation(ValidationResult.ValidationResultBuilder builder, String json) {
            return builder;
        }
    };

    private Validator andThen;

    public ValidationResult validate(ValidationResult.ValidationResultBuilder builder, String json) {
        ValidationResult.ValidationResultBuilder intermediateBuilder = performValidation(builder, json);
       if(andThen != null) {
           andThen.validate(builder, json);
       }
       return intermediateBuilder.build();
    }

    protected abstract ValidationResult.ValidationResultBuilder performValidation(ValidationResult.ValidationResultBuilder builder, String json);

    public Validator andThen(Validator andThen) {
        this.andThen = andThen;
        return andThen;
    }
}
