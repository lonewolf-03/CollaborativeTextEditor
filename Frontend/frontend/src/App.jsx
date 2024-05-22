import LoginSignup from "./LoginSignup/LoginSignup.jsx";
import RegisterationConfirmation from "./RegisterationConfirmation/RegisterationConfirmation.jsx";
import { Routes , Route, BrowserRouter } from "react-router-dom";
import Documents from "./Documents/Documents.jsx";
import DocumentEditor from "./DocumentEditor/DocumentEditor.jsx";
import { useState } from "react"


function App() {
  const uri = 'http://localhost:8080'; 
  
  let [jwt, setJwt] = useState('');
  let [email, setEmail] = useState('');
  let [firstName, setFirstName] = useState('');
  let [lastName, setLastName] = useState('');
  let [uid , setUid] = useState(0);

  return (
    <div>
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<LoginSignup uri={uri} jwt={jwt} setJwt={setJwt} email={email} setEmail={setEmail} firstName={firstName} setFirstName={setFirstName} lastName={lastName} setLastName={setLastName} uid={uid} setUid={setUid}/>}/>
                <Route path="/auth/confirm" element={<RegisterationConfirmation uri={uri} jwt={jwt} setJwt={setJwt} email={email} setEmail={setEmail} firstName={firstName} setFirstName={setFirstName} lastName={lastName} setLastName={setLastName} uid={uid} setUid={setUid}/>}/>
                <Route path="/documents" element={<Documents uri={uri} jwt={jwt} setJwt={setJwt} email={email} setEmail={setEmail} firstName={firstName} setFirstName={setFirstName} lastName={lastName} setLastName={setLastName} uid={uid} setUid={setUid}/>}/>
                <Route path="/edit" element={<DocumentEditor uri={uri} jwt={jwt} setJwt={setJwt} email={email} setEmail={setEmail} firstName={firstName} setFirstName={setFirstName} lastName={lastName} setLastName={setLastName} uid={uid} setUid={setUid}/>}/>
            </Routes>
        </BrowserRouter>
    </div>
  )
}

export default App;
