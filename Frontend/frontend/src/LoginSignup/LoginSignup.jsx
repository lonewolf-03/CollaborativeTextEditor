import './LoginSignup.css';
import { useContext, useState } from 'react';
import { useNavigate } from 'react-router';

const LoginSignup = (props) => {

    const [isLoading, setIsLoading] = useState(false);
    const [loginEmail , setLoginEmail] = useState('');
    const [loginPassword , setLoginPassword] = useState('');
    const [password , setPassword] = useState('');
    const [confirmedPassword, setConfirmedPassword] = useState('');
    const [registerEmail , setRegisterEmail] = useState('');
    const [firstName , setFirstName] = useState('');
    const [lastName , setLastName] = useState('');
    const navigator = useNavigate();

   const registrationHandler = async () =>{
    const data = {
        firstName : firstName,
        lastName : lastName,
        email : registerEmail,
        password : password,
        confirmedPassword : confirmedPassword
    }

    try{
        let res = await fetch(props.uri + '/register', {
            method : "POST",
            headers : { 'Content-Type' : 'application/json' },
            body : JSON.stringify(data)        
        });

        if(!res.ok){
            let resBody = await res.json();
            let message = await resBody.message;
            throw Error(message);
        }

        
        let resBody = await res.json();
        let message = await resBody.message;

        setIsLoading(false);
        alert(message);
        setTimeout(() => {navigator("/auth/confirm" , { state : { email : registerEmail} })}, 1000);
        setFirstName('');
        setLastName('');
        setRegisterEmail('');
        setConfirmedPassword('');
        setPassword('');

    } catch(error){
        setFirstName('');
        setLastName('');
        setRegisterEmail('');
        setConfirmedPassword('');
        setPassword('');
        setIsLoading(false);
        alert(error.message);
    }
}



    const loginHandler = async () => {
        const data = {
            email : loginEmail,
            password : loginPassword
        };
    
        try{
            let res = await fetch(props.uri + '/login', {
                method : "POST",
                headers : { 'Content-Type' : 'application/json' },
                body : JSON.stringify(data)        
            });

            
            

            if(!res.ok){
                let resBody = await res.json();
                let message = await resBody.message;
                console.log(message);
                throw Error(message);
            }
            

            let resBody = await res.json();
            let message = await resBody.message;

            let jwt = resBody.body.token;
            let email = resBody.body.email;
            let uid = resBody.body.uid;
            let firstName = resBody.body.firstName;
            let lastName = resBody.body.lastName;
            
            props.setJwt(jwt);
            props.setEmail(email);
            props.setUid(uid);
            props.setFirstName(firstName);
            props.setLastName(lastName);
            setLoginEmail('');
            setLoginPassword('');
            setIsLoading(false);
            alert(message);
            setTimeout(() => {navigator("/documents")}, 3000);

        } catch(error){
            setLoginEmail('');
            setLoginPassword('');
            setIsLoading(false);
            alert(error.message);
        }
    }

    const loginBtn = ()=>{
        const loginText = document.querySelector(".title-text .login");
        const loginForm = document.querySelector("form.login");
        loginForm.style.marginLeft = "0%";
        loginText.style.marginLeft = "0%";
    };

    const signupBtn = ()=>{
        const loginText = document.querySelector(".title-text .login");
        const loginForm = document.querySelector("form.login");
        loginForm.style.marginLeft = "-50%";
        loginText.style.marginLeft = "-50%";
    };
    
  
    return ((isLoading)?<div>Loading...</div>:
        <div>
            <div class="wrapper">
                <div class="title-text">
                    <div class="title login">Login Form</div>
                    <div class="title signup">Signup Form</div>
                </div>
                <div class="form-container">
                    <div class="slide-controls">
                        <input type="radio" name="slide" id="login" onClick={loginBtn} checked/>
                        <input type="radio" name="slide" id="signup" onClick={signupBtn}/>
                        <label for="login" class="slide login" >Login</label>
                        <label for="signup" class="slide signup" >Signup</label>
                        <div class="slider-tab"></div>
                    </div>
                    <div class="form-inner">
                        <form action="#" class="login">
                            <div class="message-box" id="login-message-box">
                                <span class="message" id="login-message"></span>
                                <span class="close-btn" onClick="closeMessageBox('login')">×</span>
                            </div>
        
                            <div class="field">
                                <input type="text" placeholder="Email Address" onChange={(e) => {setLoginEmail(e.target.value)}} value={loginEmail} required/>
                            </div>
                            <div class="field">
                                <input type="password" placeholder="Password" onChange={(e) => {setLoginPassword(e.target.value)}} value={loginPassword} required/>
                            </div>
                            <div class="field btn">
                                <div class="btn-layer"></div>
                                <input type="submit" value="Login" onClick={loginHandler}/>
                            </div>
                        </form>
                        <form action="#" class="signup">
                            <div class="message-box" id="signup-message-box">
                                <span class="message" id="signup-message"> </span>
                                <span class="close-btn" onClick="closeMessageBox('signup')">×</span>
                            </div>
        
                            <div class="field">
                                <input type="text" id="first-name" placeholder="First name" onChange={(e) => {setFirstName(e.target.value)}} value={firstName} required/>
                            </div>
                            <div class="field">
                                <input type="text" id="last-name" placeholder="Last name" onChange={(e) => {setLastName(e.target.value)}} value={lastName} required/>
                            </div>
                            <div class="field">
                                <input type="text" placeholder="Email Address" onChange={(e) => {setRegisterEmail(e.target.value)}} value={registerEmail} required/>
                            </div>
                            <div class="field">
                                <input type="password" id="password" placeholder="Password" onChange={(e) => {setPassword(e.target.value)}} value={password} required/>
                            </div>
                            <div class="field">
                                <input type="password" id="confirm-password" placeholder="Confirm password" onChange={(e) => {setConfirmedPassword(e.target.value)}} value={confirmedPassword} required/>
                            </div>
                            <div class="field btn">
                                <div class="btn-layer"></div>
                                <input type="submit" value="Signup" id="signup-button" onClick={registrationHandler}/>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default LoginSignup;