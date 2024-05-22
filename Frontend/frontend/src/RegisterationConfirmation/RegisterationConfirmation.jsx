import { useState } from "react";
import { useNavigate } from "react-router";
import "./RegisterationConfirmation.css";
import { useLocation } from "react-router";
import { useRef } from "react";

const RegisterationConfirmation = (props) => {

    const navigator = useNavigate();
    const [isLoading, setIsLoading] = useState(false);
    const [confirmationToken, setConfirmationToken] = useState('');
    const location = useLocation();
    console.log(location);
    const regemail = useRef(location.state?.email)
    
    
    const confirmationHandler = async () =>{
        

        const data = {
            email : regemail.current,
            token : confirmationToken
        };

        console.log(data);
        try{
            let res = await fetch(props.uri + '/register/confirm', {
                method : 'POST',
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
            console.log("reached here");
            alert(message);
            setTimeout(() => {navigator("/")}, 300);
        } catch(error){
            setIsLoading(false);
            if(error.message === "Expired token"){
                alert(error.message);
                setTimeout(() => {navigator(-1)}, 3000);
            }
            alert(error.message);
        }
    }




    
    

  
    return (
    <div>
    <div class="wrapper">
        <div class="title-text">
            <div class="title login">Confirm registeration</div>
        </div>
        <div class="form-container">
            {/* <div class="slide-controls">
                <input type="radio" name="slide" id="login" onClick={loginBtn} checked/>
                <input type="radio" name="slide" id="signup" onClick={signupBtn}/>
                <label for="login" class="slide login" >Login</label>
                <label for="signup" class="slide signup" >Signup</label>
                <div class="slider-tab"></div>
            </div> */}
            <div class="form-inner">
                <form action="#" class="login">
                    <div class="field">
                        <input type="text" placeholder="Confirmation token" onChange={(e) => {setConfirmationToken(e.target.value)}} value={confirmationToken}required/>
                    </div>
                    <div class="field btn">
                        <div class="btn-layer"></div>
                        <input type="submit" value="confirm" onClick={confirmationHandler}/>
                    </div>
                </form>
                {/* <form action="#" class="login">
                    <div class="message-box" id="login-message-box">
                        <span class="message" id="login-message"></span>
                        <span class="close-btn" onClick="closeMessageBox('login')">×</span>
                    </div>

                    <div class="field">
                        <input type="text" placeholder="Email Address" onChange={(e) => {setLoginEmail(e.target.value)}} required/>
                    </div>
                    <div class="field">
                        <input type="password" placeholder="Password" onChange={(e) => {setLoginPassword(e.target.value)}} required/>
                    </div>
                    <div class="field btn">
                        <div class="btn-layer"></div>
                        <input type="submit" value="Login"/>
                    </div>
                </form> */}
                {/* <form action="#" class="signup">
                    <div class="message-box" id="signup-message-box">
                        <span class="message" id="signup-message"> </span>
                        <span class="close-btn" onClick="closeMessageBox('signup')">×</span>
                    </div>

                    <div class="field">
                        <input type="text" id="first-name" placeholder="First name" onChange={(e) => {setFirstName(e.target.value)}} required/>
                    </div>
                    <div class="field">
                        <input type="text" id="last-name" placeholder="Last name" onChange={(e) => {setLastName(e.target.value)}} required/>
                    </div>
                    <div class="field">
                        <input type="text" placeholder="Email Address" onChange={(e) => {setRegisterEmail(e.target.value)}} required/>
                    </div>
                    <div class="field">
                        <input type="password" id="password" placeholder="Password" onChange={(e) => {setPassword(e.target.value)}} required/>
                    </div>
                    <div class="field">
                        <input type="password" id="confirm-password" placeholder="Confirm password" onChange={(e) => {setConfirmedPassword(e.target.value)}}required/>
                    </div>
                    <div class="field btn">
                        <div class="btn-layer"></div>
                        <input type="submit" value="Signup" id="signup-button" onClick={submitssignup}/>
                    </div>
                </form> */}
            </div>
        </div>
    </div>
</div>
    )
}

export default RegisterationConfirmation;