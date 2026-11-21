package web.mapper;

import domain.model.signup.SignUp;
import web.model.SignUpRequest;

public class SignUpRequestMapper {
    public SignUp toDomain(SignUpRequest signUpRequest){
        return new SignUp(signUpRequest.getLogin(), signUpRequest.getPassword());
    }

    public SignUpRequest toWeb(SignUp signUp){
        return new SignUpRequest(signUp.getLogin(), signUp.getPassword());
    }
}
