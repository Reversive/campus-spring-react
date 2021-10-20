package ar.edu.itba.paw.models.exception;


public class DuplicateUserException extends RuntimeException {
    private final Integer fileNumber;
    private final String name;
    private final String surname;
    private final String username;
    private final String email;

    public static class Builder {
        private Integer fileNumber;
        private String name;
        private String surname;
        private String username;
        private String email;

        public Builder() {
        }

        Builder(Integer fileNumber, String name, String surname, String username, String email) {
            this.fileNumber = fileNumber;
            this.name = name;
            this.surname = surname;
            this.username = username;
            this.email = email;
        }

        public Builder withFileNumber(Integer fileNumber){
            this.fileNumber = fileNumber;
            return Builder.this;
        }

        public Builder withName(String name){
            this.name = name;
            return Builder.this;
        }

        public Builder withSurname(String surname){
            this.surname = surname;
            return Builder.this;
        }

        public Builder withUsername(String username){
            this.username = username;
            return Builder.this;
        }

        public Builder withEmail(String email){
            this.email = email;
            return Builder.this;
        }

        public DuplicateUserException build() {
            return new DuplicateUserException(this);
        }

    }

    private DuplicateUserException(Builder builder) {
        this.fileNumber = builder.fileNumber;
        this.name = builder.name;
        this.surname = builder.surname;
        this.username = builder.username;
        this.email = builder.email;
    }

    public boolean isUsernameDuplicated() {
        return this.username.isEmpty();
    }

    public boolean isFileNumberDuplicated() {
        return this.fileNumber == 0;
    }
    public boolean isEmailDuplicated() {
        return this.email.isEmpty();
    }

    public Integer getFileNumber() {
        return fileNumber;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getUsername() {
        return username;
    }

    public boolean shouldTrigger() {
        return this.isUsernameDuplicated() || this.isFileNumberDuplicated() || this.isEmailDuplicated();
    }

    public String getEmail() {
        return email;
    }
}
